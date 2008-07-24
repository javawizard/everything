<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
This page shows the current status of your OpenGroove Realm Server.<br/>
<br/>
<a href="javascript:animatedcollapse.show('notifyDiv')">Send notification to everyone</a>
<jsp:include page="/notify.jsp"><jsp:param name="divId" 
value="notifyDiv"/></jsp:include>
<table border="0" cellspacing="0" cellpadding="3">
<tr><td colspan="2"><b>Memory:</b></td></tr>
<tr><td>Free memory:</td><td><%= Runtime.getRuntime().freeMemory()/1024 %>KB</td></tr>
<tr><td>Total memory:</td><td><%= Runtime.getRuntime().totalMemory()/1024 %>KB</td></tr>
<tr><td>Max memory:</td><td><%= Runtime.getRuntime().maxMemory()/1024 %>KB</td></tr>
<tr><td colspan="2"><b>Connections:</b></td></tr>
<tr><td>Active connections:</td><td><%= 
net.sf.opengroove.realmserver.OpenGrooveRealmServer.getNumConnections() %></td></tr>
<tr><td>Authenticated connections:</td><td><%= 
net.sf.opengroove.realmserver.OpenGrooveRealmServer.getNumAuthConnections() %></td></tr>
<tr><td>Computer connections:</td><td><%= 
net.sf.opengroove.realmserver.OpenGrooveRealmServer.getNumComputerAuthConnections() 
%></td></tr>
</table>