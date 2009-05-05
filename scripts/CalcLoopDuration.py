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
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter
import lsst.ctrl.evmon.output.MysqlWriter as MysqlWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor

runId = sys.argv[1]
query = "SELECT date, nanos, id, sliceid, runid, level, log, comment, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.pipeline.visit' order by nanos;"

chain = Chain()

cond1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit")
cond2 = LogicalCompare("$msg:status", Relation.EQUALS, "start")
cond3 = LogicalCompare("$msg:sliceid", Relation.EQUALS, "-1")
cond4 = LogicalCompare("$msg:runid", Relation.EQUALS, runId)

firstAnd = LogicalAnd(cond1, cond2)
firstAnd.add(cond3)
firstAnd.add(cond4)
firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask2 = SetTask("$nextLoop", "$msg:loopnum+1")
chain.addLink(setTask2)
setTask2a = SetTask("$host", "$msg:hostid")
chain.addLink(setTask2a)

comp1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit")
comp2 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextLoop")
comp3 = LogicalCompare("$msg:hostid", Relation.EQUALS, "$host")
comp4 = LogicalCompare("$msg:status", Relation.EQUALS, "start")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)
logicalAnd1.add(comp4)

condi2 = Condition(logicalAnd1)
chain.addLink(condi2)

startDate = SetTask("$startdate", "$msg[0]:date")
chain.addLink(startDate)

setTask3 = SetTask("$duration", "$msg[1]:nanos-$msg[0]:nanos")
chain.addLink(setTask3)

setTask4 = SetTask("$id","$msg:id")
chain.addLink(setTask4)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("date", Template.STRING, "$msg[0]:date")
template.put("host", Template.STRING, "$msg[1]:hostid")
template.put("id start", Template.INT, "$msg[0]:id")
template.put("id end  ", Template.INT, "$msg[1]:id")
template.put("TIME", Template.INT, "$duration")
template.put("pipeline", Template.STRING, "$msg[1]:pipeline")

outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)

# write to database

insertQuery = "INSERT INTO logs.durations(runid, name, sliceid, duration, host, loopnum, pipeline, date) values({$msg:runid}, {$msg:log}, {$msg:sliceid}, {$duration}, {$msg:hostid}, {$firstLoop}, {$msg:pipeline}, {$startdate});"
mysqlWriter = MysqlWriter("lsst10", "logs", "rplante", "net.wadr")
mysqlTask = MysqlTask(mysqlWriter, insertQuery)
chain.addLink(mysqlTask)


mysqlReader = MysqlReader("lsst10", "logs", "rplante", "net.wadr")
mysqlReader.setFilter(NormalizeMessageFilter("custom", "=", ";"))

mysqlReader.setSelectString(query)
job = Job(mysqlReader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
