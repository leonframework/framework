# coding=utf-8

import sys
import os
import re
import inspect
import logging
import json
import cherrypy
from cherrypy import HTTPError
from cherrypy.lib.static import serve_file
from mako.lookup import TemplateLookup
from webhandler.py_2vs3_utils import escape


default_log_handler = logging.StreamHandler(sys.stdout)
default_log_handler.setLevel(logging.DEBUG)
default_log_handler.setFormatter(logging.Formatter(
    '[%(relativeCreated)-5d %(levelname)-8s %(name)s:%(lineno)d] %(message)s'))


###############################################################################
# argument conversion
###############################################################################

_NOT_OPTIONAL = object()


class ListOf:
    def __init__(self, member_type):
        self.member_type = member_type

    def convert_list(self, alist):
        return [_convert_to_type(i, self.member_type) for i in alist]


def _convert_to_type(value, default_value_or_type):
    if type(default_value_or_type) is type:
        target_type = default_value_or_type
    else:
        target_type = type(default_value_or_type)

    # int
    if target_type is int:
        return int(value)

    # bool
    if target_type is bool:
        val = value.lower().strip()
        if val in ('true', 'on', 'yes', '1'):
            return True
        elif val in ('false', 'off', 'no', '0'):
            return False
        else:
            raise Exception('The string "%s" can not be converted to a bool value. '
                            'Supported values are: true/false, on/off, 1/0, yes/no' % value)

    # list
    if target_type is list and type(value) != list:
        return list(value)

    # typed list
    if isinstance(default_value_or_type, ListOf):
        value = value if isinstance(value, list) else [value]
        return default_value_or_type.convert_list(value)

    return value


###############################################################################
# request handler for methods
###############################################################################

class FnHandler:
    def __init__(self, fn, url):
        self.log = logging.Logger(self.__class__.__name__)
        self.log.addHandler(default_log_handler)

        route_syntax_as_re = re.sub(r'/:(\w+)', r'/(?P<\1>\w+)', url + '([/?]|$)')
        self.url = url
        self.url_re = re.compile(route_syntax_as_re)
        self.fn = fn

        argspec = inspect.getargspec(fn)
        (argument_names, varargs, kwargs, defaults) = argspec
        if inspect.ismethod(fn):
            argument_names = argument_names[1:]

        self.has_kwargs = True if kwargs else False
        defaults = [] if defaults is None else defaults
        defaults = [_NOT_OPTIONAL] * (len(argument_names) - len(defaults)) + list(defaults)
        self.argnames_with_defaults = list(zip(argument_names, defaults))

    def _get_argument_value(self, argument_name, default_value_or_type, request_params):
        if argument_name in request_params:
            return _convert_to_type(request_params[argument_name], default_value_or_type)

        if default_value_or_type is not _NOT_OPTIONAL and type(default_value_or_type) != type:
            return default_value_or_type

        raise HTTPError(message='Could not find a value for parameter "%s"' % argument_name)

    def _call(self, params):
        values = []
        for (name, default) in self.argnames_with_defaults:
            values.append(self._get_argument_value(name, default, params))

        self.log.info('Calling handler "%s" with params %s', self.fn, params)
        if self.has_kwargs:
            return self.fn(*values, **params)
        else:
            return self.fn(*values)

    def create_callable_for_request(self, url, params):
        """
        :param url:
        :return: None, if this handler does not match the URL
        """
        match = self.url_re.match(url)
        if match:
            all_params = dict(**params)
            all_params.update(match.groupdict())
            return lambda: self._call(all_params)
        else:
            return None


###############################################################################
# core
###############################################################################

class WebHandler(object):
    def __init__(self, config=None):
        self.config = config if config else {}
        self._handlers = []
        self.app = None
        self.template_lookup = None
        self._static_dir = ''

        self.log = logging.Logger(self.__class__.__name__)
        self.log.addHandler(default_log_handler)
        #cherrypy.log.access_log.addHandler(default_log_handler)
        #cherrypy.log.error_log.addHandler(default_log_handler)

        cherrypy.config.update({'environment': 'embedded'})
        app = cherrypy.tree.mount(self)
        self.app = app
        conf = {
            '/': {
                'tools.sessions.on': True,
                'tools.sessions.timeout': 60
            },
            'server': {
                'deployment_mode': 'production'
            }
        }
        app.merge(conf)
        app.merge(self.config)

        if self.is_in_development_mode():
            self.log.info("Starting in development mode.")
            self._start_watchdog()
        else:
            self.log.info("Starting in production mode.")

    def _start_watchdog(self):
        pass

    def _encode_result(self, result):
        if type(result) in (set, list, dict):
            return json.dumps(result)
        else:
            return escape(str(result))

    def _load_static_file(self, path):
        if self._static_dir == '':
            return Exception("No directory for serving static files was configured.")

        if path.startswith('/'):
            path = path[1:]
        if path == '':
            path = 'index.html'
        if '..' in path or path.endswith('.mak'):
            raise HTTPError(message='Not allowed to access path "%s"' % path)

        if os.path.isdir(self._static_dir + path):
            path = os.path.join(path, 'index.html')

        # Check for Mako templates
        if path.endswith('.html') and not os.path.exists(self._static_dir + path):
            path_mak = path[:-5] + ".mak"
            if os.path.exists(self._static_dir + path_mak):
                template = self.template_lookup.get_template(path_mak)
                return template.render()

        return serve_file(self._static_dir + path)

    def is_in_development_mode(self):
        server_config = self.app.config.get('server', {})
        deployment_mode = server_config.get('deployment_mode', 'production')
        return deployment_mode == 'development'

    def set_static_dir(self, path):
        """
        Set the directory to serve static resources from.

        :param str path: the absolute path
        """
        if not os.path.isabs(path):
            raise Exception('Static directory path must be absolute!')

        if not path.endswith(os.path.sep):
            path += os.path.sep

        self.log.info('Using "%s" for static files and Mako templates', path)

        self._static_dir = path
        self.template_lookup = TemplateLookup(directories=[path], input_encoding='utf-8')

    def set_static_dir_relative_to_file(self, filename, dirname):
        self.set_static_dir(os.path.abspath(os.path.join(os.path.dirname(filename), dirname)))

    def add_route(self, url, handler_fn):
        self.log.info('Registering handler "%s" for URL "%s"', handler_fn, url)
        self._handlers.append(FnHandler(handler_fn, url))

    # noinspection PyUnusedLocal
    def default(self, *args, **kwargs):
        """ This method gets called by CherryPy.
        :param args:
        :param kwargs:
        """
        request_path = cherrypy.request.path_info
        resource = request_path.split('/')[-1]
        self.log.debug('Request: %s', request_path)

        # Serving static resource
        if '.' in resource or request_path.endswith('/'):
            self.log.debug('Serving static resources "%s".', request_path)
            return self._load_static_file(request_path)

        # Serving with handler
        kwargs['session'] = cherrypy.session
        kwargs['request'] = cherrypy.request

        handler_callables = []
        for handler in self._handlers:
            handler_callable = handler.create_callable_for_request(request_path, kwargs)
            if handler_callable:
                handler_callables.append(handler_callable)

        if len(handler_callables) > 1:
            raise HTTPError(message='More than one handler found to handle the URL "%s". Handlers: "%s"' %
                            (request_path, handler_callables))

        elif len(handler_callables) == 1:
            handler_callable = handler_callables[0]
            response = handler_callable()
            return self._encode_result(response)

        raise HTTPError(message='No handler was found.')

    default.exposed = True
