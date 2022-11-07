package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark01_RDD_Operator_Transform_Par {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子-map
    /** 1 rdd的计算一个分区内的数据是一个一个执行逻辑
     *    只有前面一个数据全部的逻辑执行完毕后，才会执行下一个数据
     *    分区内数据的执行是有序的
     *  2 不同分区数据计算是无序的
     */
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),1)

    val mapRdd: RDD[Int] = rdd.map(
      num => {
        println("num = " + num)
        num
      }
    )
    val mapRdd1: RDD[Int] = mapRdd.map(
      num => {
        println("num >> " + num)
        num
      }
    )

    mapRdd1.collect()

    sc.stop()

  }

}
