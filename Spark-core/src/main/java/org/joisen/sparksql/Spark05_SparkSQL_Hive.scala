package org.joisen.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql._

/**
 * @author : joisen 
 * @date : 17:25 2022/11/9 
 */
object Spark05_SparkSQL_Hive {
  def main(args: Array[String]): Unit = {

    // todo 创建SparkSQL的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate() // 此处的spark为前面定义的spark

    // 使用SparkSQL连接外置的Hive
    // 1 拷贝Hive-site.xml 到classpath下
    // 2 启用hive的支持 .enableHiveSupport()
    // 3 增加对应的依赖关系（包含mysql驱动）
    spark.sql("show tables").show()



    // todo 关闭环境
    spark.close()

  }
}
