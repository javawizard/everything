<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Are you sure you want to delete the web user ${param.username}?<br/>
<form method="post" action="${requestScope.actions.deleteuser}">
<input type="hidden" name="finalized" value="true"/>
<input type="hidden" name="username" value="${param.username}"/>
<input type="submit" value="Yes"/>
<a href="${requestScope.actions.listusers}">No</a>
</form>