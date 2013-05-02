
test:
	.virtualenv/bin/py.test --pep8

clean:
	rm -rf .virtualenv
	rm -rf .cache
	rm -rf build
	rm -rf dist
	rm -rf *.egg-info

setup:
	virtualenv --no-site-packages --distribute -p python3 --prompt=ENV .virtualenv

dependencies:
	.virtualenv/bin/python setup.py develop

upload:
	dev/upload.sh
