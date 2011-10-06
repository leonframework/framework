
.. image:: https://github.com/leonframework/framework/raw/master/leon-docs/img/mascot/Leon_github_header.png

----

Leon is an application framework for building smart web applications. We believe that in modern web applications the UI should be completely based on HTML(5), CSS and JavaScript, and be rendered in the browser while the server should provide strong AJAX / Comet communication mechanisms, database access, security management, etc.

Leon combines and integrates best-of-breed client frameworks like `jQuery <http://www.jquery.org/>`_ and `AngularJS <http://www.angularjs.org/>`_ with it's strong server runtime. Even though Leon is JVM-based (implemented in Scala & Java), server-side code can be written in JavaScript as well. Leon's Java/JavaScript integration goes beyond simple method calls by providing automatic conversions of JavaScript objects and Java POJOs, for example.

For those who like it (we certainly do!), we provide automatic `CoffeeScript <http://jashkenas.github.com/coffee-script/>`_ and `Less CSS <http://lesscss.org/>`_ generation. For example, CoffeeScript files can be used as a drop-in replacement and be used on the server- as well as client-side.

We decided to use the JVM as the development and deployment target instead of e.g. node.js so that we can easily connect to databases, message brokers and even SAP systems. The JVM is probably the most proven and tested application platform.

Why a chameleon as as mascot, you ask? Well, applications written with Leon *may* utilise Java, Scala, JavaScript, CoffeeScript and Less CSS, programmers *might* start with a Java JAR file providing the application logic or use Leon's HTML form driven approach for rapid CRUD applications. Hence, working with Leon can look quite differently while it's all the same in the end. 

