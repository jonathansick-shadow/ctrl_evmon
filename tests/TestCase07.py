chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)


comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending pipeline")
comp2 = LogicalCompare("$msg:HOST", Relation.EQUALS, "$msg[0]:HOST")

logicalAnd = LogicalAnd(comp1, comp2)
reqs2 = Condition(logicalAnd)
chain.addLink(reqs2)

template = Template()
template.put("info", Template.STRING, "packet number")
template.put("packet", Template.STRING, "DONE")
template.put("host", Template.STRING, "$msg[0]:HOST")

writer = LsstEventWriter("monitor")
eventTask = EventTask(writer, template)

chain.addLink(eventTask)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
