package org.joisen.sparkcore.rdd.serial

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:04 2022/11/5 
 */
object Spark01_RDD_Serial {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(Array("hello Word", "hello Scala", "hello Spark", "hive"))

    val search = new Search("h")

    search.getMatch1(rdd).collect().foreach(println)


    sc.stop()

  }

  // 查询方法
  // 类的构造参数其实是类的属性， 构造参数需要进行闭包检测，其实就等同于类进行闭包检测
  class Search(query: String) extends Serializable {

    def isMatch(s: String): Boolean = {
      s.contains(query)
    }
    // 函数序列化案例
    def getMatch1 (rdd: RDD[String]): RDD[String] = {
      rdd.filter(isMatch)
    }
    // 属性序列化案例
    def getMatch2(rdd: RDD[String]): RDD[String] = {
      rdd.filter(x => x.contains(query))

    }

  }
}
