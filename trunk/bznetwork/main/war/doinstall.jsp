
<%@page import="java.io.File"%>
<%@page import="jw.bznetwork.server.BZNetworkServer"%><html>
<body>
<%
    File configFolder = new File(application
            .getRealPath("/WEB-INF/config"));
    if (BZNetworkServer.isInstalled())
    {
%>
BZNetwork is already installed on this server, and doesn't need to be
installed again.
<%
    }
    else
    {
%>
<!-- BZNetwork has not yet been installed, so we're in business. The first thing to
do is check to see if BZNetwork has already been installed in the specified database.
If it is, we'll issue a warning to the user, telling them that the tables will not be
re-created and that they should make sure the store folder is the same, unless there is
a query parameter called supress-existence-warning in the request (which is what is sent
when they choose ok when presented with the warning). -->
<%
    String dbDriver = request.getParameter("db-driver");
        String dbUrl = request.getParameter("db-url");
        String dbUsername = request.getParameter("db-username");
        String dbPassword = request.getParameter("db-password");
        String storeFolder = request.getParameter("store-folder");
        String cacheFolder = request.getParameter("cache-folder");
%>
<%
    }
%>
</body>
</html>