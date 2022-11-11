package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, Row, SparkSession, functions}

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark03_SparkSQL_UDAF1 {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")

    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate() // 此处的spark为前面定义的spark

    val df: DataFrame = spark.read.json("datas/user.json")
    df.createOrReplaceTempView("user")

    // 使用udf给查询结果的字段前面加上前缀
    spark.udf.register("ageAvg", functions.udaf(new MyAvgUDAF()))

    spark.sql("select ageAvg(age) from user").show()


    // todo 关闭环境
    spark.close()

  }

  /**
   * 自定义聚合函数类：计算年龄的平均值
   * 1 继承 org.apache.spark.sql.expressions.Aggregator 定义泛型
   *      IN : 输入数据类型Long
   *      BUF：
   *      OUT : 输入数据类型Long
   * 2 重写方法
   */
  case class Buff(var total: Long, var count: Long)
  class MyAvgUDAF extends Aggregator[Long, Buff, Long]{
    // z&zero 初始值或者零值
    // 缓冲区初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }
    // 根据输入的数据更新缓冲区的数据
    override def reduce(buff: Buff, in: Long): Buff = {
      buff.total = buff.total + in
      buff.count = buff.count + 1
      buff
    }
    // 缓冲区
    override def merge(buff1: Buff, buff2: Buff): Buff = {
      buff1.total = buff1.total + buff2.total
      buff1.count = buff1.count + buff2.count
      buff1
    }
    // 计算结果
    override def finish(buff: Buff): Long = {
      buff.total / buff.count
    }
    // 缓冲区的编码
    override def bufferEncoder: Encoder[Buff] = Encoders.product
    // 输出的编码
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }
}
