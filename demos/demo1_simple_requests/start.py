# coding=utf-8

import leon


def request1():
    return "Request 1"


def request2():
    return "Request 2"


app = leon.create()

app.set_static_dir_relative_to_file(__file__, '.')
app.add_route('/request1', request1)
app.add_route('/request2', request2)

leon.start_server(app)
