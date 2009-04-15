import lsst.ctrl.evmon.Chain as Chain
import lsst.ctrl.evmon.Condition as Condition
import lsst.ctrl.evmon.SetTask as SetTask
import lsst.ctrl.evmon.Template as Template
import lsst.ctrl.evmon.Relation as Relation
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter
import lsst.ctrl.evmon.EventTask as EventTask
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.EventMonitor as EventMonitor

# INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
#query = "INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values({$date}, {$msg:micros}, {$msg:PID});"

query = "SELECT (micros-'+str(micros)+')/1000000.0 as time, sliceid, level, log, comment, custom, hostid from logger where runid="'+runId+'" and micros >= '+str(micros)+' order by micros;"

chain = Chain()

cond1 = Condition("$msg:comment", Relation.STARTS_WITH, "Starting Stage Loop")
chain.addLink(cond1)
cond2 = Condition("$msg:comment", Relation.STARTS_WITH, "Completed Stage Loop")
chain.addLink(cond2)

setTask = SetTask("$time", "$msg[1]:time-$msg[0]:time")
chain.addLink(setTask)

template = Template()
template.put("INFO", Template.STRING, "Results for time delta")
template.put("TIME", Template.FLOAT, "$time")

outputWriter = ConsoleWriter()
eventTask = EventTask(outputWriter, template)
chain.addLink(eventTask)

mysqlReader = MysqlReader("ds33.ncsa.uiuc.edu", "events", "srp", "LSSTdata")
mysqlReader.setSelectString(query)
job = Job(mysqlReader, chain)

monitor = EventMonitor(job)
monitor.runJobs()

