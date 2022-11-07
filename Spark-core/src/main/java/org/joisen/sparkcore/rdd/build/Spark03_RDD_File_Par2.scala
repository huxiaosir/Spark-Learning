package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark03_RDD_File_Par2 {
  def main(args: Array[String]): Unit = {
    //todo 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    //todo 创建RDD

    // word.txt => 14 byte     14 / 2 = 7 => 2个分区

    /**
     * 1234567@@  => 0 1 2 3 4 5 6 7 8
     * 89@@       => 9 10 11 12
     * 0@@        => 13
     *
     * [0,7]  => 1234567
     * [7,14] => 890
     */
      // 如果数据源为多个文件，那么计算分区时以文件为单位进行分区
    val rdd: RDD[String] = sc.textFile("datas/word.txt",2)

    rdd.saveAsTextFile("output")
    //todo 关闭环境
    sc.stop()

  }

}
