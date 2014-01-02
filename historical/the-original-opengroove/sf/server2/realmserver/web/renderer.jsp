<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html><head><title>${requestScope.pageName} - OpenGroove Realm Server</title>
<script type="text/javascript" src="/bypass/jquery.js"></script>
<script type="text/javascript" src="/bypass/animatedcollapse.js"></script>
<script type="text/javascript">
if( typeof XMLHttpRequest == "undefined" )
  XMLHttpRequest = function() {
    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0") } catch(e) {}
    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0") } catch(e) {}
    try { return new ActiveXObject("Msxml2.XMLHTTP") }     catch(e) {}
    try { return new ActiveXObject("Microsoft.XMLHTTP") }  catch(e) {}
    throw new Error( "This browser does not support XMLHttpRequest or XMLHTTP." )
  };
  function requestAsString(url)
  {
      var request = new XMLHttpRequest();
      request.open("POST", url, false);
      request.send(null);
      return request.responseText;
  }
  function trim(s)
  {
      var l=0; var r=s.length -1;
      while(l < s.length && (s[l] == ' ' || s[l] == '\r' || s[l] == '\n' || s[l] == '\t'))
      {   l++; }
      while(r > l && (s[r] == ' ' || s[r] == '\r' || s[r] == '\n' || s[r] == '\t'))
      {   r-=1;   }
      return s.substring(l, r+1);
  }
  
</script>
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
<tr><td><%for(int i=0;i<10;i++){%>&nbsp;<%}%></td>
<td align="right">
<a href="/logout"
 style="text-decoration: none;color:black;border:thin none">Log out</a>&nbsp;&nbsp;
</td>
<td></td>
</tr>
<tr><td valign="top">
<table width="100" border="0" cellspacing="0" cellpadding="1"
style="border-right:thin solid black">
<c:forEach items="${requestScope.tabs}" var="tab">
<tr><td>
&nbsp;<c:if
test="${requestScope.selectedTab==tab.key}"><b></c:if>
<a href="${requestScope.rendererPath}/${tab.value}"
style="text-decoration: none;color:black;border:thin none">${tab.key}</a>&nbsp;
<c:if test="${requestScope.selectedTab==tab.key}"></b></c:if>
</td></tr>
</c:forEach></table></td>
<td valign="top"><table border="0" cellspacing="0" cellpadding="12"><tr>
<td valign="top"><jsp:include page="pages/${requestScope.page}"/>
</td></tr></table></td>
<td>&nbsp;</td></tr>
<tr><td>&nbsp;</td><td
 colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<small><a 
href="http://www.opengroove.org" style="text-decoration: none">OpenGroove</a>
</small></td></tr>
</td></tr>
</table>
<script type="text/javascript">
animatedcollapse.init();
</script>
</body></html>