#!/bin/bash
#
#Create (or override!) a pom.xml file for each project in leon-samples.

BASEDIR="$(dirname $0)/.."
SAMPLESDIR="$BASEDIR/leon-samples"
POMTEMPLATE="$BASEDIR/project/sample-template.pom"

VERSION="$1"

if [ -z "$VERSION" ]; then
  echo "usage: gen-sample-pom.sh [version]"
  exit 1
fi

for SAMPLESRC in $(find $SAMPLESDIR -maxdepth 4 -name src -type d); do
  FULL_SAMPLEDIR=${SAMPLESRC%/src}
  SAMPLEDIR=${FULL_SAMPLEDIR##$SAMPLESDIR/}
  SAMPLENAME=$(echo $SAMPLEDIR | sed -e 's/\//-/g')
  
  echo "[$SAMPLENAME] Copying pom.xml to $SAMPLEDIR" 

  sed -e "s/{LEON_VERSION}/$VERSION/g" -e "s/{ARTIFACT_ID}/$SAMPLENAME/" $POMTEMPLATE > $FULL_SAMPLEDIR/pom.xml
done
