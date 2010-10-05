import os
import string
import Token

class Tokenizer:

    COLON = ':'
    OPEN_BRACE = '{'
    CLOSE_BRACE = '}'
    WORD = 'W'

    def __init__(self, fileName):
        fd = open(fileName)
        self.lines = fd.readlines()
        self.text = ""
        for line in self.lines:
            self.text = self.text+line
        self.totalLines = len(self.lines)
        self.curPos = 0
        self.curLine = 0
        self.eof = False
        self.totalChars = len(self.text)


    def getToken(self):
        if self.eof:
            return None
        x = self.curPos

        while self.text[x] == '#':
            while self.text[x] != '\n':
                x = x + 1
            x = x + 1 # skip \n too
            if x >= self.totalChars:
                self.eof = True
                return None

        while self.text[x] == ' ' or self.text[x] == '\n':
            x = x + 1
            if x >= self.totalChars:
                self.eof = True
                return None

        str = ""
        if self.text[x] == self.COLON:
            x = x + 1
            self.curPos = x
            if x >= self.totalChars:
                self.eof = True
            return Token.Token(self.COLON, ":")
        elif self.text[x] == self.OPEN_BRACE:
            x = x + 1
            self.curPos = x
            if x >= self.totalChars:
                self.eof = True
            return Token.Token(self.OPEN_BRACE, "{")
        elif self.text[x] == self.CLOSE_BRACE:
            x = x + 1
            self.curPos = x
            if x >= self.totalChars:
                self.eof = True
            return Token.Token(self.CLOSE_BRACE, "}")
        while self.text[x] in (string.letters + string.digits + '.' ):
            str = str + self.text[x]
            x = x + 1
            self.curPos = x
        self.curPos = x
        if x >= self.totalChars:
            self.eof = True
            return None
        if str == "":
            return None
        return Token.Token(self.WORD, str)

if __name__ == "__main__":
    t = Tokenizer("foo.paf")

    while True:
        token = t.getToken()
        if token != None:
            print token.getValue()
        else:
            break
