from lsst.ctrl.evmon import *

from lsst.ctrl.evmon.engine import MessageEvent

chain = Chain()

chain.addLink(Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline"))
chain.addLink(Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline"))
chain.addLink(SetTask("$delta", "$msg[1]:micros - $msg[0]:micros"))

template = Template()
template.put("info", Template.STRING, "Results for delta")
template.put("delta", Template.INT, "$delta")
chain.addLink(EventTask(output, template))

reader = LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu", 61616)
job = Job(reader, chain)

engine = Engine(job)
engine.runJobs()
