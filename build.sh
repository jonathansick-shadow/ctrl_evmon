#!/bin/sh

BUILDCLASSPATH=$PWD/lib/jars/mysql-connector-java-5.0.6-bin.jar:$ACTIVEMQ_DIR/activemq-all-5.2.0.jar
CLASSDIR=$PWD/classes

javac -cp $BUILDCLASSPATH:$PWD/src -d $CLASSDIR src/lsst/ctrl/evmon/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $CLASSDIR src/lsst/ctrl/evmon/input/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $CLASSDIR src/lsst/ctrl/evmon/test/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $CLASSDIR src/lsst/ctrl/evmon/engine/*java

cd $CLASSDIR
rm evmon.jar
jar cvf evmon.jar lsst
