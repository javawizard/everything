

You can't delete yourself. <a href="${requestScope.actions.listwebusers}">Click here</a>
to go back to the list of users.


Are you sure you want to delete the user ${param.username}?<br/>
<form method="post" action="${requestScope.actions.deletewebuser}">
<input type="hidden" name="finalized" value="true"/>
<input type="hidden" name="username" value="${param.username}"/>
<input type="submit" value="Yes"/>
<a href="${requestScope.actions.listwebusers}">No</a>
</form>
