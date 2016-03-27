from lsst.ctrl.evmon import *

from lsst.ctrl.evmon.input import LsstEventReader
from lsst.ctrl.evmon.engine import MessageEvent


chain = Chain()

comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
comp2 = LogicalCompare("$msg:DATE", Relation.STARTS_WITH, "2009")
logicalAnd = LogicalAnd(comp1, comp2)
reqs = Condition(logicalAnd)

chain.addLink(reqs)

req2 = Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline")
chain.addLink(req2)

reader = LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu")
job = Job(reader, chain)

engine = EventMonitor(job)
engine.runJobs()
