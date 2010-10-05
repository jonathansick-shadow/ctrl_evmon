import sys, os, re

from lsst.ctrl.evmon import EventMonitor
from lsst.ctrl.evmon.durations import fromdb
from lsst.ctrl.evmon.auth import DbAuth

def main():
    host = "lsst10.ncsa.uiuc.edu"

    runid = sys.argv[1]
    dbname = sys.argv[2]

    dbAuth = DbAuth.DbAuth()
    authinfo = dbAuth.readAuthInfo(host)

    monitor = EventMonitor(fromdb.LoopDuration(runid, authinfo, dbname))
    #monitor.addJob(fromdb.ProcessDuration(runid, authinfo, dbname, dest))
    #monitor.addJob(fromdb.PreprocessDuration(runid, authinfo, dbname, dest))
    #monitor.addJob(fromdb.PostprocessDuration(runid, authinfo, dbname, dest))
    #monitor.addJob(fromdb.EventWaitDuration(runid, authinfo, dbname, dest))
    #monitor.addJob(fromdb.SliceEventWaitDuration(runid, authinfo, dbname, dest))
    #monitor.addJob(fromdb.StageDuration(runid, authinfo, dbname, dest))

    monitor.runJobs()

if __name__ == "__main__":
    main()
    
