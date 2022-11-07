package org.joisen.sparkcore.rdd.partition

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:17 2022/11/7 
 */
// 自定义分区规则
object Spark01_RDD_Part {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val rdd: RDD[(String, String)] = sc.makeRDD(List(
      ("nba", "xxxxxxxx"),
      ("wnba", "xxxxxxxx"),
      ("cba", "xxxxxxxx"),
      ("nba", "xxxxxxxx")
    ), 3)

    val partRDD: RDD[(String, String)] = rdd.partitionBy(new MyPartitioner)

    partRDD.saveAsTextFile("output")



    // 关闭连接
    sc.stop()
  }

  /**
   * 自定义分区器
   */
  class MyPartitioner extends Partitioner{
    // 分区数量
    override def numPartitions: Int = 3

    // 根据数据的key值返回数据的分区索引（从0开始）
    override def getPartition(key: Any): Int = {
      key match {
        case "nba" => 0
        case "wnba" => 1
        case _ => 2
      }
    }
  }

}
