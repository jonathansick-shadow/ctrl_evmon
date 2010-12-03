import sys, os, re

from lsst.ctrl.evmon import Chain, Condition, EventTask, Job, LogicalAnd
from lsst.ctrl.evmon import LogicalCompare, NormalizeMessageFilter, Relation
from lsst.ctrl.evmon import SetTask, MysqlTask, Template, EventMonitor

from lsst.ctrl.evmon.output import ConsoleWriter, MysqlWriter

insertTmpl = "INSERT INTO %(dbname)s.%(durtable)s (runid, name, sliceid, duration, hostid, loopnum, pipeline, start, stageid, comment, workerid, userduration, systemduration, stagename) values (%(runid)s, %(name)s, %(sliceid)s, %(duration)s, %(hostid)s, %(loopnum)s, %(pipeline)s, %(date)s, %(stageid)s, %(comment)s, %(workerid)s, %(userduration)s, %(systemduration)s, %(stagename)s);"

def DBWriteTask(data, authinfo, dbname, durtable):
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

    """
    data["dbname"] = dbname
    data["durtable"] = durtable
    insert = insertTmpl % data

    writer = MysqlWriter(authinfo["host"], dbname,
                         authinfo["user"], authinfo["password"])
    return MysqlTask(writer, insert)

def GenericBlockDurationChain(runid, logname, authinfo, dbname, durtable, console):
    """
    return the Chain of conditions and tasks required to calculation the
    duration of the a traced block in the Slice harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param dbname      the database to insert this duration information into
    @param console     boolean indicating to send this output to the console
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
    comp2 = LogicalCompare("$msg:workerid",Relation.EQUALS,"$msg[0]:workerid")
    
    recmatch = LogicalAnd(comp1, comp2)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$duration", "$msg[1]:PUBTIME-$msg[0]:PUBTIME"))
    chain.addLink(SetTask("$userduration", "$msg[1]:usertime-$msg[0]:usertime"))
    chain.addLink(SetTask("$userduration", "$userduration*1000.0"))
    chain.addLink(SetTask("$systemduration", "$msg[1]:systemtime-$msg[0]:systemtime"))
    chain.addLink(SetTask("$systemduration", "$systemduration*1000.0"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}",
                     "workerid":  "{$msg:workerid}",
                     "userduration": "{$userduration}",
                     "systemduration": "{$systemduration}",
                     "stagename": "{$msg:stagename}",
                     "comment":  "{$msg:COMMENT}"  }

    if console == True:
        eventTask2 = consoleTask()
        chain.addLink(eventTask2)
    else:
        chain.addLink(DBWriteTask(insertValues, authinfo, dbname, durtable))

    return chain
def SliceBlockDurationChain(runid, logname, authinfo, dbname, durtable, console):
    """
    return the Chain of conditions and tasks required to calculation the
    duration of the a traced block in the Slice harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param dbname      the database to insert this duration information into
    @param console     boolean indicating to send this output to the console
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
    comp7 = LogicalCompare("$msg:workerid",Relation.EQUALS,"$msg[0]:workerid")
    
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
    recmatch.add(comp6)
    recmatch.add(comp7)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$duration", "$msg[1]:PUBTIME-$msg[0]:PUBTIME"))
    chain.addLink(SetTask("$userduration", "$msg[1]:usertime-$msg[0]:usertime"))
    chain.addLink(SetTask("$userduration", "$userduration*1000.0"))
    chain.addLink(SetTask("$systemduration", "$msg[1]:systemtime-$msg[0]:systemtime"))
    chain.addLink(SetTask("$systemduration", "$systemduration*1000.0"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}",
                     "workerid":  "{$msg:workerid}",
                     "userduration": "{$userduration}",
                     "systemduration": "{$systemduration}",
                     "stagename": "{$msg:stagename}",
                     "comment":  "{$msg:COMMENT}"  }

    if console == True:
        eventTask2 = consoleTask()
        chain.addLink(eventTask2)
    else:
        chain.addLink(DBWriteTask(insertValues, authinfo, dbname, durtable))

    return chain

def PipelineBlockDurationChain(runid, logname, authinfo, dbname, durtable, console):

    """
    calculate the durations for a particular block executed within Pipeline
    (master) process.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param dbname      the database to insert this duration information into
    @param console     boolean indicating to send this output to the console
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
    comp2 = LogicalCompare("$msg:sliceId", Relation.EQUALS, -1)
    comp3 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
    comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
    comp5 = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    comp6 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")
    comp7 = LogicalCompare("$msg:workerid",Relation.EQUALS,"$msg[0]:workerid")
    
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
    recmatch.add(comp6)
    recmatch.add(comp7)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$duration", "$msg[1]:PUBTIME-$msg[0]:PUBTIME"))
    chain.addLink(SetTask("$userduration", "$msg[1]:usertime-$msg[0]:usertime"))
    chain.addLink(SetTask("$userduration", "$userduration*1000.0"))
    chain.addLink(SetTask("$systemduration", "$msg[1]:systemtime-$msg[0]:systemtime"))
    chain.addLink(SetTask("$systemduration", "$systemduration*1000.0"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}",
                     "workerid":  "{$msg:workerid}",
                     "userduration": "{$userduration}",
                     "systemduration": "{$systemduration}",
                     "stagename": "{$msg:stagename}",
                     "comment":  "{$msg:COMMENT}"  }

    if console == True:
        eventTask2 = consoleTask()
        chain.addLink(eventTask2)
    else:
        chain.addLink(DBWriteTask(insertValues, authinfo, dbname, durtable))

    return chain

def AppBlockDurationChain(runid, stageid, logname, startComm, endComm, 
                          authinfo, dbname, durtable, console, blockName=None):
    """
    calculate the duration of application level block.  This requires knowing
    the comments for the starting message and the ending message.  
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param startComm   the log message comment to search for as the mark
                          of the start of the block
    @param endComm     the log message comment to search for as the mark
                          of the end of the block
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param dbname      the database to insert this duration information into
    @param console     boolean indicating to send this output to the console
    @param blockName   a name to give to the block; if None, one is formed
                          from the starting comment
    @return Chain   a Chain to be added to a Job
    """
    if blockName is None:
        blockName = re.sub(r'\s*[sS]tart(ed|ing)?\s*', '', startComm)
        
    chain = Chain()

    # find the start of the process to get some metadata
    comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS,
                           'harness.slice.visit.stage.process')
    comp2 = LogicalCompare("$msg:stageId", Relation.EQUALS, stageid)
    chain.addLink(LogicalAnd(comp1, comp2))
    chain.addLink(SetTask("$stagestart", "$msg:PUBTIME"))
    
    # find the start of the trace block
    comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS, logname)
    comp2 = LogicalCompare("$msg:COMMENT", Relation.EQUALS, startComm)
    comp3 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
    comp4 = LogicalCompare("$msg:pipeline", Relation.EQUALS,
                           "$msg[0]:pipeline")
    comp5 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "$msg[0]:sliceId")
#    comp6 = LogicalCompare("$msg:PUBTIME", Relation.LESSTHAN,
#                           "$stagestart+1000000000")
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
#    recmatch.add(comp6)
    chain.addLink(Condition(recmatch))

    chain.addLink(SetTask("$startdate", "$msg:DATE"))

    # find the end of the trace block
    comp1 = LogicalCompare("$msg:LOG", Relation.EQUALS, logname)
    comp2 = LogicalCompare("$msg:COMMENT", Relation.EQUALS, endComm);       
    comp3 = LogicalCompare("$msg:sliceId", Relation.EQUALS, "$msg[0]:sliceId")
    comp4 = LogicalCompare("$msg:runId", Relation.EQUALS, "$msg[0]:runId")
    comp5 = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    
    recmatch = LogicalAnd(comp1, comp2)
    recmatch.add(comp3)
    recmatch.add(comp4)
    recmatch.add(comp5)
    chain.addLink(Condition(recmatch))

    chain.addLink(SetTask("$duration", "$msg[1]:PUBTIME-$msg[0]:PUBTIME"))
    chain.addLink(SetTask("$userduration", "$msg[1]:usertime-$msg[0]:usertime"))
    chain.addLink(SetTask("$userduration", "$userduration*1000.0"))
    chain.addLink(SetTask("$systemduration", "$msg[1]:systemtime-$msg[0]:systemtime"))
    chain.addLink(SetTask("$systemduration", "$systemduration*1000.0"))
    
    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     blockName,
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$msg[0]:loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg[0]:stageId}",
                     "workerid":  "{$msg:workerid}",
                     "userduration": "{$userduration}",
                     "systemduration": "{$systemduration}",
                     "stagename": "{$msg:stagename}",
                     "comment":  "{$msg:COMMENT}"  }

    if console == True:
        eventTask2 = consoleTask()
        chain.addLink(eventTask2)
    else:
        chain.addLink(DBWriteTask(insertValues, authinfo, dbname, durtable))
    return chain


def LoopDurationChain(runid, authinfo, dbname, durtable, console):
    """
    calculate the time required to complete each visit loop within the 
    master Pipeline process.
    harness code.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param dbname      the database to insert this duration information into
    @param console     boolean indicating to send this output to the console
    @return Job   a Job to be added to a Monitor
    """
    chain = Chain()

    # First log record: start of the visit
    cond1 = LogicalCompare("$msg:LOG",
                           Relation.EQUALS, "harness.pipeline.visit")
    cond2 = LogicalCompare("$msg:STATUS", Relation.EQUALS, "start")
    recmatch = LogicalAnd(cond1, cond2)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$loopnum", "$msg:loopnum"))
    #chain.addLink(SetTask("$nextloop", "$msg:loopnum + 1"))

    # Next log records:  the next loop (same log message)
    cond2a = LogicalCompare("$msg:STATUS", Relation.EQUALS, "end")
    cond2b = LogicalCompare("$msg:pipeline",Relation.EQUALS,"$msg[0]:pipeline")
    cond3 = LogicalCompare("$msg:workerid",Relation.EQUALS,"$msg[0]:workerid")
    recmatch = LogicalAnd(cond1, cond2a)
    recmatch.add(cond2b)
    recmatch.add(cond3)
    chain.addLink(Condition(recmatch));

    chain.addLink(SetTask("$startdate", "$msg[0]:DATE"))
    chain.addLink(SetTask("$duration", "$msg[1]:PUBTIME-$msg[0]:PUBTIME"))
    chain.addLink(SetTask("$userduration", "$msg[1]:usertime-$msg[0]:usertime"))
    chain.addLink(SetTask("$userduration", "$userduration*1000.0"))
    chain.addLink(SetTask("$systemduration", "$msg[1]:systemtime-$msg[0]:systemtime"))
    chain.addLink(SetTask("$systemduration", "$systemduration*1000.0"))

    # write to the durations table
    insertValues = { "runid":    "{$msg:runId}",
                     "name":     "{$msg:LOG}",
                     "sliceid":  "{$msg:sliceId}",
                     "duration": "{$duration}", 
                     "hostid":   "{$msg:hostId}", 
                     "loopnum":  "{$loopnum}", 
                     "pipeline": "{$msg:pipeline}", 
                     "date":     "{$startdate}",
                     "stageid":  "{$msg:stageId}",
                     "workerid":  "{$msg:workerid}",
                     "userduration": "{$userduration}",
                     "systemduration": "{$systemduration}",
                     "stagename": "{$msg:stagename}",
                     "comment":  "{$msg:COMMENT}"   }

    if console == True:
        eventTask2 = consoleTask()
        chain.addLink(eventTask2)
    else:
        chain.addLink(DBWriteTask(insertValues, authinfo, dbname, durtable))

    return chain


def consoleTask():

    template = Template()
    template.put("runid", Template.STRING, "$msg[0]:runId")
    template.put("workerid", Template.STRING, "$msg[0]:workerid")
    template.put("log", Template.STRING, "$msg[0]:LOG")
    template.put("sliceid", Template.INT, "$msg[0]:sliceId")
    template.put("hostid", Template.STRING, "$msg[0]:hostId")
    template.put("loopnum", Template.INT, "$msg[0]:loopnum")
    template.put("pipeline", Template.STRING, "$msg[0]:pipeline")
    template.put("startdate", Template.STRING, "$startdate")
    template.put("stageId", Template.STRING, "$msg[0]:stageId")
    template.put("stagename", Template.STRING, "$msg[0]:stagename")
    template.put("firsttime", Template.INT, "$msg[0]:PUBTIME")
    template.put("secondtime", Template.INT, "$msg[1]:PUBTIME")
    template.put("duration", Template.INT, "$duration")
    template.put("comment", Template.STRING, "$msg[1]:COMMENT")
    template.put("workerid", Template.STRING,  "$msg[1]:workerid")
    template.put("userduration", Template.FLOAT, "$userduration")
    template.put("systemduration", Template.FLOAT, "$systemduration")

    outputWriter = ConsoleWriter()
    eventTask = EventTask(outputWriter, template)

    return eventTask
