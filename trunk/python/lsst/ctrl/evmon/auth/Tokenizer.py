import os
import string
import Token

class Tokenizer:
    SEPARATOR = 1
    WORD = 2
    DATA = 3
    
    def __init__(self, fileName):    
        fd = open(fileName)
        self.lines = fd.readlines()
        self.text = ""
        for line in self.lines:
            self.text = self.text+line
        self.totalLines = len(self.lines)
        self.curPos = 0
        self.curLine = 0
        self.type = None
        self.state = self.WORD
        self.eof = False
    
    def getCurrentLinePosition(self):
            return self.curLine

    def skipComments(self):
        x = self.curPos
        while self.text[x] == '#':
            while self.text[x] != '\n':
                x = x + 1
            x = x + 1 # skip \n too
            self.curLine = self.curLine + 1
        self.curPos = x

    def getWord(self):
        x = self.curPos
        self.value = None
        while self.text[x] == ' ':
            x = x + 1
        str = ""
        while self.text[x] in (string.letters + string.digits):
            str = str + self.text[x]
            x = x + 1
        self.curPos = x
        if str == "":
            print "parse error: expected word"
        self.type = self.WORD
        self.value = str
        self.state = self.SEPARATOR
        return Token.Token(self.WORD, str)

    def eatBlanks(self):
        x = self.curPos
        if self.curLine >= len(self.lines):
            self.eof = True
            return
        if (self.curLine < len(self.lines)):
            while self.text[x] == ' ' or self.text[x] == '\n':
                if self.text[x] == '\n':
                    self.curLine = self.curLine + 1
                    if self.curLine >= self.totalLines:
                        break
                x = x + 1
        self.curPos = x 

    def getSeparator(self):
        x = self.curPos

        self.value = None
        while self.text[x] == ' ':
            x = x + 1
        if self.text[x] in [':', '{', '}', ',']:
            self.value = self.text[x]
            x = x + 1
        else:
            print "parse error: unknown token '"+self.text[x]+"'"
        self.curPos = x
        self.eatBlanks()
        if self.value == ':' and self.text[x] == '{':
            self.state = self.SEPARATOR
        elif self.value == '}':
            self.state = self.SEPARATOR
        elif self.value == ',':
            self.state = self.WORD
        else:
            self.state = self.DATA
        self.type = self.SEPARATOR
        return Token.Token(self.type, self.value)
        
    def getRestOfLine(self):
        x = self.curPos

        self.value = None
        str = ""
        while self.text[x] != '\n':
            str = str + self.text[x]
            x = x + 1
        x = x + 1
        self.curLine = self.curLine + 1

        self.type = self.DATA
        self.value = str
        self.curPos = x
        self.state = self.WORD
        self.eatBlanks()
        x = self.curPos
        if self.eof == False:
            ch = self.text[x]
            # print ">"+ch+"<, str = "+str
            if self.text[x] == '}':
                self.state = self.SEPARATOR
        return Token.Token(self.type, str)
        
    
    def getToken(self):
        if (self.curLine >= len(self.lines)):
            self.eof = True
            return None
        if self.state == self.WORD:
            self.skipComments()
            return self.getWord()
        if self.state == self.SEPARATOR:
            return self.getSeparator()
        if self.state == self.DATA:
            return self.getRestOfLine()



if __name__ == "__main__":
    dict = {1 : 'SEPARATOR', 2: 'WORD', 3: 'DATA'}
    total = ""
    t = Tokenizer("/Users/srp/.lsst/foo.paf")

    while True:
        token = t.getToken()
        if (token == None):
            break
        print dict[token.getType()]," = "+token.getValue()
