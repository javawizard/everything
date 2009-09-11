
<%
    request.getSession().invalidate();
%>

<%@page import="jw.bznetwork.client.Settings"%><html>
<%
    String customLogout = Settings.customlogout.getString();
    if (request.getParameter("url") != null)
        customLogout = request.getParameter("url");
    if (customLogout.trim().equals(""))
    {
%>
<body>
You have been successfully logged out. If you'd like, you can
<a href="<%=request.getContextPath()%>/index.jsp">log back in again</a>
.
</body>
<%
    }
    else
    {
%>
<head>
<meta http-equiv="refresh" content="0;url=<%=customLogout%>" />
</head>
<body>
<a href="<%=customLogout%>"><%=customLogout%></a>
</body>
<%
    }
%>
</html>