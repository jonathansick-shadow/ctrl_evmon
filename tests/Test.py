from lsst.ctrl.evmon import *
from lsst.ctrl.evmon.engine import MessageEvent


class MyLink(Link):

    def evaluate(self, msg):
        if msg != 0:
            return 1
        return 0

x = Task()

chain = Chain()
link = MyLink()

chain.addLink(link)
x.addChain(chain)

print 'done'
