# coding=utf-8

import sys
import logging
import json
log = logging.getLogger('leon')

import cherrypy
from leon.web_handler import WebHandler as _WebHandler
from leon.arg_conversions import list_of


def init_logging_system(app):
    """
    :param app: The Leon application
    :type app: WebHandler
    """
    # TODO use file for production mode

    log_handler = logging.StreamHandler(sys.stdout)
    log_handler.setFormatter(
        logging.Formatter('[%(relativeCreated)-5d %(levelname)-8s %(name)s:%(lineno)d] %(message)s'))

    if app.is_in_development_mode():
        logging.root.setLevel(logging.DEBUG)
    else:
        logging.root.setLevel(logging.DEBUG)

    logging.root.addHandler(log_handler)

    cherrypy.log.access_log.setLevel(logging.ERROR)
    cherrypy.log.error_log.setLevel(logging.ERROR)
    #cherrypy.log.access_log.addHandler(default_log_handler)
    #cherrypy.log.error_log.addHandler(default_log_handler)


def create(config=None, init_logging=True, encoder=json.dumps):
    """
    :param config: Optional configuration dict
    :type config: dict
    :param init_logging:
    :param encoder: Optional encoder
    :return: The Leon application
    :rtype: WebHandler
    """
    app = _WebHandler(config)

    if init_logging:
        init_logging_system(app)

    return app


def start_server(app):
    """
    :param app: The Leon application to start
    :type app: WebHandler
    """
    cherrypy.quickstart(app, '/', app.app.config)
