package org.joisen.sparkcore.requirement

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:25 2022/11/8 
 */
object Spark02_Req1_HotCategoryTop10Analysis1 {
  def main(args: Array[String]): Unit = {
    // todo top10 热门品类
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    // todo 问题1 rdd重复使用
    // todo 问题2 cogroup性能可能较低

    // 1 读取原始日志数据
    val rdd: RDD[String] = sc.textFile("datas/user_visit_action.txt")
    rdd.cache()
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
    /*
     数据格式 （品类ID，点击数量），（品类ID，下单数量），（品类ID，支付数量）
     cogroup===》 （品类ID，（点击数量，下单数量，支付数量））
     另一种方式 （品类ID，点击数量） =》（品类ID，（点击数量，0，0））
                （品类ID，下单数量）=》（品类ID，（0，下单数量，0））
                （品类ID，支付数量）=》（品类ID，（0，0，支付数量））

     */

    // 5 将品类进行排序 并且取前10名
    //    点击数量排序，下单数量排序，支付数量排序
    //    元组排序，先比较第一个，再比较第二个。。。
    //    （品类ID，（点击数量，下单数量，支付数量））
    // join、zip、leftOuterJoin、cogroup都可以实现将不同数据源连接
    // join操作需要不同数据源有相同的key才能操作
    // 只有cogroup能实现该需求
    // todo cogroup可能存在shuffle
    val rdd1: RDD[(String, (Int, Int, Int))] = clickCountRDD.map {
      case (cid, cnt) => {
        (cid, (cnt, 0, 0))
      }
    }

    val rdd2: RDD[(String, (Int, Int, Int))] = orderCountRDD.map {
      case (cid, cnt) => {
        (cid, ( 0,cnt, 0))
      }
    }

    val rdd3: RDD[(String, (Int, Int, Int))] = payCountRDD.map {
      case (cid, cnt) => {
        (cid, (0, 0, cnt))
      }
    }

    // 将三个数据源合并在一起，统一进行聚合计算
    val sourceRDD: RDD[(String, (Int, Int, Int))] = rdd1.union(rdd2).union(rdd3)

    val analysisRDD: RDD[(String, (Int, Int, Int))] = sourceRDD.reduceByKey(
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
      }
    )
    val resultRDD: Array[(String, (Int, Int, Int))] = analysisRDD.sortBy(_._2, false).take(10)


    // 6 输出结果
    resultRDD.foreach(println)


    // 关闭连接
    sc.stop()
  }
}
