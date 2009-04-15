chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH,	"Starting Stage")
chain.addLink(cond1)

span = Span("1",3)


comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting node")
comp2 = LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX)
logicalAnd = LogicalAnd(comp1, comp2)

reqs2 = Condition(logicalAnd, span)
chain.addLink(reqs2)

task1 = SetTask("$micros", "$msg:micros")
chain.addLink(task1)

comp3 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending node")
comp4 = LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX)
logicalAnd2 = LogicalAnd(comp3, comp4)

exceptionTemplate = Template()
exceptionTemplate.put("INFO", Template.STRING, "Problem!")
exceptionTemplate.put("CPU", Template.INT, Span.INDEX)

exceptionWriter = LsstEventWriter("warning")
exceptionTask = ExceptionTask(exceptionWriter, exceptionTemplate)

reqs3 = Condition(logicalAnd2, span)
reqs3.setException(exceptionTask, 5000L)
chain.addLink(reqs3)

setTask = SetTask("$delta","$msg[2]:micros - $msg[1]:micros")
chain.addLink(setTask)

template = Template()
template.put("INFO", Template.STRING, "Results for delta")
template.put("DELTA", Template.INT, "$delta")

writer2 = LsstEventWriter("information")
eventTask = EventTask(writer2, template)
chain.addLink(eventTask)

outputWriter = ConsoleWriter()
eventTask2 = EventTask(outputWriter, template)
chain.addLink(eventTask2)

cond4 = Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage")
chain.addLink(cond4)

reader = LsstEventReader("monitor")
Job job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
