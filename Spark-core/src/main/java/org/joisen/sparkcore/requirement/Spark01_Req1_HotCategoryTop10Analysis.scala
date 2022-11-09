package org.joisen.sparkcore.requirement

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @author : joisen 
 * @date : 9:25 2022/11/8 
 */
object Spark01_Req1_HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {
    // todo top10 热门品类
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    // 1 读取原始日志数据
    val rdd: RDD[String] = sc.textFile("datas/user_visit_action.txt")

    // 2 统计品类的点击数量：（品类id，点击数量） 过滤掉其中不是点击行为的数据
    val clickActionRDD: RDD[String] = rdd.filter(
      action => {
        val datas: Array[String] = action.split("_")
        datas(6) != "-1"
      }
    )

    val clickCountRDD: RDD[(String, Int)] = clickActionRDD.map(
      action => {
        val datas: Array[String] = action.split("_")
        (datas(6), 1)
      }
    ).reduceByKey(_ + _)


    // 3 统计品类的下单数量：（品类id，下单数量） 过滤掉其中不是下单的数据
    val orderActionRDD: RDD[String] = rdd.filter(
      action => {
        val datas: Array[String] = action.split("_")
        datas(8) != "null"
      }
    )
    // 将下单的品类id集 扁平化
    val orderCountRDD: RDD[(String, Int)] = orderActionRDD.flatMap(
      action => {
        val datas: Array[String] = action.split("_")
        val cid: String = datas(8)
        val cids: Array[String] = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)

    // 4 统计品类的支付数量：（品类id，支付数量） 过滤掉其中不是支付的数据
    val payActionRDD: RDD[String] = rdd.filter(
      action => {
        val datas: Array[String] = action.split("_")
        datas(10) != "null"
      }
    )
    // 将支付的品类id集 扁平化
    val payCountRDD: RDD[(String, Int)] = payActionRDD.flatMap(
      action => {
        val datas: Array[String] = action.split("_")
        val cid: String = datas(10)
        val cids: Array[String] = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)

    // 5 将品类进行排序 并且取前10名
    //    点击数量排序，下单数量排序，支付数量排序
    //    元组排序，先比较第一个，再比较第二个。。。
    //    （品类ID，（点击数量，下单数量，支付数量））
    // join、zip、leftOuterJoin、cogroup都可以实现将不同数据源连接
    // join操作需要不同数据源有相同的key才能操作
    // 只有cogroup能实现该需求
    val cogroupRDD: RDD[(String, (Iterable[Int], Iterable[Int], Iterable[Int]))] = clickCountRDD.cogroup(orderCountRDD, payCountRDD)

    val analysisRDD: RDD[(String, (Int, Int, Int))] = cogroupRDD.mapValues {
      case (clickIter, orderIter, payIter) => {
        var clickCnt = 0
        val iter1: Iterator[Int] = clickIter.iterator
        if (iter1.hasNext) {
          clickCnt = iter1.next()
        }
        var orderCnt = 0
        val iter2: Iterator[Int] = orderIter.iterator
        if (iter2.hasNext) {
          orderCnt = iter2.next()
        }
        var payCnt = 0
        val iter3: Iterator[Int] = payIter.iterator
        if (iter3.hasNext) {
          payCnt = iter3.next()
        }

        (clickCnt, orderCnt, payCnt)
      }
    }

    val resultRDD: Array[(String, (Int, Int, Int))] = analysisRDD.sortBy(_._2, false).take(10)


    // 6 输出结果
    resultRDD.foreach(println)


    // 关闭连接
    sc.stop()
  }
}
