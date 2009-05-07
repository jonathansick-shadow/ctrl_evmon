from Tokenizer import Tokenizer
class Policy:
    def __init__(self, fileName):
        self.t = Tokenizer(fileName)
        node = self.create()
        print node

    def create(self):
        list = []
        while True:
            node = self.createNode()
            if node == None:
                return list
            list.append(node)
        
    
    def createNode(self):
        t = self.t
        key = t.getToken()
        if key == None:
            return None
        if key.getValue() == '}':
            return None
        if key.getValue() == ',':
            return self.createNode()

        sep = t.getToken()
        if sep == None:
            return None

        data = t.getToken().getValue()

        if data == '{':
            node = []
            while True:
                n = self.createNode()
                if n == None:
                    return [key.getValue(), node]
                node.append(n)
        else:
            node = [key.getValue(),data]
            return node

if __name__ == "__main__":

    p = Policy("/Users/srp/.lsst/foo.paf")
