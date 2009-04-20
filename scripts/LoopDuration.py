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
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor

runId = sys.argv[1]

chain = Chain()

cond1 = LogicalCompare("$msg:LOG", Relation.EQUALS, "harness.pipeline.visit")
cond3 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "-1")
cond4 = LogicalCompare("$msg:runId", Relation.EQUALS, runId)
firstAnd = LogicalAnd(cond1, cond3)
firstAnd.add(cond4)
firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask2 = SetTask("$nextLoop", "$msg:loopnum+1")
chain.addLink(setTask2)

comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS, "harness.pipeline.visit")
comp2 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextLoop")
comp3 = LogicalCompare("$msg:hostId", Relation.EQUALS, "$msg[0]:hostId")

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)

cond2 = Condition(logicalAnd1)
chain.addLink(cond2)


setTask3 = SetTask("$time", "$msg[1]:nanos-$msg[0]:nanos")
chain.addLink(setTask3)

setTask4 = SetTask("$id","$msg:id")
chain.addLink(setTask4)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("name", Template.STRING, "$msg[1]:log")
template.put("host", Template.STRING, "$msg[1]:hostId")
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
