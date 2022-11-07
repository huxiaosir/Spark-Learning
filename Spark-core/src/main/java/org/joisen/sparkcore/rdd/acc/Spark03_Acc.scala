package org.joisen.sparkcore.rdd.acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:41 2022/11/7 
 */
object Spark03_Acc {
  def main(args: Array[String]): Unit = {

    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("ACC")
    val sc = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))



    // 获取系统累加器
    // spark默认提供了简单数据聚合的累加器
    val sumAcc: LongAccumulator = sc.longAccumulator("sum")
//    sc.doubleAccumulator
//    sc.collectionAccumulator

    // 使用map输出结果为0， 出现少加情况
    // 少加： 转换算子中调用累加器，如果没有行动算子的话，那么不会执行
    // 多加： 多次调用collect会重复执行操作，导致多加
    // 一般情况下，累加器会放置在行动算子中进行操作
    val mapRdd: RDD[Int] = rdd.map(
      num => {
        // 使用累加器
        sumAcc.add(num)
        num
      }
    )
    mapRdd.collect()
    mapRdd.collect()

    // 获取累加器的值
    println(sumAcc.value)


    sc.stop()
  }

}
