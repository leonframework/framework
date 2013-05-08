# coding=utf-8

import os
import sys
import re
import inspect
import json
import cherrypy
from cherrypy import HTTPError
from cherrypy.lib.static import serve_file
from mako.lookup import TemplateLookup
from leon import log
from leon.py_2vs3_utils import escape
from leon.arg_conversions import NOT_OPTIONAL_MARKER, convert_to_type


class FnHandler:
    def __init__(self, fn, url):
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
        defaults = [NOT_OPTIONAL_MARKER] * (len(argument_names) - len(defaults)) + list(defaults)
        self.argnames_with_defaults = list(zip(argument_names, defaults))

    def _get_argument_value(self, argument_name, default_value_or_type, request_params):
        if argument_name in request_params:
            return convert_to_type(request_params[argument_name], default_value_or_type)

        if default_value_or_type is not NOT_OPTIONAL_MARKER and type(default_value_or_type) != type:
            return default_value_or_type

        raise HTTPError(message='Could not find a value for parameter "%s"' % argument_name)

    def _call(self, params):
        values = []
        for (name, default) in self.argnames_with_defaults:
            values.append(self._get_argument_value(name, default, params))

        log.debug('Calling handler "%s" with params %s', self.fn, params)
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


class WebHandler(object):
    def __init__(self, config=None):
        self.config = config if config else {}
        self._handlers = []
        self.app = None
        self.template_lookup = None
        self._file_change_watcher = None
        self._static_dir = ''
        self.json_encoder = json.dumps

        cherrypy.config.update({'environment': 'embedded'})

        app = cherrypy.tree.mount(self)
        self.app = app
        conf = {
            '/': {
                'tools.sessions.on': True,
                'tools.sessions.timeout': 60
            },
            'leon': {
                'deployment_mode': 'production'
            }
        }
        app.merge(conf)
        app.merge(self.config)

    def _encode_result(self, result):
        if type(result) in (set, list, dict):
            return self.json_encoder(result)
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

    def _handle_request(self, params):
        request_path = cherrypy.request.path_info
        resource = request_path.split('/')[-1]
        log.debug('Request: %s', request_path)

        # Serving static resource
        if '.' in resource or request_path.endswith('/'):
            log.debug('Serving static resource "%s".', request_path)
            return self._load_static_file(request_path)

        # Serving with handler
        if hasattr(cherrypy, 'session'):
            params['session'] = cherrypy.session
        params['request'] = cherrypy.request

        # find all matching handler
        handler_callables = []
        for handler in self._handlers:
            handler_callable = handler.create_callable_for_request(request_path, params)
            if handler_callable:
                handler_callables.append(handler_callable)

        # error, if not unique
        if len(handler_callables) > 1:
            raise HTTPError(message='More than one handler found to handle the URL "%s". Handlers: "%s"' %
                                    (request_path, handler_callables))

        # one handler found
        elif len(handler_callables) == 1:
            handler_callable = handler_callables[0]
            response = handler_callable()
            return self._encode_result(response)

        # no handler found
        raise HTTPError(message='No handler was found.')

    def is_in_development_mode(self):
        if '--development' in sys.argv:
            return True

        server_config = self.app.config.get('leon', {})
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

        log.info('Using "%s" for static files and Mako templates', path)

        self._static_dir = path
        self.template_lookup = TemplateLookup(directories=[path], input_encoding='utf-8')

        #if self.is_in_development_mode():
        #    self._file_change_watcher = FileChangeWatcher()
        #    self._file_change_watcher.start()

    def set_static_dir_relative_to_file(self, filename, dirname):
        self.set_static_dir(os.path.abspath(os.path.join(os.path.dirname(filename), dirname)))

    def add_route(self, url, handler_fn):
        log.info('Registering handler "%s" for URL "%s"', handler_fn, url)
        self._handlers.append(FnHandler(handler_fn, url))

    # noinspection PyUnusedLocal
    def default(self, *args, **kwargs):
        return self._handle_request(kwargs)
    default.exposed = True
