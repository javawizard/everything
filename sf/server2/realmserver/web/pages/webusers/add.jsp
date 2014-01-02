<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Enter the new web user's information:<br/>
<form method="post" action="${requestScope.actions.addwebuser}">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td><input type="text" name="username"/></td></tr>
<tr><td>Password:</td><td><input type="password" name="password"/></td></tr>
<tr><td>Password again:</td><td><input type="password" name="passwordagain"/></td></tr>
<tr><td>&nbsp;</td><td><input type="submit" value="create"/></td></tr>
</table>
</form>