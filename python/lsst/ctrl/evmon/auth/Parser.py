import os
import string
import Token
import Tokenizer

class Parser:

    def __init__(self, filename):
        self.t = Tokenizer.Tokenizer(filename)
        self.dict = {}
            

    def getWord(self):
        token = self.t.getToken()
        if token == None:
            print "expected keyword, got None"
            return None
        value = token.getValue()
        type = token.getType()
        if type != 'W':
            print "expected keyword, got ",value
            return None
        return value
        
    def getColon(self):
        token = self.t.getToken()
        if token == None:
            print "expected ':' got None"
            return None
        value = token.getValue()
        type = token.getType()
        if type != ':':
            print "expected ':' got ",value
            return None
        return value

    def getToken(self):
        token = self.t.getToken()
        return token
        

    def resolve(self):
        result = []
        while True:
            word = self.getToken()
            if word == None:
                return result
            if word.getValue() == '}':
                return result
            #print "word = ",word
            colon = self.getColon()
            if colon == None:
               return result
            token = self.getToken()
            if token.getValue() == '{':
                d1 = self.resolve()
                
                result.append([word.getValue() ,  d1])
            else:
                result.append([word.getValue() , token.getValue()])
        


if __name__ == "__main__":
    p = Parser("~/.lsst/db-auth.paf")
    d = p.resolve()
