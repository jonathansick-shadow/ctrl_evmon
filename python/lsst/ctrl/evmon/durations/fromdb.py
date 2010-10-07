import sys, os, re

from lsst.ctrl.evmon import Job, NormalizeMessageFilter

from lsst.ctrl.evmon.input import LsstEventReader, MysqlReader
from recipes import *

loggerselect = "SELECT workerid, DATE, TIMESTAMP, loopnum, id, stageId, sliceId, runId, level, LOG, COMMENT, custom, hostId, STATUS, pipeline from logs";

def DBReader(query, authinfo, dbname):
    """
    return a reader that will pull records from the Logging database ("logs").
    @param query     the query to use to select records
    @param dbname    database name
    @param authinfo  the database authorization data returned from
                        db.readAuthInfo()
    """
    out = MysqlReader(authinfo["host"], dbname,
                      authinfo['user'], authinfo['password'])
    out.setFilter(NormalizeMessageFilter("custom", ":", ";"))
    out.setSelectString(query)
    return out


def SliceBlockDuration(runid, logname, authinfo, dbname):
    """
    calculate the durations for a particular block executed within Slice
    harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    select = "%s where runId='%s' and log='%s' order by TIMESTAMP;" % \
             (loggerselect, runid, logname)

    mysqlReader = DBReader(select, authinfo, dbname)
    chain = SliceBlockDurationChain(runid, logname, authinfo, dbname)
    return Job(mysqlReader, chain)    
    
def PipelineBlockDuration(runid, logname, authinfo, dbname, destination="durations"):

    select = "%s where runid='%s' and sliceId=-1 and log='%s' order by TIMESTAMP;" % (loggerselect, runid, logname)
    mysqlReader = DBReader(select, authinfo, dbname)
    chain = PipelineBlockDurationChain(runid, logname, authinfo, destination)
    return Job(mysqlReader, chain)

def AppBlockDuration(runid, stageid, logname, startComm, endComm, authinfo, dbname,
                     blockName=None, destination="durations"):
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
    @param blockName   a name to give to the block; if None, one is formed
                          from the starting comment
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    select = "%s where runId='%s' and log='%s' order by TIMESTAMP;" % \
             (loggerselect, runid, logname)

    mysqlReader = DBReader(select, authinfo, dbname)
    chain = AppBlockDurationChain(runid, stageid, logname, startComm, endComm,
                                  authinfo, dbname, blockName, destination)
    return Job(mysqlReader, chain)    


def ProcessDuration(runid, authinfo, dbname):
    """
    calculate the time required to execute the process() function for each
    each stage within each worker Slice process.  
    The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return SliceBlockDuration(runid, 'harness.slice.visit.stage.process',
                              authinfo, dbname)

def EventWaitDuration(runid, authinfo, dbname):
    """
    calculate the time spent in a Slice waiting for an event to arrive.  
    The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid,
                         'harness.pipeline.visit.stage.handleEvents.eventwait',
                                 authinfo, dbname)

def SliceEventWaitDuration(runid, authinfo, dbname):
    """
    calculate the time spent in a Slice waiting for an event to arrive.  
    The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return SliceBlockDuration(runid,
                            'harness.slice.visit.stage.handleEvents.eventwait',
                              authinfo, dbname)

def StageDuration(runid, authinfo, dbname):
    """
    calculate the time required to complete each stage within the 
    master Pipeline process.  The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid, 'harness.pipeline.visit.stage',
                                 authinfo, dbname)

def PreprocessDuration(runid, authinfo, dbname):
    """
    calculate the time required to complete the preprocess function for
    each stage within the master Pipeline process.  The data is read in
    from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid,
                                 'harness.pipeline.visit.stage.preprocess',
                                 authinfo, dbname)

def PostprocessDuration(runid, authinfo, dbname, destination="durations"):
    """
    calculate the time required to complete the preprocess function for
    each stage within the master Pipeline process.  The data is read in
    from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid,
                                 'harness.pipeline.visit.stage.postprocess',
                                 authinfo, dbname)

def LoopDuration(runid, authinfo, dbname):
    """
    calculate the time required to complete each visit loop within the 
    master Pipeline process.  The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    select = "%s where runid='%s' and log='harness.pipeline.visit' order by TIMESTAMP;" % (loggerselect, runid)
    mysqlReader = DBReader(select, authinfo, dbname)
    chain = LoopDurationChain(runid, authinfo, dbname)
    return Job(mysqlReader, chain)


def LoopDuration1(runid, authinfo, dbname):
    """
    calculate the time required to complete each visit loop within the 
    master Pipeline process.  The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    select = "%s where runid='%s' and log='harness.pipeline.visit' order by TIMESTAMP;" % (loggerselect, runid)
    print select
    mysqlReader = DBReader(select, authinfo, dbname)
    chain = LoopDurationChain1(runid, authinfo, dbname)
    return Job(mysqlReader, chain)
