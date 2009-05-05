import sys, os, re

from lsst.ctrl.evmon import Job, NormalizeMessageFilter

from lsst.ctrl.evmon.input import LsstEventReader, MysqlReader
from recipes import *

loggerselect = "SELECT DATE, TIMESTAMP, id, sliceId, runId, level, LOG, COMMENT, custom, hostId, STATUS, pipeline from logger";

def DBReader(query, authinfo):
    """
    return a reader that will pull records from the Logging database ("logs").
    @param query     the query to use to select records
    @param authinfo  the database authorization data returned from
                        db.readAuthInfo()
    """
    out = MysqlReader(authinfo["host"], "logs",
                      authinfo['user'], authinfo['password'])
    out.setFilter(NormalizeMessageFilter("custom", "=", ";"))
    out.setSelectString(query)
    return out


def SliceBlockDuration(runid, logname, authinfo, destination="durations"):
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

    mysqlReader = DBReader(select, authinfo)
    chain = SliceBlockDurationChain(runid, logname, authinfo, destination)
    return Job(mysqlReader, chain)    
    
def PipelineBlockDuration(runid, logname, authinfo, destination="durations"):

    select = "%s where runid='%s' and sliceid=-1 and log='%s' order by TIMESTAMP;" % (loggerselect, runid, logname)
    mysqlReader = DBReader(select, authinfo)
    chain = PipelineBlockDurationChain(runid, logname, authinfo, destination)
    return Job(mysqlReader, chain)

def ProcessDuration(runid, authinfo, destination="durations"):
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
                              authinfo, destination)

def EventWaitDuration(runid, authinfo, destination="durations"):
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
                              authinfo, destination)

def StageDuration(runid, authinfo, destination="durations"):
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
                                 authinfo, destination)

def LoopDuration(runid, authinfo, destination="durations"):
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
    mysqlReader = DBReader(select, authinfo)
    chain = LoopDurationChain(runid, authinfo, destination)
    return Job(mysqlReader, chain)


