import os
import sys
from lsst.ctrl.evmon.auth import DbAuth


import lsst.ctrl.evmon.Chain as Chain
import lsst.ctrl.evmon.AttributeSet as AttributeSet
import lsst.ctrl.evmon.ExclusionFilterTask as ExclusionFilterTask
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.MysqlTask as MysqlTask
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader
import lsst.ctrl.evmon.output.MysqlWriter as MysqlWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor
import lsst.ctrl.evmon.db as db
import sys

broker = "lsst4.ncsa.uiuc.edu"
host = "lsst10.ncsa.uiuc.edu"
port = "3306"

if len(sys.argv) > 1:
    broker = sys.argv[1]
    if broker.find('.') < 0:
        print "broker =", broker
        broker += ".ncsa.uiuc.edu"
if len(sys.argv) > 2:
    host = sys.argv[2]
if len(sys.argv) > 3:
    port = sys.argv[3]


query = "INSERT INTO logs.logger(hostId, runId, sliceId, LEVEL, LOG, DATE, TIMESTAMP, COMMENT, custom, STATUS, pipeline) values({$msg:hostId}, {$msg:runId}, {$msg:sliceId}, {$msg:LEVEL}, {$msg:LOG}, {$msg:DATE}, {$msg:TIMESTAMP}, {$msg:COMMENT}, {$custom}, {$msg:STATUS}, {$msg:pipeline});"

dbAuth = DbAuth.DbAuth()

auth = dbAuth.readAuthInfo(host,port)

if auth == None:
    print "Couldn't find matching entry for host="+host+", port="+port+" in db-auth.paf file"
    sys.exit(10)

chain = Chain()

mysqlWriter = MysqlWriter(host, "logs", auth['user'], auth['password'])

attSet = AttributeSet()
attSet.put("hostId")
attSet.put("runId")
attSet.put("sliceId")
attSet.put("LEVEL")
attSet.put("LOG")
attSet.put("DATE")
attSet.put("COMMENT")
attSet.put("TIMESTAMP")
attSet.put("STATUS")
attSet.put("pipeline")
				
filterTask = ExclusionFilterTask("$custom", attSet)
chain.addLink(filterTask)
				
mysqlTask = MysqlTask(mysqlWriter, query)
chain.addLink(mysqlTask)

reader = LsstEventReader("LSSTLogging", broker)
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()

