class Token:

    def __init__(self, tokenType, tokenValue):
        self.tokenType = tokenType
        self.tokenValue = tokenValue

    def getType(self):
        return self.tokenType

    def getValue(self):
        return self.tokenValue
