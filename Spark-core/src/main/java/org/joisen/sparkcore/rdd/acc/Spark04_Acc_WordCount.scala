package org.joisen.sparkcore.rdd.acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
 * @author : joisen 
 * @date : 19:41 2022/11/7 
 */
object Spark04_Acc_WordCount {
  def main(args: Array[String]): Unit = {

    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("ACC")
    val sc = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(List("hello","spark","hello"))

//    rdd.map((_, 1)).reduceByKey(_+_)

    // 累加器：wordCount
    // 创建累加器对象
    val wcAcc = new MyAccumulator
    // 向Spark进行注册
    sc.register(wcAcc, "wordCountAcc")


    rdd.foreach(
      word =>{
        // 数据的累加（使用累加器）
        wcAcc.add(word)
      }
    )


    // 获取累加器累加的结果
    println(wcAcc.value)


    sc.stop()
  }

  /**
   * 自定义数据累加器
   * 1 继承AccumulatorV2 定义泛型
   * IN：累加器输入数据类型 String
   * OUT：累加器返回的数据类型 mutable.Map[String, Long]
   *
   * 2: 重写方法
   */
  class MyAccumulator extends AccumulatorV2[String, mutable.Map[String, Long]]{

    private var wcMap = mutable.Map[String, Long]()

    // 判断是否为初始状态
    override def isZero: Boolean = {
      wcMap.isEmpty
    }

    //
    override def copy(): AccumulatorV2[String, mutable.Map[String, Long]] = {
      new MyAccumulator()
    }

    override def reset(): Unit = {
      wcMap.clear()
    }

    // 获取累加器需要计算的值
    override def add(word: String): Unit = {
      val newCnt: Long = wcMap.getOrElse(word, 0L) + 1
      wcMap.update(word, newCnt)
    }

    // Driver合并多个累加器
    override def merge(other: AccumulatorV2[String, mutable.Map[String, Long]]): Unit = {
      val map1: mutable.Map[String, Long] = this.wcMap
      val map2: mutable.Map[String, Long] = other.value

      map2.foreach {
        case (word, count) => {
          val newCount: Long = map1.getOrElse(word, 0L) + count
          map1.update(word, newCount)
        }
      }
    }

    // 累加器结果
    override def value: mutable.Map[String, Long] = {
      wcMap
    }
  }
}
