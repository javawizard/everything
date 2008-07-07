<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html><head><title>${requestScope.pageName} - OpenGroove Realm Server</title></head>
<body>
<table border="0" cellspacing="0" cellpadding="20">
<tr><td>
<table border="0" cellspacing="0" cellpadding="0">
<tr><td colspan="3"><h2>OpenGroove Realm Server</h2></td></tr>
<tr><td style="border-bottom: small solid black"><%for(int i=0;i<30;i++){%>&nbsp;<%}%></td>
<td><table border="0" cellspacing="0" cellpadding="0">
<tr><td style="border-bottom: thin solid black">&nbsp;&nbsp;</td>
<c:forEach items="${requestScope.tabs}" var="tab">
<td style="border: thin solid black<c:if
test="${requestScope.selectedTab==tab.key}">;border-bottom: none</c:if>">
&nbsp;<a href="${requestScope.rendererPath}/${tab.value}">${tab.key}</a>&nbsp;</td>
<td style="border-bottom: thin solid black">&nbsp;&nbsp;</td>
</c:forEach>
</tr>
</table></td>
<td style="border-bottom: small solid black"><a href="/logout">Log out</a></td>
</tr>
<tr><td style="border-right: small solid black">&nbsp;</td>
<td><jsp:include page="${requestScope.page}"/></td>
<td>&nbsp;</td></tr>
</td></tr>
</table>
</body></html>