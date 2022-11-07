package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark07_RDD_Operator_Transform_Test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- filter
    val rdd: RDD[String] = sc.textFile("datas/apache.log")
    rdd.filter(
      line => {
        val datas = line.split(" ")
        val time: String = datas(3)
        time.startsWith("17/05/2015")
      }
    ).collect().foreach(println)



    sc.stop()

  }

}
