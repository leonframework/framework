# coding=utf-8

import cherrypy
from leon.core import WebHandler as _WebHandler
from leon.core import ListOf


def create(config=None):
    """
    :param config: Optional configuration dict
    :type config: dict
    :return: The Leon application
    :rtype: WebHandler
    """
    return _WebHandler(config)


def run(app):
    """
    :param app: The Leon application to start
    :type app: WebHandler
    """
    cherrypy.quickstart(app)
