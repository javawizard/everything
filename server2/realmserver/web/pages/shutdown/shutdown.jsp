<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${empty param.shutdown}">
Are you sure you want to shut down this OpenGroove Realm Server?<br/>
<form method="POST" action="">
<input type="hidden" name="shutdown" value="true"/>
<input type="submit" value="Yes"/>
</form>
<a href="${requestScope.actions.welcome}">No</a><br/>
</c:if>
<%if(request.getParameter("shutdown") != null){
net.sf.opengroove.realmserver.OpenGrooveRealmServer.serverSocket.close();%>
The server is now in the process of shutting down.
<%}%>
