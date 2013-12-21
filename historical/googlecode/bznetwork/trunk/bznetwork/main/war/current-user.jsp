<%@page import="jw.bznetwork.server.*"%>
<%@page import="jw.bznetwork.client.data.*"%>
<%
    boolean authStatus = (request.getSession(false) != null && request
            .getSession().getAttribute("user") != null);
%>
{"auth":
<%="" + authStatus%>
<%
    if (authStatus)
    {
        AuthUser user = (AuthUser) request.getSession().getAttribute(
                "user");
%>
,"username":"<%=user.getUsername().replace("\\", "\\\\").replace(
                                "\"", "\\\"")%>" ,"provider":"<%=user.getProvider()%>"
<%
    }
%>
}
