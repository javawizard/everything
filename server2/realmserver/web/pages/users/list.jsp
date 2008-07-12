<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Here's a list of users.<br/>
<a href="${requestScope.actions.adduser}">Add</a><br/>
<table border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${requestScope.userList}" var="user">
<tr><td>${user.username}</td><td><a
href="${requestScope.actions.deleteuser}?username=${user.username}">Delete</a></td>
</tr>
</c:forEach>
</table>