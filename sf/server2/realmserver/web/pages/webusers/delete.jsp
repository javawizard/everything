<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${sessionScope.username==param.username}">
You can't delete yourself. <a href="${requestScope.actions.listwebusers}">Click here</a>
to go back to the list of users.
</c:if>
<c:if test="${sessionScope.username!=param.username}">
Are you sure you want to delete the web user ${param.username}?<br/>
<form method="post" action="${requestScope.actions.deletewebuser}">
<input type="hidden" name="finalized" value="true"/>
<input type="hidden" name="username" value="${param.username}"/>
<input type="submit" value="Yes"/>
<a href="${requestScope.actions.listwebusers}">No</a>
</form>
</c:if>