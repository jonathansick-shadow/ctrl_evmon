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
    parser.addArg("--logtable", "store", None)
    parser.addArg("--durtable", "store", None)
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
    logtable = parser.getArg("--logtable")
    durtable = parser.getArg("--durtable")

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

    if logtable == None:
        logtable = "Logs"
    if durtable == None:
        durtable = "Durations"

        
    dbAuth = DbAuth.DbAuth()
    authinfo = dbAuth.readAuthInfo(host)

    count = 0
    while bits:
        count += (bits & 1)
        bits >>= 1

    if console == False: # throw everything into the durations table
        if count > 0:
            print "options --loop, --process, --preprocess, --postprocess, --stage only specifiable with --console option"
            sys.exit(10)

        monitor = EventMonitor(fromdb.LoopDuration(runid, authinfo, dbname, logtable, durtable, console))
        monitor.addJob(fromdb.ProcessDuration(runid, authinfo, dbname, logtable, durtable, console))
        monitor.addJob(fromdb.PreprocessDuration(runid, authinfo, dbname, logtable, durtable, console))
        monitor.addJob(fromdb.PostprocessDuration(runid, authinfo, dbname, logtable, durtable, console))

        # does this exist still?
        #monitor.addJob(fromdb.EventWaitDuration(runid, authinfo, dbname, console))

        # does this exist still?
        #monitor.addJob(fromdb.SliceEventWaitDuration(runid, authinfo, dbname, console))

        monitor.addJob(fromdb.StageDuration(runid, authinfo, dbname, logtable, durtable, console))
        monitor.runJobs()
    else: 
        if (count == 0) or (count > 1):
            print "console argument specified; must specify ONE of --loop, --process, --preprocess, --postprocess, --stage"
            sys.exit(10)

        job = None
        if loop == True:
            job = fromdb.LoopDuration(runid, authinfo, dbname, logtable, durtable, console)
        elif process == True:
            job = fromdb.ProcessDuration(runid, authinfo, dbname, logtable, durtable, console)
        elif preprocess == True:
            job = fromdb.PreprocessDuration(runid, authinfo, dbname, logtable, durtable, console)
        elif postprocess == True:
            job = fromdb.PreprocessDuration(runid, authinfo, dbname, logtable, durtable, console)
        elif stage == True:
            job = fromdb.StageDuration(runid, authinfo, dbname, logtable, durtable, console)

        if job == None:
            print "console argument specified; must specify ONE of --loop, --process, --preprocess, --postprocess, --stage"
            sys.exit(10)

        monitor = EventMonitor(job)
        monitor.runJobs()


if __name__ == "__main__":
    main()
