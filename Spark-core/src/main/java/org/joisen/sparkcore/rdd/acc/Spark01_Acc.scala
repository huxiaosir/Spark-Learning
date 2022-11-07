package org.joisen.sparkcore.rdd.acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 19:41 2022/11/7 
 */
object Spark01_Acc {
  def main(args: Array[String]): Unit = {

    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster("local").setAppName("ACC")
    val sc = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    // reduce：分区内计算，分区间计算
//    val i: Int = rdd.reduce(_ + _)
//    println(i)

    /**
     * 输出的结果为 0：因为执行累加的操作是在executor端进行的，而executor端得到的结果
     * 没有返回给driver端，故输出的结果为 0, 通过spark的累加器可以解决
     */
//    var sum = 0
//    rdd.foreach(
//      num => {
//        sum += num
//      }
//    )
//    println("sum = "+ sum)

    // 获取系统累加器
    // spark默认提供了简单数据聚合的累加器
    val sumAcc: LongAccumulator = sc.longAccumulator("sum")
//    sc.doubleAccumulator
//    sc.collectionAccumulator

    rdd.foreach(
      num => {
        // 使用累加器
        sumAcc.add(num)
      }
    )
    // 获取累加器的值
    println(sumAcc.value)


    sc.stop()
  }

}
