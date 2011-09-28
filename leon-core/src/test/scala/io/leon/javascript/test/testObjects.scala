package io.leon.javascript.test

import reflect.BeanInfo
import java.lang.{Float, Short}


@BeanInfo
case class TestBean(x: String, y: Int, z: NestedTestBean, xs: Array[String]) {
  private def this() = this("", 0, null, null)
}

@BeanInfo
case class NestedTestBean(a: String, b: Long) {
  private def this() = this("", 0)
}


class TestService {

  private var bean = TestBean("x", 1, NestedTestBean("a", 2), Array("a"))

  def getTestBean = bean

  def setTestBean(bean: TestBean) {
    this.bean = bean
  }

  def methodWithNumericArgs(i: Int, l: Long, s: Short, b: Byte, f: Float, d: Double) = {
    (i + l + s + b + f + d)
  }

  def methodWithDefaultArgs(bean: TestBean, x: Int = 2) {

  }

  def methodWithString(s: String) = {
    "Hello World"
  }

  def methodWithList(list: Seq[TestBean]) {
  }

  def methodWithIntList(list: Seq[Int]) {
  }

  def methodWithJavaTestBean(bean: JavaTestBean) = {
    bean.setX("Hello world")
    bean
  }

  def methodWithJavaList(list: java.util.List[JavaTestBean]) = {
    list
  }

  def methodWithSeq(seq: Seq[TestBean]) = {
    seq map { e => e.copy(y = e.y + 1) }
  }

  def methodWithArray(arr: Array[Int]) = {
    arr map { _ + 1 }
  }

  def overloadedMethod(i: Int, bean: TestBean) {}

  def overloadedMethod(bean: TestBean, i: Int) {}


}
