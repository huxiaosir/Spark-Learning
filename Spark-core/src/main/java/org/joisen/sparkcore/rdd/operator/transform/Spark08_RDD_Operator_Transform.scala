package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark08_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- sample
    val rdd:RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6,7,8,9,10))
    // sample算子需要传递3个参数
    // 1 第一个参数表示抽取数据后是否将数据返回 true(放回，即放回数据源中，可以被再次抽取)，false(丢弃)
    // 2 第二个参数表示数据源中每条数据被抽取的概率, 基准值的概念
    // 3 第三个参数表示抽取数据时随机算法的种子，如果不使用第三个参数，则使用的是当前系统时间
//    println(rdd.sample(
//      false, 0.4
//    ).collect().mkString(","))
    println(rdd.sample(
      true, 2
    ).collect().mkString(","))

    sc.stop()

  }

}
