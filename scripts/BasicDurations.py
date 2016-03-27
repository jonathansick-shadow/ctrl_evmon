import sys
import os
import re
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
    parser.addArg("--butlerPut", "flag", False)
    parser.addArg("--butlerRead", "flag", False)
    parser.addArg("--configureSlice", "flag", False)
    parser.addArg("--initializeQueues", "flag", False)
    parser.addArg("--initializeStages", "flag", False)
    parser.addArg("--butlerWrite", "flag", False)
    parser.addArg("--sliceVisit", "flag", False)
    parser.addArg("--sliceVisitStage", "flag", False)

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
    butlerPut = parser.getFlag("--butlerPut")
    if butlerPut == True:
        bits |= 32
    butlerRead = parser.getFlag("--butlerRead")
    if butlerRead == True:
        bits |= 64
    butlerWrite = parser.getFlag("--butlerWrite")
    if butlerWrite == True:
        bits |= 128
    configureSlice = parser.getFlag("--configureSlice")
    if configureSlice == True:
        bits |= 256
    initializeQueues = parser.getFlag("--initializeQueues")
    if initializeQueues == True:
        bits |= 512
    initializeStages = parser.getFlag("--initializeStages")
    if initializeStages == True:
        bits |= 1024
    sliceVisit = parser.getFlag("--sliceVisit")
    if sliceVisit == True:
        bits |= 2048
    sliceVisitStage = parser.getFlag("--sliceVisitStage")
    if sliceVisitStage == True:
        bits |= 4096

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

    if (count == 0) or (count > 1):
        print "console argument specified; must specify ONE of --loop, --process, --preprocess, --postprocess, --stage, --butlerPut, --butlerRead, --butlerWrite, --configureSlice, --initializeQueues, --initializeStages, --sliceVisit, --sliceVisitStage"
        sys.exit(10)

    job = None
    if loop == True:
        job = fromdb.LoopDuration(runid, authinfo, dbname, logtable, durtable, console)
    elif process == True:
        job = fromdb.ProcessDuration(runid, authinfo, dbname, logtable, durtable, console)
    elif preprocess == True:
        job = fromdb.PreprocessDuration(runid, authinfo, dbname, logtable, durtable, console)
    elif postprocess == True:
        job = fromdb.PostprocessDuration(runid, authinfo, dbname, logtable, durtable, console)
    elif stage == True:
        job = fromdb.StageDuration(runid, authinfo, dbname, logtable, durtable, console)
    elif butlerPut == True:
        job = fromdb.GenericBlockDuration(runid, "daf.persistence.butler.put",
                                          authinfo, dbname, logtable, durtable, console)
    elif butlerRead == True:
        job = fromdb.GenericBlockDuration(runid, "daf.persistence.butler.read",
                                          authinfo, dbname, logtable, durtable, console)
    elif configureSlice == True:
        job = fromdb.SliceBlockDuration(runid, "harness.slice.configureSlice",
                                        authinfo, dbname, logtable, durtable, console)
    elif initializeQueues == True:
        job = fromdb.SliceBlockDuration(runid, "harness.slice.initializeQueues",
                                        authinfo, dbname, logtable, durtable, console)
    elif initializeStages == True:
        job = fromdb.SliceBlockDuration(runid, "harness.slice.initializeStages",
                                        authinfo, dbname, logtable, durtable, console)
    elif butlerWrite == True:
        job = fromdb.SliceBlockDuration(
            runid, "harness.slice.iostage.output.write_using_butler", authinfo, dbname, logtable, durtable, console)
    elif sliceVisit == True:
        job = fromdb.SliceBlockDuration(runid, "harness.slice.visit", authinfo,
                                        dbname, logtable, durtable, console)
    elif sliceVisitStage == True:
        job = fromdb.SliceBlockDuration(runid, "harness.slice.visit.stage",
                                        authinfo, dbname, logtable, durtable, console)

    if job == None:
        print "console argument specified; must specify ONE of --loop, --process, --preprocess, --postprocess, --stage, --butlerPut, --butlerRead, --butlerWrite, --configureSlice, --initializeQueues, --initializeStages, --sliceVisit, --sliceVisitStage"
        sys.exit(10)

    monitor = EventMonitor(job)
    monitor.runJobs()


if __name__ == "__main__":
    main()
