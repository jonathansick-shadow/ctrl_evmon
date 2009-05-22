import os
import sys
from lsst.ctrl.evmon.auth import DbAuth

import lsst.ctrl.evmon.Chain as Chain
import lsst.ctrl.evmon.Condition as Condition
import lsst.ctrl.evmon.EventTask as EventTask
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.LogicalAnd as LogicalAnd
import lsst.ctrl.evmon.LogicalCompare as LogicalCompare
import lsst.ctrl.evmon.NormalizeMessageFilter as NormalizeMessageFilter
import lsst.ctrl.evmon.Relation as Relation
import lsst.ctrl.evmon.SetTask as SetTask
import lsst.ctrl.evmon.MysqlTask as MysqlTask
import lsst.ctrl.evmon.Template as Template
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter
import lsst.ctrl.evmon.output.MysqlWriter as MysqlWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor

host = "ds33.ncsa.uiuc.edu"

dbAuth = DbAuth.DbAuth()
auth = dbAuth.readAuthInfo(host)
if auth == None:
    print "Couldn't find matching entry for host="+host+" in db-auth.paf file"
    sys.exit(10)

user = auth['user']

runId = sys.argv[1]

chain = Chain()

cond1 = LogicalCompare("$msg:LOG", Relation.EQUALS, "harness.pipeline.visit")
cond2 = LogicalCompare("$msg:status", Relation.EQUALS, "start")
cond3 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "-1")
cond4 = LogicalCompare("$msg:runId", Relation.EQUALS, runId)
firstAnd = LogicalAnd(cond1, cond2)
firstAnd.add(cond3)
firstAnd.add(cond4)

firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask2 = SetTask("$nextLoop", "$msg:loopnum+1")
chain.addLink(setTask2)

setTask3 = SetTask("$startdate", "$msg:DATE")
chain.addLink(setTask3)


comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS, "harness.pipeline.visit")
comp2 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextLoop")
comp3 = LogicalCompare("$msg:hostId", Relation.EQUALS, "$msg[0]:hostId")
comp4 = LogicalCompare("$msg:runId", Relation.EQUALS, runId)
comp5 = LogicalCompare("$msg:status", Relation.EQUALS, "start")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)
logicalAnd1.add(comp4)
logicalAnd1.add(comp5)

cond2 = Condition(logicalAnd1)
chain.addLink(cond2)

setTask4 = SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP")
chain.addLink(setTask4)

setTask5 = SetTask("$id", "$msg:id")
chain.addLink(setTask5)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("runId", Template.STRING, runId)
template.put("name", Template.STRING, "$msg[0]:LOG")
template.put("sliceId", Template.STRING, "$msg[0]:sliceId")
template.put("duration", Template.INT, "$duration")
template.put("host", Template.STRING, "$msg[0]:hostId")
template.put("loopnum", Template.INT, "$msg[0]:loopnum")
template.put("DATE", Template.INT, "$msg[0]:DATE")


# write to console
outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)


# write to database
query = "INSERT INTO test_events.durations_loop_srptest(runId, name, sliceId, duration, host, loopnum, pipeline, date) values({$msg:runId}, {$msg:LOG}, {$msg:sliceId}, {$duration}, {$msg:hostId}, {$firstLoop}, {$msg:pipeline}, {$startdate});"
mysqlWriter = MysqlWriter(host, "test_events", auth['user'], auth['password'])
mysqlTask = MysqlTask(mysqlWriter, query)
chain.addLink(mysqlTask)

reader = LsstEventReader("LSSTLogging", "lsst4.ncsa.uiuc.edu")

job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
