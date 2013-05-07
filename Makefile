
test:
	.virtualenv/bin/py.test --pep8
	.virtualenv/bin/tox

clean:
	rm -rf .virtualenv
	rm -rf .cache
	rm -rf build
	rm -rf dist
	rm -rf *.egg-info
	rm -rf doc/_build

virtualenv:
	python3 -m virtualenv --distribute --prompt=ENV .virtualenv
	.virtualenv/bin/pip install tox
	.virtualenv/bin/pip install Sphinx
	.virtualenv/bin/pip install pytest
	.virtualenv/bin/pip install pytest-pep8
	.virtualenv/bin/pip install pytest-xdist

setup: clean virtualenv dependencies

dependencies:
	.virtualenv/bin/python setup.py develop

doc_html:
	.virtualenv/bin/sphinx-build -b html -E doc doc/_build

upload:
	dev/upload.sh

start_demo1:
	.virtualenv/bin/python demos/demo1_simple_requests/start.py
