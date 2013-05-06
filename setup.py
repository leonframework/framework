# coding=utf-8

from setuptools import setup, find_packages

requires = [
    # normal dependencies
    'cherrypy==3.2.4',
    'mako==0.8.0',

    # test dependencies
    'pytest==2.3.4',
    'pytest-pep8==1.0.4',
    'pytest-xdist==1.8',
    'WebTest==2.0.3']

setup(name='leon',
      version='0.4.0',
      description='Simple Web Framework for RPC/REST-oriented Web Applications',
      url='http://leon.io',
      author='Roman Roelofsen',
      author_email='romanroe@gmail.com',
      packages=find_packages(),
      include_package_data=True,
      zip_safe=True,
      install_requires=requires,
      tests_require=[])
