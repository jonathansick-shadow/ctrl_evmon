import lsst.ctrl.evmon.Chain as Chain
import lsst.ctrl.evmon.AttributeSet as AttributeSet
import lsst.ctrl.evmon.ExclusionFilterTask as ExclusionFilterTask
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.MysqlTask as MysqlTask
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader
import lsst.ctrl.evmon.output.MysqlWriter as MysqlWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor

query = "INSERT INTO logs.logger(hostId, runId, sliceId, LEVEL, LOG, DATE, TIMESTAMP, COMMENT, custom, STATUS, pipeline) values({$msg:hostId}, {$msg:runId}, {$msg:sliceId}, {$msg:LEVEL}, {$msg:LOG}, {$msg:DATE}, {$msg:TIMESTAMP}, {$msg:COMMENT}, {$custom}, {$msg:STATUS}, {$msg:pipeline});"

chain = Chain()

mysqlWriter = MysqlWriter("lsst10", "logs", "rplante", "net.wadr")

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

reader = LsstEventReader("LSSTLogging", "lsst4.ncsa.uiuc.edu")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()

