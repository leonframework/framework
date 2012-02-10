/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.freemarker

import com.google.inject.Inject
import freemarker.template.{Template, Configuration}
import java.io._
import io.leon.resourceloading.Resource

class FreeMarkerProcessor @Inject()(templateLoader: LeonFreeMarkerTemplateLoader) {
  import scala.collection.JavaConverters._

  private lazy val configuration = new Configuration
  configuration.setTemplateLoader(templateLoader)

  private lazy val data = Map.empty[String, Any]

  def transform(in: Resource) =  new Resource(in.name, () => {
    val tpl = new Template(in.name, new InputStreamReader(in.createInputStream()), configuration)

    val out = new ByteArrayOutputStream
    val writer = new OutputStreamWriter(out)

    tpl.process(data.asJava, writer);

    new ByteArrayInputStream(out.toByteArray)
  })
}
