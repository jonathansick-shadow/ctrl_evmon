import os
import os.path
from Tokenizer import Tokenizer
class DbAuth:
    def __init__(self):
        fileName = os.path.join(os.environ["HOME"],".lsst/db-auth.paf")
        self.t = Tokenizer(fileName)
        self.node = self._create()

    def _create(self):
        list = []
        while True:
            node = self._createNode()
            if node == None:
                return list
            list.append(node)
            
    def getAuthInfoEntry(self, host, user):
        list = self.getAuthInfoList()
        cnt = len(list)
        
        for i in range(0,cnt):
            authInfo = self.getAuthInfo(i)
            if (authInfo['host'] == host) and (authInfo['user'] == user):
                return authInfo
        return None
    
    def getDatabase(self):
        return self.node[0][1]
    
    def getAuthInfoList(self):
        db = self.get('database')
        arr = []
        for auths in db:
            arr.append(auths[1])
        return arr
    
    def getAuthInfo(self, index):
        arr = self.getAuthInfoList()
        dict = {}
        for data in arr[index]:
            dict[data[0]] = data[1]
        return dict
            
    def get(self, name):
        return self._get(self.node, name)

    def _get(self, list, name):
        key = name.split('.')
        node = list[0]
        #print "key is = ",key[0]," node is = ",node," node[0] is = ",node[0]
        if node[0] == key[0]:
            n = name.partition('.')
            if n[1] == '':
                return node[1]
            return self._get(node[1],n[2])
        else:
            return None
        
    def _createNode(self):
        t = self.t
        key = t.getToken()
        if key == None:
            return None
        if key.getValue() == '}':
            return None
        if key.getValue() == ',':
            return self._createNode()

        sep = t.getToken()
        if sep == None:
            return None

        data = t.getToken().getValue()

        if data == '{':
            node = []
            while True:
                n = self._createNode()
                if n == None:
                    return [key.getValue(), node]
                node.append(n)
        else:
            node = [key.getValue(),data]
            return node

if __name__ == "__main__":

    p = DbAuth()

    db = p.getDatabase()
    authInfo = p.getAuthInfoEntry("lsst10.ncsa.uiuc.edu","srp")
    print authInfo