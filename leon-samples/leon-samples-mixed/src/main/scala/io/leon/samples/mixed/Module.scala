package io.leon.samples.mixed

import io.leon.{LeonModule, AbstractLeonConfiguration}

/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
class Module extends AbstractLeonConfiguration {

  def config() {
    loadFile("io/leon/samples/mixed/person.js")

    browser("person").linksToServer()

    server("leon.browser").linksToCurrentPage("leon")
  }

}
