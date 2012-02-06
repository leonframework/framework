Java Interoperability
=====================

Leon uses Mozilla's Rhino [#f1]_ JavaScript engine which comes with java interoperability out-of-the-box. This enables us to [#f2]_:

* create instances of Java classes
* call methods of Java objects
* access bean properties as they were ordinary attributes
* extend java classes and implementing interfaces in JavaScript.
	

Using Java objects in JavaScript
--------------------------------

You can create a new instance of a Java object by using the keyword ``new`` and the full-qualified class name prefixed with ``Packages``:

.. code-block:: javascript

  var obj = new Packages.java.lang.StringBuffer("I'm a Java object");
  
Another way to access a Java object in JavaScript is by asking the dependency injector to get a reference to an object:

.. code-block:: javascript

  var obj = leon.inject(Packages.xyz.MyJavaObject);
    
Getters and Setters can be accessed as they were ordinary attributes. Instead of ``person.getName()`` you can write ``person.name`` and instead of ``person.setName(x)`` you can write ``person.name = x`` in JavaScript.
  

Serializing Java objects to JSON
--------------------------------

TODO

Serializing JSON to Java objects
--------------------------------

TODO  

.. rubric: Footnotes

.. [#f1] Mozilla Rhino http://www.mozilla.org/rhino/
.. [#f2] Scripting Java http://www.mozilla.org/rhino/scriptjava.html

