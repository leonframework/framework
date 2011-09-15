Closure Templates
=================

Leon uses Googles's [#f1]_ Closures Templates to enable client-side templating.


Defining a client-side template
--------------------------------
Closures Templates are defined in ``.soy`` files. See the official documentation [#f2]_ for further information about the ``.soy`` syntax.
Template files have to be placed in a :ref:`resources` folder.

Automatic compilation of ``.soy`` files
----------------------------------------
All ``.soy`` files are automatically compiled by the Closures compiler. The result is a ``.js`` file with the same name at the same location as the ``.soy`` file.


.. [#f1] Closure Templates http://code.google.com/intl/de/closure/templates/
.. [#f2] Closure File Structure http://code.google.com/intl/de/closure/templates/docs/concepts.html#filestructure