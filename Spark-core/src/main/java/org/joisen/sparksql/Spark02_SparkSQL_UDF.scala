package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark02_SparkSQL_UDF {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")

    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._ // 此处的spark为前面定义的spark

    val df: DataFrame = spark.read.json("datas/user.json")
    df.createOrReplaceTempView("user")

    // 使用udf给查询结果的字段前面加上前缀
    spark.udf.register("prefixName",(name:String) => {
      "Name: "+ name
    })

    spark.sql("select age,prefixName(username) from user").show()


    // todo 关闭环境
    spark.close()

  }
}
