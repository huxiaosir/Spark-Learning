package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark05_RDD_Operator_Transform_Test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- glom
    val rdd = sc.makeRDD(List(1,2,3,4),2)
    // 求出两个分区中的最大值，并相加
    val glomRdd: RDD[Array[Int]] = rdd.glom()
    val maxRdd: RDD[Int] = glomRdd.map(
      array => {
        array.max
      }
    )
    println(maxRdd.collect().sum)

    sc.stop()

  }

}
