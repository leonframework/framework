Receive messages from server (Comet)
================================================

It's a common requirement to modern Web applications to retrieve messages without the browser explicitly requesting it. A wide known umbrella term for that is Comet [#f1]_. Leon makes it very easy to make use of that technique.

A client can subscribe to one or more topics and registers a handler function which gets called for each new message. You can also set and update filters to only receive messages which apply to a specific filter rule.


Subscribe to a topic to retrieve messages
--------------------------------------------

To subscribe to a topic in your html page, Leon offers a tag called ``<leon:subscribe/>`` which can be placed inside your code. For every page request, Leon registers that unique page to the topic(s) and replaces all ``<leon:subscribe/>`` tags with the necessary JavaScript code. A simple html page with a subscription would look like that:

.. code-block:: html

  <html xmlns:leon="http://leon.io">
    <head>
      <title>Test Page</title>
      <!-- <#include "/io/leon/templates/full.desktop.html" /> -->
      <script type="text/javascript">
        // <![CDATA[
        function myCallback(message) {
          alert(message);
        }
        // ]]>
      </script>
    </head>
    <body>
      <leon:subscribe topic="myChannel" handlerFn="myCallback" />
    </body>  
  </html>
  
In this example the page subscribes to a topic called ``myChannel`` and it registers a callback function called ``myCallback``. The function gets called every time the page receives a new message from the server. In this example, an alert box will be prompted to the user.

If you publish a message to a topic, every user who stays on a page which is subscribed to that topic will receive the message. Sometimes this is not what you want! If so, feel free to read on to learn something about message filtering.


Filter messages
----------------

Let's say you are only interested in specific messages in a topic based on settings the user has made in the UI. For that case, you can set and update some filter rules. All filters will be applied on the server-side and only messages which have passed the filter will be send to the client. 

Filter values are valid for one unique page view. Means, that every single requested page has its own filter rules.

To set a filter, you have to declare the fields you are interested in with the attribute ``filterOn`` in ``<leon:subscribe />`` first. Here you specify the field names seperated by comma. 

.. code-block:: xml
   
   <leon:subscribe topic="myChannel" filterOn="field1, field2" handlerFn="myCallback" />
   
To set or update the actual filter values, you call the ``leon.comet.updateFilter`` JavaScript function.

.. code-block:: javascript

 leon.comet.updateFilter("myChannel", "field1", "1")
 leon.comet.updateFilter("myChannel", "field2", "2")

.. note::

  You can only update filters on fields which are declared in the ``filterOn`` attribute.
      
      
          
.. rubric: Footnotes

.. [#f1] Comet http://en.wikipedia.org/wiki/Comet_%28programming%29
