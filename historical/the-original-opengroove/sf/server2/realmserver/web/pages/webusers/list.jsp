<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Web users are users that can log into the web administration interface. These <i>are not</i>
OpenGroove users. OpenGroove users can be found on the Users tab.<br/>
<a href="${requestScope.actions.addwebuser}">Add</a><br/>
<table border="0" cellspacing="0" cellpadding="3">
<tr style="border-bottom: thin solid black"><th>username</th><td>&nbsp;</td></tr>
<c:forEach items="${requestScope.webUserList}" var="user">
<tr><td>${user}</td><td><a href="${requestScope.actions.deletewebuser}?username=${user}">
Delete</a></td></tr>
</c:forEach>
</table>