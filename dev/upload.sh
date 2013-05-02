#! /bin/bash

.virtualenv/bin/py.test --pep8 $*
if [ $? -eq 1 ]; then
    echo "Failing tests. No Upload!"
else
    .virtualenv/bin/python setup.py sdist bdist_egg upload
fi
