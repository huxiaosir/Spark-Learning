package org.joisen.sparkcore.framework.util

import org.apache.spark.SparkContext

/**
 * @author : joisen 
 * @date : 9:56 2022/11/9 
 */
object EnvUtil {
  private val scLocal = new ThreadLocal[SparkContext]()

  def put(sc: SparkContext): Unit ={
    scLocal.set(sc)
  }

  def take() ={
    scLocal.get()
  }
  def clear(): Unit ={
    scLocal.remove()
  }

}
