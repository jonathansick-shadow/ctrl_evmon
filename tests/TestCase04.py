from edu.uiuc.ncsa.monitor import *

from edu.uiuc.ncsa.monitor.engine import MessageEvent
from edu.uiuc.ncsa.monitor.engine import Template

       
chain = Chain()

cond1 = Condition("$msg:info", Condition.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)
        
cond2 = Condition("$msg:info", Condition.STARTS_WITH, "Ending pipeline")
chain.addLink(cond2)
           
setTask = SetTask("$delta", "$msg[1]:micros - $msg[0]:micros")
chain.addLink(setTask)

template = Template()
template.put("info", Template.STRING, "Results for delta")
template.put("delta", Template.INT, "$delta")

writer = LsstEventWriter("warning", "localhost", 61616)
eventTask = EventTask(writer, template)
chain.addLink(eventTask)

reader = LsstEventReader("monitor")        
job = Job(reader, chain)

engine = EventMonitor(job)
engine.runJobs()
