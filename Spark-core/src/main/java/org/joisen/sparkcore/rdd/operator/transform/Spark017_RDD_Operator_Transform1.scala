package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark017_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型  aggregateByKey

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6),
    ),2)

    // aggregateByKey存在函数柯里化，有两个参数列表
    // 第一个参数列表需要传递一个参数，表示为初始值：主要用于当碰见第一个key时和value进行分区内计算
    // 第二个参数列表需要传递两个参数：
    //        第一个参数表示分区内的计算规则
    //        第二个参数表示分区间的计算规则

    rdd.aggregateByKey(5)(
      (x,y) => Math.max(x,y),
      (x,y) => x+y
    ).collect().foreach(println)

    /**
     * 如果聚合计算时，分区内和分区间的计算规则相同，spark提供了简化方法
     */
    rdd.foldByKey(0)(_+_).collect().foreach(println)
    println("========")

    /**
     * 获取相同key的平均值
     */
    val newRdd: RDD[(String, (Int, Int))] = rdd.aggregateByKey((0, 0))(
      (t, v) => { //t表示一个元组(每个key的value，出现次数)，v表示相同key的value值（要进行操作的）
        (t._1 + v, t._2 + 1)
      },
      (t1, t2) => { // （t1，t2）表示分区间要进行聚合操作的两个t（同上）的元组
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    )
    val resultRdd: RDD[(String, Int)] = newRdd.mapValues {
      case (num, cnt) => {
        num / cnt
      }
    }
    resultRdd.collect().foreach(println)
    println("========")

    //combineByKey有三个参数：
    // 第一个参数：将相同key的第一个数据进行结构的转换（"a",1） => ("a",(1,1))
    // 第二个参数: 分区内的计算规则
    // 第三个参数：分区间的计算规则
    val newRdd1: RDD[(String, (Int, Int))] = rdd.combineByKey(
      (v: Int) => (v, 1),
      (t: (Int, Int), v: Int) => { //t表示一个元组(每个key的value，出现次数)，v表示相同key的value值（要进行操作的）
        (t._1 + v, t._2 + 1)
      },
      (t1: (Int, Int), t2: (Int, Int)) => { // （t1，t2）表示分区间要进行聚合操作的两个t（同上）的元组
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    )
    val resultRdd1: RDD[(String, Int)] = newRdd.mapValues {
      case (num, cnt) => {
        num / cnt
      }
    }
    resultRdd1.collect().foreach(println)

    rdd.reduceByKey(_+_)
    rdd.aggregateByKey(0)(_+_,_+_)
    rdd.foldByKey(0)(_+_)
    rdd.combineByKey(v=>v,(x:Int,y)=>x+y,(x:Int,y:Int)=>x+y)

    sc.stop()

  }

}
