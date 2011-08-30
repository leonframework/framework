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



Resources
--------- 

.. js:function:: addLocation(path)

   Add a location of the file system where Leon should lookup resources. 

   :param string path: Relative paths are relative to the base directory.
        

.. js:function:: addInternalPath(path)
  
   Files and directories in this path will not be accessible by clients.
  
   :param string path:
        Path which contains server-side resources. 


.. js:function:: loadFile(fileName)
  
   Loads JavaScript files in the server environment.
  
   :param string fileName:
        A server-side JavaScript file to load. The path must be relative to a registered location or to the application's classpath.


AJAX-/ Comet-Support
--------------------
        
.. js:function:: browser(browserName).linksToServer([serverName])
  
   Makes a server-side object accessible by clients via AJAX.
    
   :param string browserName: 
        Name of the client-side variable.
   :param string serverName: 
        Variable name of the server-side object. If not given, serverName is the same as browser name.
   

.. js:function:: browser(browserName).linksToServer(obj)

    Makes Java objects directly accessible by clients via AJAX.

    :param string browserName: 
        Name of the client-side variable.
    :param Object obj: 
        Instance of a Java object.
        

.. js:function:: server(serverName).linksToAllPages(browserName)

   TODO.
   
   :param string serverName:
        TODO
   :param string browserName:
        TODO


.. js:function:: server(serverName).linksToCurrentPage(browserName)

    TODO.

    :param string serverName:
        TODO
    :param string browserName:
        TODO


.. js:function:: server(serverName).linksToSessionPages(browserName)

    TODO.

    :param string serverName:
        TODO
    :param string browserName:
        TODO
