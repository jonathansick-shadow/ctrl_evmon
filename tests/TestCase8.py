from lsst.ctrl.evmon import *

from lsst.ctrl.evmon.engine import Template
from lsst.ctrl.evmon.engine import Engine

chain = Chain()

cond1 = Condition("$msg:info", Condition.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)

exceptionTemplate = Template()
exceptionTemplate.put("INFO", Template.STRING, "Problem!")

exceptionWriter = LsstEventWriter("warning", "lsst8.ncsa.uiuc.edu")
exceptionTask = ExceptionTask(exceptionWriter, exceptionTemplate)

conditions = Conditions()
cond2a = Condition("$msg:info", Condition.STARTS_WITH, "Ending pipeline")
cond2b = Condition("$msg:HOST", Condition.EQUAL, "$msg[0]:HOST")
conditions.add(cond2a)
conditions.add(cond2b)
conditions.setException(exceptionTask, 5000L)
chain.addLink(conditions)


setTask = SetTask("$result", "$msg[1]:micros - $msg[0]:micros")
chain.addLink(setTask)

template = Template()
template.put("info", Template.STRING, "Pipeline time in microseconds")
template.put("micros", Template.INT, "$result")

writer = LsstEventWriter("info", "fester.ncsa.uiuc.edu")
eventTask = EventTask(writer, template)
chain.addLink(eventTask)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

engine = Engine(simple)
engine.runJobs()
