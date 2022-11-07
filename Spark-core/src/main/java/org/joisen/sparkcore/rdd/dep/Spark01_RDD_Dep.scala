package org.joisen.sparkcore.rdd.dep

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @author : joisen 
 * @date : 9:23 2022/11/7 
 */
object Spark01_RDD_Dep {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val lines: RDD[String] = sc.textFile("datas/word.txt")
    println(lines.dependencies)
    println("-=-=-=-=-=-=-=-=-=-=-=-=")

    val words: RDD[String] = lines.flatMap(_.split(" "))
    println(words.dependencies)
    println("-=-=-=-=-=-=-=-=-=-=-=-=")

    val wordToOne: RDD[(String, Int)] = words.map(
      word => (word, 1)
    )
    println(wordToOne.dependencies)
    println("-=-=-=-=-=-=-=-=-=-=-=-=")

    val wordToSum: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)
    println(wordToSum.dependencies)
    println("-=-=-=-=-=-=-=-=-=-=-=-=")

    val array: Array[(String, Int)] = wordToSum.collect()
    array.foreach(println)
    // 关闭连接
    sc.stop()
  }
}
