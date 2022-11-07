package org.joisen.sparkcore.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 12:29 2022/11/2 
 */
object Spark03_WordCount {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)
    // 执行业务操作
    // 1 读取文件，获取一行一行的数据
    val lines: RDD[String] = sc.textFile("datas")
    // 2 将一行数据进行拆分，形成一个一个的单词
    // 将数据扁平化
    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String, Int)] = words.map(
      word => (word, 1)
    )
    // spark框架提供了更多的功能，可以将分组和聚合使用一个方法实现
    // reduceByKey：相同key的数据可以对value进行聚合
    val wordToCount: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)


    // 5 将转换结果采集到控制台打印出来
    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)
    // 关闭连接
    sc.stop()

  }
}
