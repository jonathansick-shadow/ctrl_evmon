from edu.uiuc.ncsa.monitor import *

from edu.uiuc.ncsa.monitor.engine import Template;
from edu.uiuc.ncsa.monitor.engine import Engine;

chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH,    "Starting Stage")
chain.addLink(cond1)

span = Span(1,3)

exprs2 = Expressions()
exprs2.add(Expression("$msg:info", Relation.STARTS_WITH, "Starting node"))
exprs2.add(Expression("$msg:CPU", Relation.EQUALS, Span.INDEX))

cond2 = Condition(exprs2, span)
chain.addLink(cond2)
        

exprs3 = Expressions()
exprs3.add(Expression("$msg:info", Relation.STARTS_WITH, "Ending node"))
exprs3.add(Expression("$msg:CPU", Relation.EQUALS, Span.INDEX))

cond3 = Condition(exprs3, span)        
chain.addLink(cond3);

setTask = SetTask("$delta","$msg[2]:micros - $msg[1]:micros")
chain.addLink(setTask)
        
template = Template()
template.put("INFO", Template.STRING, "Results for delta")
template.put("DELTA", Template.INT, "$delta")

writer = LsstEventWriter("info", "fester.ncsa.uiuc.edu")
eventTask = EventTask(writer, template)
chain.addLink(eventTask)
    
cond4 = Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage")
chain.addLink(cond4);

reader = LsstEventReader("monitor")        
job = Job(reader, chain)

engine = Engine(job)
engine.runJobs()