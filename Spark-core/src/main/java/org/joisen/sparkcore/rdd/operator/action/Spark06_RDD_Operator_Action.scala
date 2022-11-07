package org.joisen.sparkcore.rdd.operator.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark06_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 4), ("b", 1)
    ),2)
    // TODO 行动算子  foreach
    // foreach: 其实时Driver端内存集合的循环遍历方法
    rdd.collect().foreach(println)
    println("-------------------")
    // foreach：是Executor端内存数据打印
    rdd.foreach(println)



    sc.stop()

  }

}
