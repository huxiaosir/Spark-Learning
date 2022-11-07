package org.joisen.sparkcore.rdd.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 10:14 2022/11/7 
 */
object Spark05_RDD_Persist {
  def main(args: Array[String]): Unit = {
    /**
     * cache: 将数据临时存储在内存中 进行数据重用
     * persist: 将数据临时存储在磁盘文件中进行数据重用
     *          涉及到磁盘IO，性能较低，但是数据安全
     *           如果作业执行完毕临时保存的数据文件就会丢失
     * checkpoint: 将数据长久的保存在磁盘中，进行数据重用
     *              涉及到磁盘IO，性能较低，但是数据安全
     *             为了保证数据安全，所以一般情况下会独立执行作业
     *             为了能够提高效率，一般情况下需要和cache联合使用
     */

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
    mapRdd.cache()
    mapRdd.checkpoint()




    val reduceRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)
    reduceRdd.collect().foreach(println)

    println("************************")


    val groupRdd: RDD[(String, Iterable[Int])] = mapRdd.groupByKey()
    groupRdd.collect().foreach(println)



    // 关闭连接
    sc.stop()


  }
}
