chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)

exceptionTemplate = Template()
exceptionTemplate.put("INFO", Template.STRING, "Problem!")

writer = LsstEventWriter("warning")
exceptionTask = ExceptionTask(writer, exceptionTemplate)

cond2 = Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline")
cond2.setException(exceptionTask, 5000L)
chain.add(cond2)

logicalCompare = LogicalCompare("$msg[1]:micros", Relation.LESS_THAN, 0)
assertion = Assertion(logicalCompare, exceptionTask)
chain.addLink(assertion)

setTask = SetTask("$result", "$msg[1]:micros - $msg[0]:micros")
chain.addLink(setTask)

template = Template
template.put("INFO", Template.STRING, "Results for delta")
template.put("DELTA", Template.INT, "$result")

writer2 = LsstEventWriter("data")
eventTask = EventTask(writer2, template)

chain.addLink(eventTask)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
