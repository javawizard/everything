<html><head><title>Setup - OpenGroove Realm Server</title></head>
<body>
<%if(request.getParameter("errormessage") != null){%>
<b><span style='color: red'>${param.errormessage}</span></b><br/>
<%}%>
<b>You've successfully started up an OpenGroove Realm Server. 
We need to collect some information from you so that the server can be configured.
Please provide the information below, then click Setup.</b><br/>
<br/>
<form method="POST" action="s2.html">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">Choose a username and password that you can use to login to the web
administration interface. This is <i>not</i> an OpenGroove username.</td></tr>
<tr><td>Username:</td><td><input type="text" name="username" value="${param.username}"/></td>
</tr>
<tr><td>Password:</td><td><input type="password" name="password" value="${param.password}"/>
</td></tr>
<tr><td>Password again:</td><td>
<input type="password" name="passwordagain" value="${param.passwordagain}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">Fill out the information for the database you want OpenGroove Realm
Server to use to store it's data. OpenGroove will still store large information, such as
the contents of stored messages, in the filesystem, but configuration and other general
information will be stored in the database. The default database information will create
a new database for you, and you can normally leave this section alone. If you choose to
use a custom database, the driver
for the database should be on the classpath used to start the server (if it's in a jar file
you can put it into the lib folder, and you will need to restart the server before you can
use the driver).</td></tr>
<tr><td>Driver class:</td><td><input type="text" name="dbclass" value="${param.dbclass}"/></td></tr>
<tr><td>Database URL:</td><td><input type="text" name="dburl" value="${param.dburl}"/></td></tr>
<tr><td>Table Prefix:</td><td><input type="text" name="dbprefix" value="${param.dbprefix}"/></td></tr>
<tr><td>Database Username:</td><td><input type="text" name="dbusername" value="${param.dbusername}"/></td></tr>
<tr><td>Database Password:</td><td><input type="password" name="dbpassword" value="${param.dbpassword}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">The next few settings are for general configuration of the server.
Unless noted, these settings <i>cannot</i> be changed later.</td></tr>
<tr><td>Server port:</td><td><input type="text" name="serverport" value="${param.serverport}"/></td></tr>
<tr><td>Web port:</td><td><input type="text" name="webport" value="${param.webport}"/></td></tr>
<tr><td>Public server hostname<sup><a target="_blank"
href="http://www.opengroove.org/help/public-server-hostname">?</a></sup>:</td>
<td><input type="text" name="serverhostname" value="${param.serverhostname}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td>&nbsp;</td><td><input type="submit" 
onclick="return confirm('Are you sure you want to setup the server?');"
value="Setup"/></td></tr></table>
</body></html>

