
test:
	.virtualenv/bin/py.test --pep8

clean:
	rm -rf .virtualenv
	rm -rf .cache
	rm -rf build
	rm -rf dist
	rm -rf *.egg-info

virtualenv:
	virtualenv --no-site-packages --distribute -p python3 --prompt=ENV .virtualenv
	.virtualenv/bin/pip install Sphinx
	.virtualenv/bin/pip install sphinx-pypi-upload
	.virtualenv/bin/pip install pytest
	.virtualenv/bin/pip install pytest-pep8
	.virtualenv/bin/pip install pytest-xdist

dependencies:
	.virtualenv/bin/python setup.py develop

setup: virtualenv dependencies

upload:
	dev/upload.sh

start_demo1:
	.virtualenv/bin/python demos/demo1_simple_requests/start.py
