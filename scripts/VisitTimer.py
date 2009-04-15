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

runId = "SRP3505"
query = "SELECT nanos, id, sliceid, runid, level, log, comment, custom, hostid from logger where runid='"+runId+"' and log='harness.pipeline.visit' order by nanos;"

chain = Chain()

cond1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit")
cond3 = LogicalCompare("$msg:sliceid", Relation.EQUALS, "-1")
cond4 = LogicalCompare("$msg:runid", Relation.EQUALS, runId)
firstAnd = LogicalAnd(cond1, cond3)
firstAnd.add(cond4)
firstCondition = Condition(firstAnd)
chain.addLink(firstCondition)

setTask1 = SetTask("$firstLoop", "$msg:loopnum")
chain.addLink(setTask1)

setTask2 = SetTask("$nextLoop", "$msg:loopnum+1")
chain.addLink(setTask2)

comp1 = LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit")
comp2 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextloop")
comp3 = LogicalCompare("$msg:hostid", Relation.EQUALS, "$msg[0]:hostid")        

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)

cond2 = Condition(comp3)
chain.addLink(cond2)


setTask3 = SetTask("$time", "$msg[1]:micros-$msg[0]:micros")
chain.addLink(setTask3)

setTask4 = SetTask("$id","$msg:id")
chain.addLink(setTask4)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("host", Template.STRING, "$msg[1]:hostid")
template.put("id start", Template.INT, "$msg[0]:id")
template.put("id end  ", Template.INT, "$msg[1]:id")
template.put("TIME", Template.FLOAT, "$time")

outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)

mysqlReader = MysqlReader("ds33.ncsa.uiuc.edu", "test_events", "srp", "LSSTdata")
#mysqlReader.setFilter(new NormalizeMessageFilter("custom", "||", "~~"))

mysqlReader.setSelectString(query)
job = Job(mysqlReader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
