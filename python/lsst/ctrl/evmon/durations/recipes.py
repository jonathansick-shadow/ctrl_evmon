import sys, os, re

from lsst.ctrl.evmon import Chain, Condition, EventTask, Job, LogicalAnd
from lsst.ctrl.evmon import LogicalCompare, NormalizeMessageFilter, Relation
from lsst.ctrl.evmon import SetTask, MysqlTask, Template, EventMonitor

from lsst.ctrl.evmon.output import ConsoleWriter, MysqlWriter

insertTmpl = "INSERT INTO logs.%(destination)s (runid, name, sliceid, duration, host, loopnum, pipeline, date, stageid) values (%(runid)s, %(name)s, %(sliceid)s, %(duration)s, %(hostid)s, %(loopnum)s, %(pipeline)s, %(date)s, %(stageid)s);"

def DBWriteTask(data, authinfo, destination="durations"):
    """
    return the task that will write duration data to the output database
    table.
    @param data        a dictionary of values containing the database record
                          values.  The dictionary must include the following
                          (case-sensitive) keys:
                            runid       the run identifier
                            name        a name for the calculated duration
                            sliceid     the slide identifier
                            duration    the calculated duration value
                            hostid      the hostname where the slice was
                                          running
                            loopnum     the visit sequence number
                            pipeline    the name of the pipeline that the
                                          duration was calculated for
                            date        the formated date for the start of
                                          duration
                            stageid     the stage identifier
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    """
    if destination is not None:
        data["destination"] = destination;
    if not data.has_key('destination'):
        data["destination"] = "durations";
    insert = insertTmpl % data
    writer = MysqlWriter(authinfo["host"], "logs",
                         authinfo["user"], authinfo["password"])
    return MysqlTask(writer, insert)

def SliceBlockDurationChain(runid, logname, authinfo, destination="durations"):
    """
    return the Chain of conditions and tasks required to calculation the
    duration of the a traced block in the Slice harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = Chain()

    # find the start of the trace block
    start = Condition(LogicalCompare("$msg:STATUS", Relation.EQUALS, "start"))
    chain.addLink(start)

    chain.addLink(SetTask("$loopnum", "$msg:loopnum"))
    chain.addLink(SetTask("$startdate", "$msg:DATE"))

    # find the end of the trace block
    comp1 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "end");       
    comp2 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "$msg[0]:sliceId")
    comp3 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
    comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
    comp5 = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    comp6 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")
    
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
    recmatch.add(comp6)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runkId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}"  }

    chain.addLink(DBWriteTask(insertValues, authinfo, destination))
    return chain

def PipelineBlockDurationChain(runid, logname, authinfo,
                               destination="durations"):
    """
    calculate the durations for a particular block executed within Pipeline
    (master) process.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = Chain()

    # find the start of the trace block
    start = Condition(LogicalCompare("$msg:STATUS", Relation.EQUALS, "start"))
    chain.addLink(start)

    chain.addLink(SetTask("$loopnum", "$msg:loopnum"))
    chain.addLink(SetTask("$startdate", "$msg:date"))

    # find the end of the trace block
    comp1 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "end");       
    comp2 = LogicalCompare("$msg:sliceId", Relation.EQUALS, -1)
    comp3 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
    comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
    comp5 = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    comp6 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")
    
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
    recmatch.add(comp6)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runid}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceid}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostid}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}"  }

    chain.addLink(DBWriteTask(insertValues, authinfo, destination))
    return chain;

def LoopDurationChain(runid, authinfo, destination="durations"):
    """
    calculate the time required to complete each visit loop within the 
    master Pipeline process.
    harness code.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = Chain()

    # First log record: start of the visit
    cond1 = LogicalCompare("$msg:LOG",
                           Relation.EQUALS, "harness.pipeline.visit")
    cond2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "start")
    cond3 = LogicalCompare("$msg:sliceId", Relation.EQUALS, -1)
    cond4 = LogicalCompare("$msg:runId", Relation.EQUALS, runid)
    recmatch = LogicalAnd(cond1, cond2)
    recmatch.add(cond3)
    recmatch.add(cond4)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$loopnum", "$msg:loopnum"))
    chain.addLink(SetTask("$nextloop", "$msg:loopnum + 1"))

    # Next log records:  the next loop (same log message)
    cond2a = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextloop")
    cond2b = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    recmatch = LogicalAnd(cond1, cond2)
    recmatch.add(cond2a)
    recmatch.add(cond2b)
    recmatch.add(cond3)
    recmatch.add(cond4)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$startdate", "$msg[0]:date"))
    chain.addLink(SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP"))

    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}"   }

    chain.addLink(DBWriteTask(insertValues, authinfo, destination))
    return chain

