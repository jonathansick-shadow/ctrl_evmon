def readAuthInfo(host):
    """
    read the authentication data for connecting to a database server.
    @param host   the desired host where the server is running
    @return dict  the "authInfo" data for the requested host returned as
                     a dictionary
    """
    node = [""]
    authinfo = {}
    
    if not os.environ.has_key("HOME"):
        raise RuntimeError("No HOME in environment");
    fd = open(os.path.join(os.environ["HOME"], ".lsst", "db-auth.paf"), 'r')
    try: 
      for line in fd:
        line = commre.sub('', line.strip())
        match = nodere.search(line)
        if line.strip() == "}":
            if node[-1] == "authInfo" and authinfo["host"].startswith(host):
                return authinfo
            node.pop(-1)
        elif match:
            node.append(match.group(1))
            if node[-1] == "authInfo":
                authinfo = {}
        elif node[-1] == "authInfo":
            match = parmre.search(line)
            if match:
                authinfo[match.group(1)] = match.group(2)

    finally:
        fd.close()

    return {}

