package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark01_SparkSQL_Basic {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")

    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._ // 此处的spark为前面定义的spark

    // todo 执行逻辑操作

    // todo DataFrame
//    val df: DataFrame = spark.read.json("datas/user.json")
//    df.show()

    // todo DataFrame => SQL
//    df.createOrReplaceTempView("user")
//    spark.sql("select * from user").show
//    spark.sql("select age from user").show
//    spark.sql("select avg(age) from user").show

    // todo DataFrame => DSL
    // 使用DataFrame时，如果涉及到转换操作时，需要引入转换规则

//    df.select("age","username").show()
//    df.select($"age"+1).show()

    // todo DataSet
    // DataFrame其实是特定泛型的DataSet
//    val seq = Seq(1, 2, 3, 4)
//    val ds: Dataset[Int] = seq.toDS()
//    ds.show()

    // todo RDD <=> DataFrame
    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zs", 30), (2, "ls", 20)))
    val df: DataFrame = rdd.toDF("id", "name", "age")
    val rowRDD: RDD[Row] = df.rdd

    // todo DataFrame <=> DataSet
    val ds: Dataset[User] = df.as[User]
    val df1: DataFrame = ds.toDF()


    // todo RDD <=> DataSet
    val ds1: Dataset[User] = rdd.map {
      case (id, name, age) => {
        User(id, name, age)
      }
    }.toDS()

    val userRDD: RDD[User] = ds1.rdd



    // todo 关闭环境
    spark.close()

  }
  case class User(id:Int,name:String,age:Int)
}
