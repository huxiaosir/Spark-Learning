package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark02_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子-mapPartitions
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    // mapPartitions 可以以分区为单位进行数据转换操作，
    //                但是会将整个分区的数据加载到内存进行引用
    //                处理完的数据是不会被释放，存在对象的引用
    //                在内存较小，而数据量较大时 容易出现内存溢出
    val mpRdd: RDD[Int] = rdd.mapPartitions(
      iter => {
        println(">>>>>>>>")
        iter.map(_ * 2)
      }
    )
    mpRdd.collect().foreach(println)

    sc.stop()

  }

}
