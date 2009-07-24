
<%@page import="java.io.File"%><html><body><%
    File configFolder = new File(application
            .getRealPath("/WEB-INF/config"));
    if (configFolder.exists())
    {
%>
BZNetwork is already installed on this server, and doesn't need to be installed again.
<%
    }
    else
    {
%>
<!-- BZNetwork has not yet been installed, so we're in business. -->
<%
    }
%>
</body></html>