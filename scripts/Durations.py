import getopt
import sys
import Loop
import StopEnd
import lsst.ctrl.evmon.EventMonitor as EventMonitor
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.NormalizeMessageFilter as NormalizeMessageFilter
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader 

usage = """usage: evmon """+sys.argv[0]+""" runId"""

dbHost = "ds33.ncsa.uiuc.edu"
dbName = "test_events"

# the runId we use comes from the command line
runId = sys.argv[1]

loop = Loop.Loop(runId)
stopend = StopEnd.StopEnd(runId)

chain1 = loop.getLoopDurationChain()
chain2 = stopend.getProcessDurationChain()
chain3 = stopend.getEventWaitDurationChain()

#
# Loop Duration
#
loopQuery = "SELECT date, nanos, id, sliceid, runid, level, log, comment, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.pipeline.visit' order by nanos;"
reader1 = MysqlReader(dbHost, dbName, "srp", "LSSTdata")

# filter the "custom" field and turn it's entry into message key/value pairs
reader1.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader1.setSelectString(loopQuery)
job1 = Job(reader1, chain1)

#
# Process Duration
#
processQuery = "SELECT date, nanos, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.slice.visit.stage.process' order by nanos;"
reader2 = MysqlReader(dbHost, dbName, "srp", "LSSTdata")
reader2.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader2.setSelectString(processQuery)
job2 = Job(reader2, chain2)

#
# Process Duration
#
eventwaitQuery = "SELECT date, nanos, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.slice.visit.stage.handleEvents.eventwait' order by nanos;"
reader3 = MysqlReader(dbHost, dbName, "srp", "LSSTdata")
reader3.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader3.setSelectString(eventwaitQuery)
job3 = Job(reader3, chain3)

monitor = EventMonitor(job1)
monitor.addJob(job2)
monitor.addJob(job3)

monitor.runJobs()
