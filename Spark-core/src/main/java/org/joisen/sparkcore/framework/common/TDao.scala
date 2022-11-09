package org.joisen.sparkcore.framework.common

import org.apache.spark.rdd.RDD
import org.joisen.sparkcore.framework.util.EnvUtil

/**
 * @author : joisen 
 * @date : 9:49 2022/11/9 
 */
trait TDao {
  def readFile(path: String): RDD[String] ={
    // 1 读取文件，获取一行一行的数据
    EnvUtil.take().textFile(path)
  }
}
