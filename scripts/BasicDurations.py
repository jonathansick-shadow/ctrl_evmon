import sys, os, re
from lsst.ctrl.evmon import ArgParser

from lsst.ctrl.evmon import EventMonitor
from lsst.ctrl.evmon.durations import fromdb
from lsst.ctrl.evmon.auth import DbAuth

def main():
    host = "lsst10.ncsa.uiuc.edu"

    # this version of jython doesn't have optparse or argparse
    parser = ArgParser.ArgParser('basic duration calculations')
    parser.addArg("--runid", "store", None)
    parser.addArg("--dbname", "store", None)
    parser.addArg("--console", "flag", False)
    parser.addArg("--loop", "flag", False)
    parser.addArg("--process", "flag", False)
    parser.addArg("--preprocess", "flag", False)
    parser.addArg("--postprocess", "flag", False)
    parser.addArg("--stage", "flag", False)

    parser.parseArgs(sys.argv)

    runid = parser.getArg("--runid")
    dbname = parser.getArg("--dbname")
    console = parser.getFlag("--console")

    bits = 0
    loop = parser.getFlag("--loop")
    if loop == True:
        bits = 1
    process = parser.getFlag("--process")
    if process == True:
        bits |= 2
    preprocess = parser.getFlag("--preprocess")
    if preprocess == True:
        bits |= 4
    postprocess = parser.getFlag("--postprocess")
    if postprocess == True:
        bits |= 8
    stage = parser.getFlag("--stage")
    if stage == True:
        bits |= 16

        
    count = 0
    while bits:
        count += (bits & 1)
        bits >>= 1


    dbAuth = DbAuth.DbAuth()
    authinfo = dbAuth.readAuthInfo(host)

    if console == False: # throw everything into the durations table
        monitor = EventMonitor(fromdb.LoopDuration(runid, authinfo, dbname, console))
        monitor.addJob(fromdb.ProcessDuration(runid, authinfo, dbname, console))
        monitor.addJob(fromdb.PreprocessDuration(runid, authinfo, dbname, console))
        monitor.addJob(fromdb.PostprocessDuration(runid, authinfo, dbname, console))

        # does this exist still?
        #monitor.addJob(fromdb.EventWaitDuration(runid, authinfo, dbname, console))

        # does this exist still?
        #monitor.addJob(fromdb.SliceEventWaitDuration(runid, authinfo, dbname, console))

        monitor.addJob(fromdb.StageDuration(runid, authinfo, dbname, console))
        monitor.runJobs()
    else: 
        if (count == 0) or (count > 1):
            print "console argument specified; must specify ONE of --loop, --process, --preprocess, --postprocess, --stage"
            sys.exit(10)

        job = None
        if loop == True:
            job = fromdb.LoopDuration(runid, authinfo, dbname, console)
        elif process == True:
            job = fromdb.ProcessDuration(runid, authinfo, dbname, console)
        elif preprocess == True:
            job = fromdb.PreprocessDuration(runid, authinfo, dbname, console)
        elif postprocess == True:
            job = fromdb.PreprocessDuration(runid, authinfo, dbname, console)
        elif stage == True:
            job = fromdb.StageDuration(runid, authinfo, dbname, console)

        monitor = EventMonitor(job)
        monitor.runJobs()


if __name__ == "__main__":
    main()
