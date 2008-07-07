
<html><head><title>Login - OpenGroove Realm Server</title></head>
<body>
<h2>OpenGroove Realm Server Administration</h2>
<form name="loginForm" method="post" action="/login">
<table border="0" cellspacing="0" cellpadding="3">
<%if(request.getParameter("errormessage")!=null){%>
<tr><td colspan="2"><span style="color: red"><b>${param.errormessage}</b></span></td></tr>
<%}%>
<tr><td colspan="2">Please enter your username and password.</td></tr>
<tr><td>Username:</td><td><input type="text" name="username"/></td></tr>
<tr><td>Password:</td><td><input type="password" name="password"/></td></tr>
<tr><td></td><td><input type="submit" value="Login"/></td></tr>
</table>
</form>
<script type="text/javascript">
document.loginForm.username.focus();
</script>
</body></html>