package org.joisen.sparkcore.rdd.persist

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @author : joisen 
 * @date : 10:14 2022/11/7 
 */
object Spark01_RDD_Persist {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val list: List[String] = List("hello spark", "hello scala")
    val rdd: RDD[String] = sc.makeRDD(list)
    val flatRdd: RDD[String] = rdd.flatMap(_.split(" "))
    val mapRdd: RDD[(String, Int)] = flatRdd.map((_, 1))
    val reduceRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)
    reduceRdd.collect().foreach(println)

    println("************************")

    val list1: List[String] = List("hello spark", "hello scala")
    val rdd1: RDD[String] = sc.makeRDD(list1)
    val flatRdd1: RDD[String] = rdd1.flatMap(_.split(" "))
    val mapRdd1: RDD[(String, Int)] = flatRdd1.map((_, 1))
    val groupRdd: RDD[(String, Iterable[Int])] = mapRdd1.groupByKey()
    groupRdd.collect().foreach(println)



    // 关闭连接
    sc.stop()


  }
}
