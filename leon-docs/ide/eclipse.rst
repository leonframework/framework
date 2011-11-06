Leon Eclipse Plugin
===================

For Leon we provide an Eclipse plugin including
* a Leon project wizard
* a project property page, where you can configure the leon configuration file
* Content assist for the leon configuration file opened in the eclipse java script editor


System requirements
--------------------

Eclipse 7.1 or higher, the ``Eclipse IDE for Java Script Web Developers`` package or another package including the ``JavaScript Development Tools (org.eclipse.wst.jsdt.feature)`` and ``Java Development Tools (org.eclipse.jdt.feature)``.
Java 1.5 or higher


Leon project wizard
--------------------

You can start the Leon project wizard from the common list of new project wizards under the category ``Leon`` or with the corresponding toolbar button. With these wizard you can configure the project location an the project set, the new project should be added to. While finishing the wizard, a Leon project with default JRE and java script libraries is created. A sample content for a Leon project including a sample configuration file is added too.


Leon project property page
---------------------------

For a Leon project you will find a ``Leon`` property page on the project properties dialog. On this page you can select the Leon configuration file (default: <project root>/config.js suitable for the sample content, created by the Leon wizard).


Content assist for the leon configuration file
-----------------------------------------------

If you open the Leon configuration file configured on the Leon property page in the Eclipse build-in java script editor, you have access to content proposals for methods later provided by the Leon framework. Please see :ref:`config` for detailed information about the provided methods.