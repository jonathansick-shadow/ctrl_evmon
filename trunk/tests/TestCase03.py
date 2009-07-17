chain = Chain()

comp1 = LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting pipeline")
comp2 = LogicalCompare("$msg:DATE", Relation.STARTS_WITH, "2008")
logicalAnd = LogicalAnd(comp1, comp2)

cond = Condition(logicalAnd)
chain.addLink(cond)

reader = LsstEventReader("LSSTLogging")
job = Job(reader, chain)

engine = EventMonitor(job)
engine.runJobs()
