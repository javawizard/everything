<!-- This page redirects a user to the url given by the url parameter if they 
are not logged in to BZNetwork. If they are logged into BZNetwork, then they 
are redirected to BZNetwork itself. -->
<html>
<head>
<%
    String targetUrl;
    if (request.getSession(false) != null
            && request.getSession().getAttribute("user") != null)
    {
        targetUrl = request.getContextPath() + "/";
    }
    else
    {
        targetUrl = request.getParameter("url");
    }
%>
<meta http-equiv="refresh" content="0;url=<%=targetUrl%>" />
</head>
<body>
<a href="<%=targetUrl%>"><%=targetUrl%></a>
</body>
</html>