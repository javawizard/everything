<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html><head><title>${requestScope.pageName} - OpenGroove Realm Server</title>
<script type="text/javascript" src="/bypass/jquery.js"></script>
<script type="text/javascript" src="/bypass/animatedcollapse.js"></script>
</head>
<body>
<c:if test="${not empty requestScope.alertMessage}">
<script type="text/javascript">
alert("${requestScope.alertMessage}");
</script>
</c:if>
<table border="0" cellspacing="0" cellpadding="10">
<tr><td>
<table border="0" cellspacing="0" cellpadding="0">
<tr><td colspan="3"><h2 style="display:inline">&nbsp;&nbsp;&nbsp;OpenGroove Realm Server</h2><br/>&nbsp;</td></tr>
<tr><td style="border-bottom: thin solid black"><%for(int i=0;i<10;i++){%>&nbsp;<%}%></td>
<td><table border="0" cellspacing="0" cellpadding="0">
<tr><td style="border-bottom: thin solid black">&nbsp;&nbsp;</td>
<c:forEach items="${requestScope.tabs}" var="tab">
<td style="border: thin solid black<c:if
test="${requestScope.selectedTab==tab.key}">;border-bottom: none</c:if>">
&nbsp;<a href="${requestScope.rendererPath}/${tab.value}"
style="text-decoration: none;color:black;border:thin none">${tab.key}</a>&nbsp;</td>
<td style="border-bottom: thin solid black">&nbsp;&nbsp;</td>
</c:forEach>
<td style="border-bottom: thin solid black">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="/logout"
 style="text-decoration: none;color:black;border:thin none">Log out</a>&nbsp;&nbsp;</td>
</tr>
</table></td>
<td></td>
</tr>
<tr><td style="border-right: thin solid black">&nbsp;</td>
<td><table border="0" cellspacing="0" cellpadding="12"><tr>
<td><jsp:include page="pages/${requestScope.page}"/></td></tr></table></td>
<td>&nbsp;</td></tr>
<tr><td colspan="3" style="border-bottom: thin solid black">&nbsp;</td></tr>
<tr><td></td><td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<small><a 
href="http://www.opengroove.org" style="text-decoration: none">OpenGroove</a>
</small></td></tr>
</td></tr>
</table>
<script type="text/javascript">
animatedcollapse.init();
</script>
</body></html>