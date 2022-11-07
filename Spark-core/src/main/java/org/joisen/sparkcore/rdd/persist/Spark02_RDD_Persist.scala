package org.joisen.sparkcore.rdd.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 10:14 2022/11/7 
 */
object Spark02_RDD_Persist {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val list: List[String] = List("hello spark", "hello scala")
    val rdd: RDD[String] = sc.makeRDD(list)
    val flatRdd: RDD[String] = rdd.flatMap(_.split(" "))
    val mapRdd: RDD[(String, Int)] = flatRdd.map(word =>{
      println("***+***")
      (word, 1)
    })
    // cache默认持久化的操作，只能将数据保存到内存中
    // 持久化操作必须在行动算子执行时完成的
    // RDD对象的持久化操作不一定是为了重用
    // 在数据执行时间较长，或数据比较重要的场合也可以采用持久化操作
//    mapRdd.cache()
    mapRdd.persist(StorageLevel.DISK_ONLY)

    val reduceRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)
    reduceRdd.collect().foreach(println)

    println("************************")

    /**
     * RDD 中不存储数据，
     * 如果一个RDD需要重复使用，那么需要从头再次执行来获取数据
     * RDD对象可以重复使用，但是数据无法重复使用
     */

    val groupRdd: RDD[(String, Iterable[Int])] = mapRdd.groupByKey()
    groupRdd.collect().foreach(println)



    // 关闭连接
    sc.stop()


  }
}
