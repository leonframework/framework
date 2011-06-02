package io.leon.dummyapp.person

import com.google.inject.Inject
import io.leon.web.comet.UplinkFunction
import javax.inject.Named

class TestService @Inject()(@Named("uplinkAlert") uplink: UplinkFunction) {

  def callUplinkAlert() {
    println("Calling uplinkAlert")
    uplink("TestService", "hello from scala")
  }

}
