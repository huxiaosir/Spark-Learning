package org.joisen.sparkcore.rdd.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 10:14 2022/11/7 
 */
object Spark04_RDD_Persist {
  def main(args: Array[String]): Unit = {
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("cp")

    val list: List[String] = List("hello spark", "hello scala")
    val rdd: RDD[String] = sc.makeRDD(list)
    val flatRdd: RDD[String] = rdd.flatMap(_.split(" "))
    val mapRdd: RDD[(String, Int)] = flatRdd.map(word =>{
      println("***+***")
      (word, 1)
    })

    // checkpoint 需要落盘，需要指定检查点保存路径
    // 检查点路径保存的文件，当作业执行完毕后，不会被删除
    // 一般保存路径为分布式存储路径：HDFS
    mapRdd.checkpoint()


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
