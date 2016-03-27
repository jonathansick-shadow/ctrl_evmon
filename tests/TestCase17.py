from lsst.ctrl.evmon import *

from lsst.ctrl.evmon.engine import Template
from lsst.ctrl.evmon.engine import Engine

chain = Chain()

query = "INSERT INTO events.logger(micros, hostid, sliceid, level, log, custom, comment, runid) values({$msg:micros}, {$msg:hostid}, {$msg:sliceid}, {$msg:level}, {$msg:log}, {$msg:custom}, {$msg:comment}, {$msg:runid});"

mysqlWriter = MysqlWriter("localhost", "events", "srp", "srp123")

mysqlTask = MysqlTask(mysqlWriter, query)
chain.addLink(mysqlTask)

reader = LsstEventReader("LSSTLogging", "ds33.ncsa.uiuc.edu", 61616)
job = Job(reader, chain)

engine = Engine(simple)
engine.runJobs()
