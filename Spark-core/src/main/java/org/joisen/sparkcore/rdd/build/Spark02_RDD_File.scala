package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark02_RDD_File {
  def main(args: Array[String]): Unit = {
    // 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    // 创建RDD
    // 从文件中创建RDD， 将文件中集合的数据作为处理的数据源
    // path路径默认以当前环境的根路径（最外层，本项目中是Spark-Learning）为基准，可以谢绝对路径也可以写相对路径
//    val rdd: RDD[String] = sc.textFile("datas/1.txt")

    // path路径可以是文件的具体路径，也可以是目录的名称
//    val rdd: RDD[String] = sc.textFile("datas")

    // path路径还可以使用通配符
    val rdd: RDD[String] = sc.textFile("datas/1*.txt")

    // path路径还可以是分布式存储系统路径：HDFS
//    val rdd: RDD[String] = sc.textFile("hdfs://hadoop102:8020/test.txt")

    rdd.collect().foreach(println)
    // 关闭环境
    sc.stop()

  }

}
