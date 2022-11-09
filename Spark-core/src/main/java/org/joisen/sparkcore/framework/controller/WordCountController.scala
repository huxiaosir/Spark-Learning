package org.joisen.sparkcore.framework.controller

import org.joisen.sparkcore.framework.common.TController
import org.joisen.sparkcore.framework.service.WordCountService

/**
 * @author : joisen 
 * @date : 9:20 2022/11/9 
 */
class WordCountController extends TController {
  private val wordCountService = new WordCountService();


// 调度
  def dispatch(): Unit ={

    val array: Array[(String, Int)] = wordCountService.dataAnalysis()

    array.foreach(println)
  }
}
