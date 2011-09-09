MongoDB
=======

This Leon module enables your application to interact with a MongoDB [#f1]_. 


Enable Leon's MongoDB module
----------------------------

Leon's MongoDB module can be enabled by adding the following line to Leon's configuration file:

.. code-block:: javascript

  install(new Packages.io.leon.persistence.mongo.LeonMongoModule());
  

Setting up a connection
-----------------------

Without specifying any connection parameters Leon connects to the following database:

* Host: 127.0.0.1
* Port: 27017
* Database: leon_test

To pass your own connection parameters you can use the ``LeonMongoConfig`` object. How to do this shows the following example:

.. code-block:: javascript

    var mongoConfig = new Packages.io.leon.persistence.mongo.LeonMongoConfig("mongo0.example.com",
     27017, "your_database");

    install(new Packages.io.leon.persistence.mongo.LeonMongoModule(mongoConfig));


Working with MongoDB
--------------------

If you have enabled Leon's MongoDB module, the MongoDB connection is bound to the server-side variable ``leon.mongo``.

To access a Mongo collection within a server-side JavaScript file just use its name in the scope of ``leon.mongo``. For example, the code to insert a document in a collection named ``people`` looks like this:

.. code-block:: javascript

  leon.mongo.people.insert({name: "John Doe"});

That's all! If the collection doesn't exist, MongoDB will create it for you.

To query a collection you can use the function ``find``:

.. code-block:: javascript

  var cursor = leon.mongo.people.find({name: /^John.*$/});
  cursor.forEach(function(person) {
    // do something with the person
  });

The example above queries the collection ``people`` for documents with a field ``name`` starting with "John". The function ``find`` returns a cursor that can be used to iterate over the result.

Please see the MongoDB documentation [#f2]_ for more information about MongoDB functions. Generally speaking, all functions MongoDB provides can be accessed via Leon's Mongo module.


Using MongoDB from Java
-----------------------

TODO


.. rubric: Footnotes

.. [#f1] MongoDB is a document-oriented database which comes with a simple query language. http://mongodb.org
.. [#f2] MongoDB Documentation http://www.mongodb.org/display/DOCS/Manual



