Chain chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)

exceptionTemplate = Template()
exceptionTemplate.put("INFO", Template.STRING, "Problem!")

exceptionWriter = LsstEventWriter("monitor")
exceptionTask = ExceptionTask(exceptionWriter, exceptionTemplate)


comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending pipeline")
comp2 = LogicalCompare("$msg:HOST", Relation.EQUALS, "$msg[0]:HOST")
logicalAnd = LogicalAnd(comp1, comp2)

reqs2 = Condition(logicalAnd)
reqs2.setException(exceptionTask, 5000L)
chain.addLink(reqs2)
        
setTask = SetTask("$result", "$msg[1]:micros - $msg[0]:micros")
chain.addLink(setTask)
        
template = Template()
template.put("info", Template.STRING, "Pipeline time in microseconds")
template.put("micros", Template.INT, "$result")

writer = LsstEventWriter("information")
eventTask = EventTask(writer, template)

chain.addLink(eventTask)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
