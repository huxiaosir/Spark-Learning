package org.joisen.sparkcore.rdd.build

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:20 2022/11/3 
 */
object Spark01_RDD_Memory_Par {
  def main(args: Array[String]): Unit = {
    // 准备环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    sparkConf.set("spark.default.parallelism","5")
    val sc: SparkContext = new SparkContext(sparkConf)
    // 创建RDD
    // RDD的并行度 & 分区
    // makeRDD方法可以传递第二个参数，这个参数表示分区的数量
    // 第二个参数可以不传递，则使用默认值defaultParallelism
    // spark在默认情况下，从配置对象中获取配置参数：spark.default.parallelism
    // 如果获取不到则使用totalCores属性，这个属性为当前运行环境的最大可用核数
//    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    // 将处理的数据保存成分区文件，
    rdd.saveAsTextFile("output") // 路径不能提前存在
//    rdd.collect().foreach(println)

    // 关闭环境
    sc.stop()

  }

}
