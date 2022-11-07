package org.joisen.sparkcore.rdd.operator.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark04_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 1, 1, 4))
    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 1), ("a", 1), ("b", 1), ("b", 1)
    ))
    // TODO 行动算子
//    val intToLong: collection.Map[Int, Long] = rdd.countByValue()
//    println(intToLong)

    val stringToLong: collection.Map[String, Long] = rdd1.countByKey()
    println(stringToLong)





    sc.stop()

  }

}
