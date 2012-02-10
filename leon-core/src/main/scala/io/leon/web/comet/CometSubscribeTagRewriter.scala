/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import com.google.inject.{Inject, Injector}
import io.leon.web.htmltagsprocessor.LeonTagRewriter
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import net.htmlparser.jericho.{OutputDocument, Source}


class CometSubscribeTagRewriter @Inject()(injector: Injector,
                                          clients: Clients,
                                          cometRegistry: CometRegistry) extends LeonTagRewriter {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private def request = injector.getInstance(classOf[HttpServletRequest])

  def process(doc: Source): Seq[OutputDocument => Unit] = {
    import scala.collection.JavaConverters._

    val subscribeTags = doc.getAllStartTags("leon:subscribe")
    if (subscribeTags.size() == 0)
      return Nil

    val pageId = Option(request.getAttribute("pageId")) map {
      _.toString
    } getOrElse {
      Clients.generateNewPageId()
    }
    request.setAttribute("pageId", pageId)

    val clientId = Clients.generateExistingClientId(request.getSession(true), pageId)
    val cc = new ClientConnection(clientId, None)
    clients.add(cc)
    logger.info("Registering client [%s].".format(cc.clientId))

    for (subscribeTag <- subscribeTags.asScala) yield {

      val topicId = Option(subscribeTag.getAttributeValue("topic")) getOrElse sys.error("attribute topic is missing in <leon:subscribe>!")
      val filterOn = Option(subscribeTag.getAttributeValue("filterOn")) map {
        _.split(",").toList map {
          _.trim()
        }
      } getOrElse Nil
      val handlerFn = Option(subscribeTag.getAttributeValue("handlerFn")) getOrElse sys.error("attribute handlerFn is missing in <leon:subscribe>!")

      cc.registerTopicSubscriptions(topicId, filterOn)

      val scriptToInclude =
        ("""
        |<script type="text/javascript">
        |  leon.comet.addHandler("%s", %s);
        |  leon.comet.connect(%s);
        |</script>
        """).stripMargin.format(topicId, handlerFn, pageId)

      out: OutputDocument => out.replace(subscribeTag, scriptToInclude)
    }
  }
}
