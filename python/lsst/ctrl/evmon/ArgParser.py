import sys

# this is a really simple command line argument parser, since version of jython
# we're using doesn't have access to optparse or argparse


class ArgParser:

    def __init__(self, info):
        self.info = info
        self.flags = {}
        self.options = {}

    def addArg(self, option, action, default):
        if action == "store":
            self.options[option] = default
        elif action == "flag":
            self.flags[option] = default
        else:
            print action, " is an unknown action"

    def getArg(self, name):
        if self.options.has_key(name):
            return self.options[name]
        return None

    def getFlag(self, name):
        if self.flags.has_key(name):
            return self.flags[name]
        return None

    def parseArgs(self, args):
        total = len(args)-1

        x = 1
        while x <= total:
            for flag in self.flags:
                if args[x] == flag:
                    self.flags[flag] = True
            x += 1

        x = 1
        while x <= total:
            for opt in self.options:
                if x > total:
                    break
                if opt == args[x]:
                    x += 1
                    if x <= total:
                        if args[x].startswith("--"):
                            print opt, ": missing argument"
                        else:
                            self.options[opt] = args[x]
                    else:
                        print opt, ": missing argument"
                        sys.exit(0)
                    break
            x += 1
