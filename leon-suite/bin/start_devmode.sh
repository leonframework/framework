#! /bin/bash

base=`dirname $0`
source $base"/vars.sh"


LEON_DEPLOYMENT_MODE=development exec $maven/"bin/mvn" "jetty:run"


