package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Aggregator

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark04_SparkSQL_JDBC {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate() // 此处的spark为前面定义的spark
    import spark.implicits._

    // 读取MySQL数据
    val df: DataFrame = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark_sql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "999719")
      .option("dbtable", "user")
      .load()
//    df.show

    // 保存数据 将得到的df保存为一张新表
    df.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark_sql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "999719")
      .option("dbtable", "user1")
      .mode(SaveMode.Append)
      .save()


    // todo 关闭环境
    spark.close()

  }
}
