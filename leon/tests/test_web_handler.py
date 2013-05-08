# coding=utf-8

import os
from leon.py_2vs3_utils import string_au_ou_ou
from unittest import TestCase
from webtest import TestApp

import leon


class TestWebHandler(TestCase):
    def setUp(self):
        self.result = None
        self.web_handler = leon.create()
        self.app = TestApp(self.web_handler.app)

    def get(self, url):
        return self.app.get(url)

    # -------------------------------------------------------------------------
    # config tests
    # -------------------------------------------------------------------------

    def test_static_dir_must_be_absolute(self):
        def set_path():
            self.web_handler.set_static_dir("wrong/foo")

        self.assertRaises(Exception, set_path)

    def test_deployment_mode_default_is_production(self):
        self.assertFalse(self.web_handler.is_in_development_mode())

    def test_deployment_mode_set_to_development(self):
        conf = {
            'leon': {
                'deployment_mode': 'development'
            }
        }
        wh = leon.create(conf)
        self.assertTrue(wh.is_in_development_mode())

    # -------------------------------------------------------------------------
    # static access tests
    # -------------------------------------------------------------------------

    def test_static_file(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        res = self.get("/static.txt")
        self.assertIn('static file content', res.testbody)

    def test_set_static_dir_by_relative_filename(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, "files")
        res = self.get("/static.txt")
        self.assertIn('static file content', res.testbody)

    def test_static_file_404(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        self.assertRaises(Exception, lambda: self.get("/does_not_exist.txt"))

    def test_deny_parent_dir_access(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        self.assertRaises(Exception, lambda: self.get("/../static.txt"))

    def test_static_empty_path_to_index_html(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        res = self.get("")
        self.assertIn('index.html', res.testbody)

    def test_static_add_index_html_on_folder_access(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        res = self.get("/folder1/")
        self.assertIn('index.html in folder1', res.testbody)

    # -------------------------------------------------------------------------
    # route handling
    # -------------------------------------------------------------------------

    def test_no_match_raises_exception(self):
        def call():
            self.get("/nomatch")

        self.assertRaises(Exception, call)

    def test_simple_path(self):
        self.web_handler.add_route('/handler', lambda: "OK")
        res = self.get("/handler")
        self.assertEqual(res.testbody, 'OK')

    def test_handler_method_from_class(self):
        class Dummy:
            def handler(self):
                return 'handler'
        d = Dummy()
        self.web_handler.add_route('/handler', d.handler)
        res = self.get('/handler')
        self.assertEqual(res.testbody, 'handler')

    def test_overlapping_paths(self):
        self.web_handler.add_route('/handler', lambda: 'handler')
        self.web_handler.add_route('/handler_2', lambda: 'handler_2')
        res = self.get("/handler")
        self.assertEqual(res.testbody, 'handler')
        res = self.get("/handler/a")
        self.assertEqual(res.testbody, 'handler')
        res = self.get("/handler?a=b")
        self.assertEqual(res.testbody, 'handler')
        res = self.get("/handler_2")
        self.assertEqual(res.testbody, 'handler_2')
        res = self.get("/handler_2/a")
        self.assertEqual(res.testbody, 'handler_2')
        res = self.get("/handler_2?a=b")
        self.assertEqual(res.testbody, 'handler_2')

    def test_handler_with_default_values(self):
        def handler(var1=1, var2=2):
            self.result = [var1, var2]

        self.web_handler.add_route('/handler', handler)
        self.get("/handler")
        self.assertEqual(self.result, [1, 2])

    def test_regex_path(self):
        def handler():
            return "OK"

        self.web_handler.add_route('/han.*', handler)
        res = self.get("/handler")
        self.assertEqual(res.testbody, 'OK')

    def test_not_unique_handlers(self):
        def handler():
            return "OK"

        def call():
            self.get("/handler")

        self.web_handler.add_route('/handler.*', handler)
        self.web_handler.add_route('/handler.*', handler)

        self.assertRaises(Exception, call)

    def test_handler_with_1_route_param(self):
        def handler(var1):
            return var1

        self.web_handler.add_route('/handler/:var1', handler)
        res = self.get("/handler/value1")
        self.assertEqual(res.testbody, "value1")

    def test_handler_with_2_route_params(self):
        def handler(var1, var2):
            return var1 + "#" + var2

        self.web_handler.add_route('/handler/:var1/:var2', handler)
        res = self.get("/handler/value1/value2")
        self.assertEqual(res.testbody, "value1#value2")

    # -------------------------------------------------------------------------
    # Mako tests
    # -------------------------------------------------------------------------

    def test_mako_rendering(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        res = self.get('/mako/file1.html')
        self.assertIn('aaa', res.testbody)
        self.assertIn('bbb', res.testbody)
        self.assertIn('ccc', res.testbody)

    def test_mako_rendering_with_umlaute_in_file(self):
        self.web_handler.set_static_dir_relative_to_file(__file__, 'files')
        res = self.get('/mako/file_umlaute.html')
        self.assertIn(string_au_ou_ou, res.testbody)
