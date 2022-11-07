package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark02_RDD_File_Par {
  def main(args: Array[String]): Unit = {
    //todo 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc: SparkContext = new SparkContext(sparkConf)
    //todo 创建RDD
    // textFile 可以将文件作为数据处理的数据源，默认也可以设定分区
    // minPartitions:最小分区数量
    // math.min(defaultParallelism, 2)
//    val rdd: RDD[String] = sc.textFile("datas/1.txt") //产生两个分区
    // 可以通过第二个参数指定分区数，  spark读取文件，底层使用的就是hadoop的读取方式
    // 真实产生的分区可能比设置的多
    /** 分区数量计算方式
     *  totalSize : 文件的大小（字节）例如为7
     *  goalSize = 7 / 2(minPartitions) = 3 (byte) ... 1
     *  多的1个字节 看是否超过1.1倍（hadoop中分区的计算方式）超过则再产生一个分区
     */
    val rdd: RDD[String] = sc.textFile("datas/1.txt",3)

    rdd.saveAsTextFile("output")
    //todo 关闭环境
    sc.stop()

  }

}
