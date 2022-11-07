package org.joisen.sparkcore.rdd.io

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:30 2022/11/7 
 */
object Spark01_RDD_IO_Load {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("IO")
    val sc = new SparkContext(conf)

    val rdd1: RDD[String] = sc.textFile("output1")
    println(rdd1.collect().mkString(","))

    val rdd2: RDD[(String,Int)] = sc.objectFile[(String, Int)]("output2")
    println(rdd2.collect().mkString(","))


    val rdd3: RDD[(String, Int)] = sc.sequenceFile[String, Int]("output3")
    println(rdd3.collect().mkString(","))






    sc.stop()
  }

}
