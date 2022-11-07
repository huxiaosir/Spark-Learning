package org.joisen.sparkcore.test

/**
 * @author : joisen 
 * @date : 10:37 2022/11/3 
 */
class Task extends Serializable {

  val datas = List(1,2,3,4)

//  val logic = (num: Int) => {num * 2}
  val logic: (Int)=> Int = _ * 2

}
