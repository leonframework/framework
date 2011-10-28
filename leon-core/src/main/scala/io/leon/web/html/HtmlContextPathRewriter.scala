package io.leon.web.html

import net.htmlparser.jericho.Source
import com.google.inject.AbstractModule
import io.leon.resources.htmltagsprocessor.{LeonTagRewriters, LeonTagRewriter}

class HtmlContextPathRewriter extends LeonTagRewriter {

  def process(doc: Source) = {
    import scala.collection.JavaConverters._
    println("--------------------------------------")

    doc.getAllStartTags("a").asScala map { tag =>
      val href = tag.getAttributeValue("href")
      tag -> ("XXXX/" + href)
    }

  }

}

class HtmlContextPathRewriterModule extends AbstractModule {
  override def configure() {
    LeonTagRewriters.bind(binder(), classOf[HtmlContextPathRewriter])
  }
}
