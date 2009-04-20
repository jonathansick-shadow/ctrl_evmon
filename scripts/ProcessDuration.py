import sys

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

runId = sys.argv[1]

logName = "harness.slice.visit.stage.process"
chain = Chain()

cond1 = LogicalCompare("$msg:LOG", Relation.EQUALS, logName)
cond2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "start")
cond3 = LogicalCompare("$msg:runId", Relation.EQUALS, runId)
firstAnd = LogicalAnd(cond1, cond2)
firstAnd.add(cond3)
firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask2 = SetTask("$nextLoop", "$msg:loopnum+1")
chain.addLink(setTask2)

setTask3 = SetTask("$startdate", "$msg:DATE")
chain.addLink(setTask3)

comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS, logName)
comp2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "end");       
comp3 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "$msg[0]:sliceId")
comp4 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
comp5 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
comp6 = LogicalCompare("$msg:hostId", Relation.EQUALS, "$msg[0]:hostId")
comp7 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)
logicalAnd1.add(comp4)
logicalAnd1.add(comp5)
logicalAnd1.add(comp6)
logicalAnd1.add(comp7)

cond2 = Condition(logicalAnd1)
chain.addLink(cond2)

setTask4 = SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP")
chain.addLink(setTask4)

setTask5 = SetTask("$id", "$msg:id")
chain.addLink(setTask5)

# for debug
#template = Template()
#template.put("INFO", Template.STRING, "Results for time delta")
#template.put("sliceId", Template.STRING, "$msg[0]:sliceId")
#template.put("runId", Template.STRING, "$msg[0]:runId")
#template.put("name", Template.STRING, "$msg[0]:LOG")
#template.put("duration", Template.INT, "$duration")
#template.put("host", Template.STRING, "$msg[0]:hostId")
#template.put("loopnum", Template.INT, "$msg[0]:loopnum")
#
#
#// write to console
#outputWriter = ConsoleWriter()
#eventTask = EventTask(outputWriter, template)
#chain.addLink(eventTask)

# write to database
query = "INSERT INTO test_events.durations(runid, name, sliceid, duration, host, loopnum, pipeline, date, stageid) values({$msg:runId}, {$msg:LOG}, {$msg:sliceId}, {$duration}, {$msg:hostId}, {$firstLoop}, {$msg:pipeline}, {$startdate}, {$msg:stageId});"
mysqlWriter = MysqlWriter("ds33", "test_events", "srp", "LSSTdata")
mysqlTask = MysqlTask(mysqlWriter, query)
chain.addLink(mysqlTask)

reader = LsstEventReader("LSSTLogging", "lsst4.ncsa.uiuc.edu")

job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
