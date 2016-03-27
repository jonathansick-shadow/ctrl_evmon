import os
import os.path
from Parser import Parser


class DbAuth:

    def __init__(self):
        self.filename = os.path.join(os.environ["HOME"], ".lsst/db-auth.paf")

    def readAuthInfo(self, host, port='3306'):
        p = Parser(self.filename)
        list = p.resolve()

        db = list[0]
        auths = db[1]
        for auth in auths:
            bHost = False
            bPort = False
            for data in auth[1]:
                if data[0] == 'host' and data[1] == host:
                    bHost = True
                if data[0] == 'port' and data[1] == port:
                    bPort = True
            if bHost and bPort:
                result = {}
                for data in auth[1]:
                    result[data[0]] = data[1]
                return result
        return None


if __name__ == "__main__":

    p = DbAuth()

    authInfo = p.readAuthInfo("lsst10.ncsa.uiuc.edu")
