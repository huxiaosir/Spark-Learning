package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark012_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- sortby
    val rdd:RDD[Int] = sc.makeRDD(List(6,2,4,5,3,1),2)

    val newRdd: RDD[Int] = rdd.sortBy(num => num)

    newRdd.saveAsTextFile("output")


    sc.stop()

  }

}
