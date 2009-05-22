import os

from lsst.ctrl.evmon.auth import DbAuth

import lsst.ctrl.evmon.Chain as Chain
import lsst.ctrl.evmon.Condition as Condition
import lsst.ctrl.evmon.EventTask as EventTask
import lsst.ctrl.evmon.Job as Job
import lsst.ctrl.evmon.LogicalAnd as LogicalAnd
import lsst.ctrl.evmon.LogicalCompare as LogicalCompare
import lsst.ctrl.evmon.NormalizeMessageFilter as NormalizeMessageFilter
import lsst.ctrl.evmon.Relation as Relation
import lsst.ctrl.evmon.SetTask as SetTask
import lsst.ctrl.evmon.MysqlTask as MysqlTask
import lsst.ctrl.evmon.Template as Template
import lsst.ctrl.evmon.input.LsstEventReader as LsstEventReader
import lsst.ctrl.evmon.input.MysqlReader as MysqlReader
import lsst.ctrl.evmon.output.ConsoleWriter as ConsoleWriter
import lsst.ctrl.evmon.output.MysqlWriter as MysqlWriter
import lsst.ctrl.evmon.EventMonitor as EventMonitor


class StopEnd:

    def __init__(self, runId):
        self.runId = runId
        host = "fester.ncsa.uiuc.edu"

        dbAuth = DbAuth.DbAuth()

        self.auth = dbAuth.readAuthInfo(host)

        if self.auth == None:
            print "Couldn't find matching entry for host="+host+" in db-auth.paf file"
            sys.exit(10)


    def getProcessDurationChain(self):
        logName = "harness.slice.visit.stage.process"
        dbTableName = "durations"
        return self.getChain(logName, dbTableName)


    def getEventWaitDurationChain(self):
        logName = "harness.slice.visit.stage.handleEvents.eventwait"
        dbTableName = "durations"
        return self.getChain(logName, dbTableName)

    def getChain(self, logName, dbTableName):
        
        chain = Chain()
        
        cond3 = LogicalCompare("$msg:status", Relation.EQUALS, "start")
        firstCondition = Condition(cond3)
        chain.addLink(firstCondition)
        
        setTask1 = SetTask("$firstLoop", "$msg:loopnum")
        chain.addLink(setTask1)
        
        setTask3 = SetTask("$startdate", "$msg:date")
        chain.addLink(setTask3)
        
        comp1 = LogicalCompare("$msg:status", Relation.EQUALS, "end");       
        comp2 = LogicalCompare("$msg:sliceid", Relation.EQUALS, "$msg[0]:sliceid")
        comp3 = LogicalCompare("$msg:runid", Relation.EQUALS, "$msg[0]:runid")
        comp4 = LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum")
        comp5 = LogicalCompare("$msg:hostid", Relation.EQUALS, "$msg[0]:hostid")
        comp6 = LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId")
        
        logicalAnd1 = LogicalAnd(comp1, comp2)
        logicalAnd1.add(comp3)
        logicalAnd1.add(comp4)
        logicalAnd1.add(comp5)
        logicalAnd1.add(comp6)
        
        cond2 = Condition(logicalAnd1)
        chain.addLink(cond2)
        
        setTask4 = SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP")
        chain.addLink(setTask4)
        
        setTask5 = SetTask("$id", "$msg:id")
        chain.addLink(setTask5)
        
        # write to database
        insertQuery = "INSERT INTO "+dbTableName+"(runid, name, sliceid, duration, host, loopnum, pipeline, date, stageid) values({$msg:runid}, {$msg:log}, {$msg:sliceid}, {$duration}, {$msg:hostid}, {$firstLoop}, {$msg:pipeline}, {$startdate}, {$msg:stageId});"
        
        mysqlWriter = MysqlWriter(self.auth['host'], "logs", self.auth['user'], self.auth['password']);        
        mysqlTask = MysqlTask(mysqlWriter, insertQuery)
        chain.addLink(mysqlTask)

        return chain
