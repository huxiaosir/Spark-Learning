package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark01_RDD_Memory {
  def main(args: Array[String]): Unit = {
    // 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    // 创建RDD
    // 从内存中创建RDD， 将内存中集合的数据作为处理的数据源
    val seq: Seq[Int] = Seq[Int](1, 2, 3, 4)
    // val rdd: RDD[Int] = sc.parallelize(seq)
    // makeRDD等同于parallelize方法
    val rdd: RDD[Int] = sc.makeRDD(seq)

    rdd.collect().foreach(println)

    // 关闭环境
    sc.stop()

  }

}
