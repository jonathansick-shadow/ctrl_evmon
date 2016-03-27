chain = Chain()

cond1 = Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
chain.addLink(cond1)

cond2 = Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline")
chain.addLink(cond2)

setTask = SetTask("$result", "$msg[1]:micros - $msg[0]:micros")
chain.addLink(setTask)

template = Template()

template.put("INFO", Template.STRING, "Results for delta")
template.put("DELTA", Template.INT, "$result")

writer = LsstEventWriter("monitor")
eventTask = EventTask(writer, template)
chain.addLink(eventTask)

reader = LsstEventReader("monitor")
job = new Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()
