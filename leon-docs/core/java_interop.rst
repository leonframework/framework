Java Interoperability
=====================

Leon uses Mozilla's Rhino [#f1]_ JavaScript engine which comes with java interoperability out-of-the-box. This enables us to [#f2]_:

* create instances of Java classes
* call methods of Java objects
* access bean properties as they were ordinary attributes
* extend java classes and implementing interfaces in JavaScript.

However, this interoperability is limited to primitive types only. Leon enables us to call methods of Java objects with complex JSON data structures by transforming them to the corresponding java type. For Java, this feature is limited to POJOs [#f3]_ or simple Java Beans and Java collection types.


Using Java objects in JavaScript
--------------------------------

You can create a new instance of a Java object by using the keyword ``new`` and the full-qualified class name prefixed with ``Packages``:

.. code-block:: javascript

  var obj = new Packages.java.lang.StringBuffer("I'm a Java object");
  
Another way to access a Java object in JavaScript is by asking the dependency injector to get a reference to an object:

.. code-block:: javascript

  var obj = leon.inject(Packages.xyz.MyJavaObject);
    
Getters and Setters can be accessed as they were ordinary attributes. Instead of ``person.getName()`` you can write ``person.name`` and instead of ``person.setName(x)`` you can write ``person.name = x`` in JavaScript.
  
JavaScript objects can be passed as arguments to Java methods and will be converted to the corresponding Java type automatically. However, this will not work for overloaded method calls. It would not be guaranteed that Leon selects the desired method. In that case you have to perform the serialization manually by calling the method ``asJavaObject(clazz)`` yourself.

.. code-block:: javascript

  var obj = {...}.asJavaObject(Packages.io.leon.test.TestBean);
  javaObject.overloadedMethod(obj, 123);
 
Note that return values will not be converted automatically, because we don't want to destroy its identity. They are wrapped in a transparent proxy type and you can work with it like it were a ordinary JavaScript object. If you pass such a java proxy to a method, the java object gets simply unwrapped and there is no need for a complete conversion. However, to convert such a proxy object to a native JavaScript object, you can call the ``toJSON`` method.


Serializing Java objects to JSON
--------------------------------

Every Java object you work with in JavaScript has a function called ``toJSON``. It returns a JSON representation of the object by converting each property to the corresponding JavaScript type. This is most suitable for data objects more specifically for POJOs. 

If a java object is part of an HTTP response this function is called automatically. So in most cases you can ignore this function, but it's good to know how to convert a Java object to a native JavaScript object anyway.

The following conversions are supported:

* Primitive java types (including the corresponding ``java.lang`` objects) to JavaScript
* Java arrays to JavaScript arrays
* Java collection types to JavaScript arrays (Currently all ``java.util.Collection`` types are supported but not ``java.util.Map``)
* POJOs to JavaScript objects

Note: Dates are currently not supported.


Serializing JSON to Java objects
--------------------------------

The serialization of JavaScript to Java is triggered automatically when a method of a Java object is called from JavaScript. More precisely, the provided arguments are converted. 
For instance, you can call a Java method which expects some kind of POJO as its argument from JavaScript. Leon tries to build that object from the supplied JavaScript object.

The serialization can also be triggered manually by calling the method ``asJavaObject(clazz)`` where ``clazz`` is the desired target type. This method is available on all JavaScript objects.


The following conversions are supported:

* primitive JavaScript types to Java (Note: Date is currently not supported)
* JavaScript objects to POJOs
* JavaScript arrays to Java collections (``java.util.Map`` is currently not supported)

  Leon looks up the desired Java collection type, tests if it is assignable from ``java.util.List``
  or ``java.util.Set`` and converts the JavaScript array to an implementation of that type. 
  
  Additionally, the following concrete Java Collection types are supported which can be used in signatures:
  
  * ``java.util.ArrayList``
  * ``java.util.LinkedList``
  * ``java.util.Vector``
  * ``java.util.HashSet``
  * ``java.util.TreeSet``
  * ``java.util.LinkedHashSet``
    
  For unsupported collection types Leon raises an exception.
  

.. rubric: Footnotes

.. [#f1] Mozilla Rhino http://www.mozilla.org/rhino/
.. [#f2] Scripting Java http://www.mozilla.org/rhino/scriptjava.html
.. [#f3] POJO (Plain Old Java Object) http://en.wikipedia.org/wiki/POJO
