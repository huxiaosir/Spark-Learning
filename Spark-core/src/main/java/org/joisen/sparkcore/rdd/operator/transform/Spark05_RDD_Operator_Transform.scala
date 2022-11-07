package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark05_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- glom
    val rdd = sc.makeRDD(List(1,2,3,4),2)

    val glomRdd: RDD[Array[Int]] = rdd.glom()


    glomRdd.collect().foreach(data=> println(data.mkString(",")))

    sc.stop()

  }

}
