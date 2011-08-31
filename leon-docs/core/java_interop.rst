Java Interoperability
=====================

Leon uses Mozilla's Rhino [#f1]_ JavaScript engine which comes with a great java interoperability out-of-the-box. This enables us to [#f2]_:

* create instances of Java classes
* call methods of Java classes
* accessing bean properties as they were ordinary attributes
* extending java classes and implementing interfaces in JavaScript.

However, this interoperability is limited to primitive types only. Leon enables us to call methods in java objects with complex JSON data structures by transforming them to the corresponding java type. For Java, this feature is limited to POJOs and Java's collection types.


Using Java objects in JavaScript
--------------------------------

How to get a Java object in JavaScript? 

You can create a new instance of a Java object in JavaScript by using the keyword ``new`` and the full-qualified class name prefixed with ``Packages``::

  var obj = new Packages.java.util.StringBuffer("I'm a Java object");
  
Another way to access a Java object in JavaScript is by asking the dependency injector to get a reference to an object::

  var obj = leon.inject(Packages.xyz.MyJavaObject);
    
Getters and Setters can be accessed as they were ordinary attributes. Instead of ``person.getName()`` you can write ``person.name`` in JavaScript.
  

TODO ...  


Serializing Java objects to JSON
--------------------------------

Every Java object you work with in JavaScript has a function called ``toJSON``. It returns a JSON representation of the object by converting each field property to the corresponding JavaScript type. So this is most suitable for POJOs. 

If a java object is part of an HTTP response this function is called automatically. So in the most cases you can ignore this function, but it's good to know how to convert a Java object to a native JavaScript object anyway.


Serializing JSON to Java objects
--------------------------------

TODO ...




.. rubric: Footnotes

.. [#f1] Mozilla Rhino http://www.mozilla.org/rhino/
.. [#f2] Scripting Java http://www.mozilla.org/rhino/scriptjava.html