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

logName = ""
dbTableName = ""

usage = """script runId process  or script runId eventwait"""

if sys.argv[2] == "process":
    logName = "harness.slice.visit.stage.process"
    dbTableName = "durations_process"
elif sys.argv[2] == "eventwait":
    logName = "harness.slice.visit.stage.handleEvents.eventwait"
    dbTableName = "durations_eventwait"
else:
    print usage
    sys.exit(2)

query = "SELECT date, nanos, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='"+logName+"' order by nanos;"


chain = Chain()

cond3 = LogicalCompare("$msg:status", Relation.EQUALS, "start")
firstCondition = Condition(cond3)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask3 = SetTask("$startdate", "$msg:date")
chain.addLink(setTask3)

comp1 = LogicalCompare("$msg:status", Relation.EQUALS, "end");       
comp2 = LogicalCompare("$msg:sliceid", Relation.EQUALS, "$msg[0]:sliceid")
comp3 = LogicalCompare("$msg:runid", Relation.EQUALS, "$msg[0]:runid")
comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
comp5 = LogicalCompare("$msg:hostid", Relation.EQUALS, "$msg[0]:hostid")
comp6 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)
logicalAnd1.add(comp4)
logicalAnd1.add(comp5)
logicalAnd1.add(comp6)

cond2 = Condition(logicalAnd1)
chain.addLink(cond2)

setTask4 = SetTask("$duration", "$msg[1]:nanos-$msg[0]:nanos")
chain.addLink(setTask4)

setTask5 = SetTask("$id", "$msg:id")
chain.addLink(setTask5)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("sliceId", Template.STRING, "$msg[0]:sliceid")
template.put("runId", Template.STRING, "$msg[0]:runid")
template.put("name", Template.STRING, "$msg[0]:log")
template.put("duration", Template.INT, "$duration")
template.put("host", Template.STRING, "$msg[0]:hostid")
template.put("loopnum", Template.INT, "$msg[0]:loopnum")
template.put("stageId", Template.INT, "$msg[0]:stageId")


# write to console
outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)

# write to database
insertQuery = "INSERT INTO logs.durations (runid, name, sliceid, duration, host, loopnum, pipeline, date, stageid) values({$msg:runid}, {$msg:log}, {$msg:sliceid}, {$duration}, {$msg:hostid}, {$firstLoop}, {$msg:pipeline}, {$startdate}, {$msg:stageId});"

mysqlWriter = MysqlWriter("lsst10", "logs", "rplante", "net.wadr");
mysqlTask = MysqlTask(mysqlWriter, insertQuery)
chain.addLink(mysqlTask)
    
mysqlReader = MysqlReader("lsst10.ncsa.uiuc.edu", "logs", "rplante", "net.wadr")
mysqlReader.setFilter(NormalizeMessageFilter("custom", "=", ";"))

mysqlReader.setSelectString(query)

job = Job(mysqlReader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
