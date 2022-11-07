package org.joisen.sparkcore.rdd.acc

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
 * @author : joisen 
 * @date : 19:41 2022/11/7 
 */
object Spark05_BC {
  def main(args: Array[String]): Unit = {

    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("ACC")
    val sc = new SparkContext(conf)

    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("b", 2), ("c", 3)
    ))
//    val rdd2: RDD[(String, Int)] = sc.makeRDD(List(
//      ("a", 4), ("b", 5), ("c", 6)
//    ))

    val map: mutable.Map[String, Int] = mutable.Map(("a", 4), ("b", 5), ("c", 6))

    // join会导致数据量几何增长，并且会影响shuffle的性能
//    val joinRdd: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
//    joinRdd.collect().foreach(println)

    // 封装广播变量
    val bc: Broadcast[mutable.Map[String, Int]] = sc.broadcast(map)

    rdd1.map {
      case (w, c) =>{
        val l: Int = bc.value.getOrElse(w, 0)
        (w, (c ,l))
      }
    }.collect().foreach(println)
    sc.stop()
  }

}
