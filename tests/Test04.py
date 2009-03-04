from edu.uiuc.ncsa.monitor import *

from edu.uiuc.ncsa.monitor.engine import MessageEvent
from edu.uiuc.ncsa.monitor.engine import Template

chain = Chain()

chain.addLink(Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline"))
chain.addLink(Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline"))
chain.addLink(SetTask("$delta", "$msg[1]:micros - $msg[0]:micros"))

template = Template()
template.put("info", Template.STRING, "Results for delta")
template.put("delta", Template.INT, "$delta")
chain.addLink(EventTask(output, template))

reader = LsstEventReader("monitor", "localhost", 61616)
job = Job(reader, chain)

engine = Engine(job)
engine.runJobs()