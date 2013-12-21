
<%
    response.setContentType("text/plain");
%>
<%-- The root element of the log viewer document should be a div tag. 
The gwt app requests the log viewer page in an XHR and then extracts 
the root XML node from it via responseXML and adds it to its own
custom widget. The reason for doing this is that since the logs table 
can be fairly large, constructing it on the server side seems to go 
much faster than constructing it on the client-side. --%>

<%@page import="jw.bznetwork.client.data.model.LogRequest"%>
<%@page import="java.util.Date"%>
<%@page import="net.sf.opengroove.common.utils.StringUtils"%>
<%@page import="jw.bznetwork.client.screens.LogsScreen"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="jw.bznetwork.client.Perms"%>
<%@page import="jw.bznetwork.server.rpc.GlobalLinkImpl"%>
<%@page import="jw.bznetwork.server.data.DataStore"%>
<%@page import="jw.bznetwork.client.data.model.LogEvent"%>
<%@page import="jw.bznetwork.server.BZNetworkServer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%><div style="width: 100%">
<%
    try
    {
        BZNetworkServer.doLogViewer(request, out);
    }
    catch (Exception e)
    {
%><%=e.getClass().getName()%>: <%=e.getMessage()%><br />
<pre>
<%
    StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
%><%=StringEscapeUtils.escapeHtml(sw.toString())%></pre> <%
     }
 %>
</div>











