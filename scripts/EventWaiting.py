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
import lsst.ctrl.evmon.Template as Template
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter

runId = sys.argv[1]

chain = Chain()

cond1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.slice.visit.stage.handleEvents.eventwait")
cond2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "start")
cond3 = LogicalCompare("$msg:runid", Relation.EQUALS, runId)
firstAnd = LogicalAnd(cond1, cond2)
firstAnd.add(cond3)

firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

comp1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.slice.visit.stage.handleEvents.eventwait")
comp2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "end")
comp3 = LogicalCompare("$msg:runid", Relation.EQUALS, runId)
comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
comp5 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)
logicalAnd1.add(comp4)
logicalAnd1.add(comp5)

cond2 = Condition(logicalAnd1)
chain.addLink(cond2)


setTask3 = SetTask("$time", "$msg[1]:micros-$msg[0]:micros")
chain.addLink(setTask3)

setTask4 = SetTask("$id","$msg:id")
chain.addLink(setTask4)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("name", Template.STRING, "$msg[1]:log")
template.put("host", Template.STRING, "$msg[1]:hostid")
template.put("id start", Template.INT, "$msg[0]:id")
template.put("id end  ", Template.INT, "$msg[1]:id")
template.put("TIME", Template.FLOAT, "$time")

outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)

reader = LsstEventReader("LSSTLogging", "lsst4.ncsa.uiuc.edu");

job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
