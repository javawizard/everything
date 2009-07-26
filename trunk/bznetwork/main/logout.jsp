
<%
    request.getSession().invalidate();
%>
<html>
<body>
You have been successfully logged out. If you'd like, you can
<a href="<%=request.getContextPath()%>/index.jsp">log back in again</a>.
</body>
</html>