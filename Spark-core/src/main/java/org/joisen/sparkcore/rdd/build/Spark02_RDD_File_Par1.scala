package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark02_RDD_File_Par1 {
  def main(args: Array[String]): Unit = {
    //todo 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    //todo 创建RDD
    //todo 数据分区的分配
    // 1：数据以行为单位进行读取 spark读取文件，采用的是hadoop的方式读取，所以一行一行的读取，和字节数没有关系
    // 2：数据读取时以偏移量为单位,偏移量不会被重复读
    /** 1@@ => 012
     * 2@@ => 345
     * 3   => 6
     */
    // 3：数据分区的偏移量范围计算
    /** 0 => [0,3] => 12
     *  1 => [3,6] => 3
     *  2 => [6,7] =>
     */
    val rdd: RDD[String] = sc.textFile("datas/1.txt",3)

    rdd.saveAsTextFile("output")
    //todo 关闭环境
    sc.stop()

  }

}
