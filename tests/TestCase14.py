# INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
query = "INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values({$date}, {$msg:micros}, {$msg:PID});"

chain = Chain()

mysqlWriter = MysqlWriter("localhost", "events", "srp", "srp123")

setTask = SetTask("$date", "$msg:DATE")
chain.addLink(setTask)

mysqlTask = MysqlTask(mysqlWriter, query)
chain.addLink(mysqlTask)

reader = LsstEventReader("monitor")
job = Job(reader, chain)

monitor = EventMonitor(job)
monitor.runJobs()

