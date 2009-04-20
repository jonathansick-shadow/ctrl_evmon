# -*- python -*-
#
# Setup our environment
#
import glob, os.path, re, sys
import lsst.SConsUtils as scons
import subprocess

dependencies = []

env = scons.makeEnv("ctrl_evmon",
                    r"$HeadURL$",
                    [
                    ])
env.Help("""
LSST Event Monitor package
""")

###############################################################################
# Boilerplate below here

pkg = env["eups_product"]
env.libs[pkg] += env.getlibs(" ".join(dependencies))

#
# Build/install things
#
env['IgnoreFiles'] = r"(~$|\.pyc$|^\.svn$|\.o$)"

Alias("install", [env.Install(env['prefix'], "python"),
                  env.Install(env['prefix'], "bin"),
                  env.Install(env['prefix'], "lib"),
                  env.Install(env['prefix'], "scripts"),
                  env.InstallEups(env['prefix'] + "/ups")])

classpath = [ os.path.join(os.environ['PWD'], "classes"),
              os.path.join(os.environ['PWD'],
                           "lib/mysql-connector-java-5.0.6-bin.jar"),
              os.path.join(os.environ['ACTIVEMQ_DIR'],
                           "activemq-all-5.2.0.jar") ]
              
env.Java("classes", "src", JAVACLASSPATH=classpath, ENV=os.environ)
#env.Jar("lib/evmon.jar", "classes", ENV=os.environ)

subprocess.call("jar cvf lib/evmon.jar -C classes/ .", shell=True)

scons.CleanTree(r"*~ core *.so *.os *.o *.class")

#
# Build TAGS files
#
files = scons.filesToTag()
if files:
    env.Command("TAGS", files, "etags -o $TARGET $SOURCES")

env.Declare()
