
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

dependencies:
	.virtualenv/bin/python setup.py develop

setup: virtualenv dependencies

upload:
	dev/upload.sh

start_demo1:
	.virtualenv/bin/python demos/demo1_simple_requests/start.py
