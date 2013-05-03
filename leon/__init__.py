# coding=utf-8

import cherrypy
from leon.web_handler import WebHandler as _WebHandler
from leon.arg_conversions import list_of


def create(config=None):
    """
    :param config: Optional configuration dict
    :type config: dict
    :return: The Leon application
    :rtype: WebHandler
    """
    return _WebHandler(config)


def start_server(app):
    """
    :param app: The Leon application to start
    :type app: WebHandler
    """
    cherrypy.quickstart(app)
