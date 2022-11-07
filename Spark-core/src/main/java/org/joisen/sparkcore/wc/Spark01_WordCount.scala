package org.joisen.sparkcore.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 12:29 2022/11/2 
 */
object Spark01_WordCount {
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
    // 3 将数据根据单词进行分组，便于统计
    val wordGroup: RDD[(String, Iterable[String])] = words.groupBy(word => word)
    // 4 对分组后的数据进行转换（word,word,word）-> (word,count)
    val wordToCount = wordGroup.map {
      case (word, list) => {
        (word, list.size)
      }
    }
    // 5 将转换结果采集到控制台打印出来
    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)
    // 关闭连接
    sc.stop()

  }
}
