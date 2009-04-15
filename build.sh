#!/bin/sh
setup activemq
#for i in `ls $ACTIVEMQ_DIR/lib`
#do
#CP=$CP:$ACTIVEMQ_DIR/lib/$i
#done
#echo $CP

BUILDCLASSPATH=$PWD/lib/jars/mysql-connector-java-5.0.6-bin.jar:$ACTIVEMQ_DIR/activemq-all-5.2.0.jar
#export BUILDCLASSPATH
javac -cp $BUILDCLASSPATH:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/input/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/test/*java
javac -Xlint:unchecked -cp $BUILDCLASSPATH:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/engine/*java
#javac -cp $CP:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/*java
#javac -Xlint:unchecked -cp $CP:$PWD/src -d $PWD/bin src/lsst/ctrl/evmon/input/*java

cd bin
rm evmon.jar
jar cvf evmon.jar lsst
