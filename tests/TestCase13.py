chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting Stage")
chain.addLink(cond1)

span = Span(1, 3)
comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting node")
comp2 = LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX)
comp3 = LogicalCompare("$msg:value", Relation.GREATER_THAN, 0)

logicalAnd1 = LogicalAnd(comp1, comp2)
logicalAnd1.add(comp3)

cond2 = Condition(logicalAnd1, span)
chain.addLink(cond2)

task1 = SetTask("$micros", "$msg:micros")
chain.addLink(task1)

comp4 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending node")
comp5 = LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX)
logicalAnd2 = LogicalAnd(comp4, comp5)

cond3 = Condition(logicalAnd2, span)
chain.addLink(cond3)

setTask = SetTask("$delta", "$msg[2]:micros - $msg[1]:micros")
chain.addLink(setTask)

template = Template()
template.put("INFO", Template.STRING, "Results for delta")
template.put("DELTA", Template.INT, "$delta")

lsstWriter = LsstEventWriter("warning")
eventTask = EventTask(lsstWriter, template)
chain.addLink(eventTask)

cond4 = Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage")
chain.addLink(cond4)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
