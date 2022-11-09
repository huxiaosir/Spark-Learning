package org.joisen.sparkcore.framework.common

import org.apache.spark.{SparkConf, SparkContext}
import org.joisen.sparkcore.framework.controller.WordCountController
import org.joisen.sparkcore.framework.util.EnvUtil

/**
 * @author : joisen 
 * @date : 9:40 2022/11/9 
 */
trait TApplication{
  def start(master:String="local[*]", app: String="Application")( op: => Unit ): Unit ={
    // 建立和Spark框架的连接
    val conf = new SparkConf().setMaster(master).setAppName(app)
    val sc = new SparkContext(conf)
    EnvUtil.put(sc)
    try{
      op
    }catch {
      case ex => println(ex.getMessage)
    }


    // 关闭连接
    sc.stop()
    EnvUtil.clear()
  }
}
