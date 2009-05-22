import os
import getopt
import sys
import Loop
import StopEnd

from lsst.ctrl.evmon.auth import DbAuth

import lsst.ctrl.evmon.EventMonitor as EventMonitor
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.NormalizeMessageFilter as NormalizeMessageFilter
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader 

usage = """usage: evmon """+sys.argv[0]+""" runId"""

dbHost = "fester.ncsa.uiuc.edu"
dbName = "logs"


dbAuth = DbAuth.DbAuth()
auth = dbAuth.readAuthInfo(dbHost)
if auth == None:
    print "Couldn't find matching entry for host="+host+" in db-auth.paf file"
    sys.exit(10)

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
loopQuery = "SELECT date, TIMESTAMP, id, sliceid, runid, level, log, comment, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.pipeline.visit' order by TIMESTAMP;"
reader1 = MysqlReader(dbHost, dbName, auth['user'], auth['password'])

# filter the "custom" field and turn it's entry into message key/value pairs
reader1.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader1.setSelectString(loopQuery)
job1 = Job(reader1, chain1)

#
# Process Duration
#
processQuery = "SELECT date, TIMESTAMP, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.slice.visit.stage.process' order by TIMESTAMP;"
reader2 = MysqlReader(dbHost, dbName, auth['user'], auth['password'])
reader2.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader2.setSelectString(processQuery)
job2 = Job(reader2, chain2)

#
# Process Duration
#
eventwaitQuery = "SELECT date, TIMESTAMP, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='" + runId + "' and log='harness.slice.visit.stage.handleEvents.eventwait' order by TIMESTAMP;"
reader3 = MysqlReader(dbHost, dbName, auth['user'], auth['password'])
reader3.setFilter(NormalizeMessageFilter("custom", "=", ";"))
reader3.setSelectString(eventwaitQuery)
job3 = Job(reader3, chain3)

monitor = EventMonitor(job1)
monitor.addJob(job2)
monitor.addJob(job3)

monitor.runJobs()
