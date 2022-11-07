package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark01_RDD_Memory_Par1 {
  def main(args: Array[String]): Unit = {
    // 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    // 创建RDD
      // 【1，2】，【3，4】
//    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    // 【1】，【2】，【3，4】
//    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),3)
    // 【1】，【2，3】，【4，5】
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4,5),3)
    // 数据切分：
    /**
     * def positions(length: Long, numSlices: Int): Iterator[(Int, Int)] = {
     * (0 until numSlices).iterator.map { i =>
     * val start = ((i * length) / numSlices).toInt
     * val end = (((i + 1) * length) / numSlices).toInt
     * (start, end)
     * }
     * }
     */
    // 将处理的数据保存成分区文件，
    rdd.saveAsTextFile("output") // 路径不能提前存在
//    rdd.collect().foreach(println)

    // 关闭环境
    sc.stop()

  }

}
