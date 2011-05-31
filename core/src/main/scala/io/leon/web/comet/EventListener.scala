/*
 * Copyright 2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.leon.web.comet

import org.atmosphere.cpr.{AtmosphereResourceEvent, AtmosphereResourceEventListener}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import java.util.logging.Logger

class EventListener extends AtmosphereResourceEventListener {

  private val logger = Logger.getLogger(getClass.getName)

  def onSuspend(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    logger.info("onSuspend")
  }

  def onThrowable(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    logger.info("onThrowable")
  }

  def onBroadcast(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    logger.info("onBroadcast:" + event.isCancelled)
  }

  def onDisconnect(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    logger.info("onDisconnect")
  }

  def onResume(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    logger.info("onResume")
  }

}
