package io.leon.javascript.test

import reflect.BeanInfo

@BeanInfo
case class TestBean(x: String, y: Int, z: NestedTestBean) {
  private def this() = this("", 0, null)
}

@BeanInfo
case class NestedTestBean(a: String, b: Long) {
  private def this() = this("", 0)
}

trait TestService {

  def getTestBean: TestBean

  def setTestBean(bean: TestBean)

  def setInt(i: Int) = i
}

class TestServiceImpl extends TestService {

  private var bean = TestBean("x", 1, NestedTestBean("a", 2))

  def getTestBean = bean

  def setTestBean(bean: TestBean) {
    this.bean = bean
    println("setTestBean got: " + bean)
  }
}
