package io.leon.testng

import org.scalatest.Assertions
import org.testng.annotations.Test

class NG2Scala extends Assertions {

  @Test(groups = Array("nodefault", "requires_jetty_server"))
  def test1jetty() {
    println("###### 2-1 start ###### (jetty)")
    NGUtils.sleep(2000)
    println("###### 2-1 ende ###### (jetty)")
  }

  @Test(groups = Array("nodefault", "requires_jetty_server"))
  def test2jetty() {
    println("###### 2-2 start ###### (jetty)")
    NGUtils.sleep(2000)
    println("###### 2-2 ende ###### (jetty)")
  }

  @Test
  def test3() {
    println("###### 2-1 start ######")
    NGUtils.sleep(2000)
    println("###### 2-1 ende ######")
  }

  @Test
  def test4() {
    println("###### 2-2 start ######")
    NGUtils.sleep(2000)
    println("###### 2-3 ende ######")
  }

  @Test
  def test5() {
    println("###### 2-4 start ######")
    NGUtils.sleep(2000)
    println("###### 2-4 ende ######")
  }

}