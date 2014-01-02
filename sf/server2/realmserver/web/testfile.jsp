<html><body>
Hello. This is a <b>Test page</b>.
<%
if(request.getParameter("param") != null)
{
%>
You typed <%=request.getParameter("param")%>.
<%}%>
<form method="post" action="">
<input type="text" name="param"/>
<input type="submit" value="submit"/>
</form>
</body></html>