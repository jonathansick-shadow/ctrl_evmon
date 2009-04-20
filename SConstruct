# -*- python -*-
# Setup our environment#
import glob, os.path, re, os
import lsst.SConsUtils as scons

classpath = [ os.path.join(os.environ['PWD'], "classes"),
             os.path.join(os.environ['PWD'],
                          "lib/mysql-connector-java-5.0.6-bin.jar"),
             os.path.join(os.environ['ACTIVEMQ_DIR'],
                          "activemq-all-5.2.0.jar") ]

env.Java("classes", "src", JAVACLASSPATH=classpath, ENV=os.environ)
env.Jar("lib/evmon.jar", "classes", ENV=os.environ)
