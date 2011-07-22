package io.leon.javascript.test

import reflect.BeanInfo
import java.lang.{Float, Short}
import sun.security.util.DerEncoder

@BeanInfo
case class TestBean(x: String, y: Int, z: NestedTestBean) {
  private def this() = this("", 0, null)
}

@BeanInfo
case class NestedTestBean(a: String, b: Long) {
  private def this() = this("", 0)
}

class TestService {

  private var bean = TestBean("x", 1, NestedTestBean("a", 2))

  def getTestBean = bean

  def setTestBean(bean: TestBean) {
    this.bean = bean
    println("setTestBean got: " + bean)
  }

  def methodWithNumericArgs(i: Int, l: Long, s: Short, b: Byte, f: Float, d: Double) = {
    (i + l + s + b + f + d)
  }

  def methodWithDefaultArgs(bean: TestBean, x: Int = 2) {
    println("bean: %s, x: %s".format(bean, x))
  }

  def methodWithString(s: String) = {
    println("s = " + s)
    "Hello World"
  }

  def methodWithList(list: Seq[TestBean]) = {
    println(list.mkString(", "))
  }

  def methodWithIntList(list: Seq[Int]) = {
    println(list.mkString(", "))
  }
}
