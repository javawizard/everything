<html>
<html><head><title>${requestScope.pageName} - OpenGroove Realm Server</title></head>
<body>
<table border="0" cellspacing="0" cellpadding="20">
<tr><td>
<table border="0" cellspacing="0" cellpadding="0"/>
<tr><td colspan="3"><h2>OpenGroove Realm Server</h2></td></tr>
<tr><td style="border-bottom: small solid black">&lt;%for(int i=0;i&lt;30;i++){%&gt;&nbsp;&lt;%}%&gt;</td>
<td><table border="0" cellspacing="0" cellpadding="0">
<tr>
<c:forEach items="${}" var="tab">
<td style="border: thin solid black<c:if test=">&nbsp;&nbsp;</td>
<td style="border-bottom: thin solid black">&nbsp;&nbsp;</td>
</c:forEach>
</tr>
</table></td>
<td style="border-bottom: small solid black"><a href="logout">Log out</a></td>
</tr>
</td></tr>
</table>
</body></html>