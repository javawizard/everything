
<%@page import="java.io.File"%>
<%@page import="jw.bznetwork.server.BZNetworkServer"%>
<%@page import="jw.bznetwork.server.InstallResponse"%>
<%@page import="java.util.Arrays"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Collections"%><html>
<body>
<%
    File configFolder = new File(application
            .getRealPath("/WEB-INF/config"));
    if (BZNetworkServer.isInstalled())
    {
%>
BZNetwork is already installed on this server, and doesn't need to be
installed again. <a href="index.jsp">Log in</a> to BZNetwork.
<%
    }
    else
    {
%>
<!-- BZNetwork has not yet been installed, so we're in business.  -->
<%
    InstallResponse ires = null;
        try
        {
            ires = BZNetworkServer.doInstall(request);
        }
        catch (Exception e)
        {
            e.printStackTrace();
%>An internal error occured:
<br />
<pre>
<%
    StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
%><%=sw.toString()%>
</pre>
<%
    ires = new InstallResponse(null, "", false);
        }
%>
<%=ires.getMessage()%><br />
<%
    if (ires.isShowContinueButton())
        {
%>
<form method="post" action="doinstall.jsp">
<%
    for (Object keyObject : Collections.list(request
                    .getParameterNames()))
            {
                String key = keyObject.toString();
                String value = request.getParameter(key);
%><input type="hidden" name="<%=StringEscapeUtils.escapeXml(key)%>"
	value="<%=StringEscapeUtils.escapeXml(value)%>" /> <%
     }
 %><input type="hidden" name="<%=ires.getContinueAddParameter()%>"
	value="true" /><input type="submit" value="Continue"
	style="font-weight: bold" /></form>
<%
    }
%>
<%
    }
%>

</body>
</html>