package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql._

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark03_SparkSQL_UDAF2 {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate() // 此处的spark为前面定义的spark
    import spark.implicits._

    val df: DataFrame = spark.read.json("datas/user.json")


    // 早期版本中，spark不能在sql中使用强类型udaf操作
    // SQL & DSL
    // 早期的UDAF强类型聚合函数使用DSL语法操作
    val ds: Dataset[User] = df.as[User]

    // 将UDAF转换为查询的列对象
    val udafCol: TypedColumn[User, Long] = new MyAvgUDAF().toColumn
    ds.select(udafCol).show()


    // todo 关闭环境
    spark.close()

  }

  /**
   * 自定义聚合函数类：计算年龄的平均值
   * 1 继承 org.apache.spark.sql.expressions.Aggregator 定义泛型
   *      IN : 输入数据类型User
   *      BUF：
   *      OUT : 输入数据类型Long
   * 2 重写方法
   */
  case class User(username: String, age: Long)
  case class Buff(var total: Long, var count: Long)
  class MyAvgUDAF extends Aggregator[User, Buff, Long]{
    // z&zero 初始值或者零值
    // 缓冲区初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }
    // 根据输入的数据更新缓冲区的数据
    override def reduce(buff: Buff, in: User): Buff = {
      buff.total = buff.total + in.age
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
