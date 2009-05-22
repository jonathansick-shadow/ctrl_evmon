from lsst.ctrl.evmon import *

from lsst.ctrl.evmon.input import LsstEventReader
from lsst.ctrl.evmon.engine import MessageEvent

       
chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting Stage")
chain.addLink(cond1)
        
cond2 = Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage")
chain.addLink(cond2)
           
reader = LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu")        
job = Job(reader, chain)

engine = EventMonitor(job)
engine.runJobs()
