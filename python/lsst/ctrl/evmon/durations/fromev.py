import sys, os, re

from lsst.ctrl.evmon import Job, NormalizeMessageFilter

from lsst.ctrl.evmon.input import LsstEventReader, MysqlReader
from recipes import *

def LogEventStreamReader(broker, channel="LSSTLogging"):
    """
    return a reader that will read the Log Event channel for its inputs
    """
    return LsstEventReader(channel, broker)

def SliceBlockDuration(runid, logname, authinfo, broker,
                       channel="LSSTLogging", destination="durations"):
    """
    calculate the durations for a particular block executed within Slice
    harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = SliceBlockDurationChain(runid, logname, authinfo, destination)
    return Job(LogEventStreamReader(broker, channel), chain)    
    
def PipelineBlockDuration(runid, logname, authinfo, broker,
                          channel="LSSTLogging", destination="durations"):
    """
    calculate the durations for a particular block executed within Pipeline
    harness code.
    @param runid       the run identifier for the run to process
    @param logname     the name of log that contains the start and stop
                          messages
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = PipelineBlockDurationChain(runid, logname, authinfo, destination)
    return Job(LogEventStreamReader(broker, channel), chain)

def ProcessDuration(runid, authinfo, broker, channel="LSSTLogging", 
                    destination="durations"):
    """
    calculate the time required to execute the process() function for each
    each stage within each worker Slice process.  
    The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return SliceBlockDuration(runid, 'harness.slice.visit.stage.process',
                              authinfo, broker, channel, destination)

def EventWaitDuration(runid, authinfo, broker, channel="LSSTLogging", 
                      destination="durations"):
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
                                 authinfo, broker, channel, destination)

def SliceEventWaitDuration(runid, authinfo, broker, channel="LSSTLogging", 
                           destination="durations"):
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
                              authinfo, broker, channel, destination)

def StageDuration(runid, authinfo, broker, channel="LSSTLogging", 
                  destination="durations"):
    """
    calculate the time required to complete each stage within the 
    master Pipeline process.  The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid, 'harness.pipeline.visit.stage',
                                 authinfo, broker, channel, destination)

def PreprocessDuration(runid, authinfo, broker, channel="LSSTLogging", 
                       destination="durations"):
    """
    calculate the time required to complete the preprocess function for
    each stage within the master Pipeline process.  The data is read in
    from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid,
                                 'harness.pipeline.visit.stage.preprocess',
                                 authinfo, broker, channel, destination)

def PostprocessDuration(runid, authinfo, broker, channel="LSSTLogging", 
                        destination="durations"):
    """
    calculate the time required to complete the preprocess function for
    each stage within the master Pipeline process.  The data is read in
    from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param broker      the host where the event broker is running
    @param channel     the logging event channel (def: "LSSTLogging")
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    return PipelineBlockDuration(runid,
                                 'harness.pipeline.visit.stage.postprocess',
                                 authinfo, broker, channel, destination)

def LoopDuration(runid, authinfo, broker, channel="LSSTLogging", 
                 destination="durations"):
    """
    calculate the time required to complete each visit loop within the 
    master Pipeline process.  The data is read in from the logs database.
    @param runid       the run identifier for the run to process
    @param authinfo    the database authorization data returned from
                          db.readAuthInfo()
    @param destination the name of the table to write to (def: "durations")
    @return Job   a Job to be added to a Monitor
    """
    chain = LoopDurationChain(runid, authinfo, destination)
    return Job(LogEventStreamReader(broker, channel), chain)




    
