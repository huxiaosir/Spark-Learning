package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark02_RDD_File1 {
  def main(args: Array[String]): Unit = {
    // 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    // 创建RDD
    // 从文件中创建RDD， 将文件中集合的数据作为处理的数据源
    // textFile：以行为单位来读取数据，读取的数据都是字符串
    // wholeTextFile：以文件为单位读取数据  读取的结果为元组，第一个元素为文件路径，第二个元素为文件内容
    val rdd: RDD[(String, String)] = sc.wholeTextFiles("datas")

    rdd.collect().foreach(println)


    // 关闭环境
    sc.stop()

  }

}
