# coding=utf-8

from __future__ import unicode_literals
import sys


# this string is used by tests
string_au_ou_ou = 'äöü'


# HTML escape
if sys.version_info >= (3, 2):
    import html
    escape = html.escape
else:
    import cgi
    # noinspection PyDeprecation
    escape = cgi.escape
