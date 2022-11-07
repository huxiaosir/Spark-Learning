package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark04_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子-flatMap
    val rdd: RDD[String] = sc.makeRDD(List(
      "hello scala","hello spark"
    ))
    val flatRdd: RDD[String] = rdd.flatMap(
      s => {
        s.split(" ")
      }
    )

    flatRdd.collect().foreach(println)

    sc.stop()

  }

}
