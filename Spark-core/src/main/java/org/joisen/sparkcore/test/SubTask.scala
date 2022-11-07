package org.joisen.sparkcore.test

/**
 * @author : joisen 
 * @date : 10:37 2022/11/3 
 */
class SubTask extends Serializable {

  var datas: List[Int] = _

//  val logic = (num: Int) => {num * 2}
  var logic: (Int)=> Int = _

  def compute(): List[Int] = {
    datas.map(logic)
  }



}
