package org.joisen.sparkcore.framework.application

import org.joisen.sparkcore.framework.common.TApplication
import org.joisen.sparkcore.framework.controller.WordCountController

/**
 * @author : joisen 
 * @date : 9:20 2022/11/9 
 */
object WordCountApplication extends App with TApplication {

  //启动应用程序
  start(){
    val controller = new WordCountController();
    controller.dispatch()
  }
}
