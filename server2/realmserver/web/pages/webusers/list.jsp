<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table border="0" cellspacing="0" cellpadding="3">
<a href="${requestScope.actions.addwebuser}">Add</a><br/>
<tr style="border-bottom: thin solid black"><th>username</th><td>&nbsp;</td></tr>
<c:forEach items="${requestScope.webUserList}" var="user">
<tr><td>${user}</td><td><a href="${requestScope.actions.deletewebuser}?username=${user}">
Delete</a></td></tr>
</c:forEach>
</table>