Configuration
=============

TODO

.. js:function:: install(module)
  
   Installs additional Leon modules.
      
   :param Module module:
       The module to install. (e.g. ``new Packages.io.leon.persistence.mongo.LeonMongoModule()``)
       
       
.. js:function:: setBaseDir(baseDir)
   
   Sets the base directory of the application. All relative locations are relative to this directory.
   
   :param string baseDir:        
        Default value is the location of the configuration file.

.. _resources:

Resources
--------- 

.. js:function:: addLocation(path)

   Adds a directory where Leon should lookup resources. 

   :param string path: Relative paths are relative to the base directory.
        

.. js:function:: exposeUrl(regex)
  
   Exposes paths that match the given regular expression to clients.
   
   By default, Leon gives client access to the following paths/files: ``*.html, *.png, *.jpg, *.gif, *.css, favicon.ico, */browser/*.js, */browser/*.json``. To allow access on other paths or files to clients, you have to expose them by calling this function.
   
   
   :param string regex:
        Regular expression to match. 


.. js:function:: loadFile(fileName)
  
   Loads JavaScript files in the server environment.
  
   :param string fileName:
        A server-side JavaScript file to load. The path must be relative to a registered location or to the application's classpath.


AJAX-Support
--------------------
        
.. js:function:: browser(browserName).linksToServer([serverName])
  
   Makes a server-side object accessible by clients via AJAX.
    
   :param string browserName: 
        Name of the client-side variable.
   :param string serverName: 
        Variable name of the server-side object. If not given, serverName is the same as browser name.
   

.. js:function:: browser(browserName).linksToServer(clazz)

    Makes Java objects directly accessible by clients via AJAX.

    :param string browserName: 
        Name of the client-side variable.
    :param Class clazz: 
        Java class on the server-side.

Dependency injection
---------------------

.. js:function:: bind(clazz)
  
  TODO: Registers a binding in google guice. See `Google Guice Binder <http://google-guice.googlecode.com/svn/trunk/javadoc/com/google/inject/Binder.html>`_ for more information about how to use bindings.
  
  :param Class clazz:
      The Java class to bind.
