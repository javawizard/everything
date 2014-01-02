<html><head><title>Setup - OpenGroove Realm Server</title></head>
<body>
<%if(request.getParameter("errormessage") != null){%>
<b><span style='color: red'>${param.errormessage}</span></b><br/>
<span style='color: red'>If you need help solving problems with setting up your server, visit <a href="http://www.opengroove.org/help/server-setup-problems">www.opengroove.org/help/server-setup-problems</a></span><br/>
<%}%>
<b>You've successfully started up an OpenGroove Realm Server. 
We need to collect some information from you so that the server can be configured.
Please provide the information below, then click Setup.</b><br/>
<br/>
<form method="POST" action="/setup">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">Choose a username and password that you can use to login to the web
administration interface. This is <i>not</i> an OpenGroove username.</td></tr>
<tr><td width="25%">Username:</td><td width="75%"><input type="text" name="username" value="${param.username}"/></td>
</tr>
<tr><td>Password:</td><td><input type="password" name="password" value="${param.password}"/>
</td></tr>
<tr><td>Password again:</td><td>
<input type="password" name="passwordagain" value="${param.passwordagain}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">OpenGroove uses two databases to store it's data. These databases are
called the persistant database and the large database. The persistant database is usually
small and contains information about users registered to this server, and server
configuration information. The large database contains large objects, such as stored
messages, that don't need to be persisted in the event of a serious server crash. Choose
the information you want for the persistant database and the large database. You can use
the same database for both, if you want. <i>You can just leave this information alone to
create the databases automatically.</i> If you choose to
use a custom database, the driver 
for the database should be on the classpath used to start the server (if it's in a jar file
you can put it into the lib folder) <i>before</i> the server starts.</td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">Persistant database:</td></tr>
<tr><td>Driver class:</td><td><input type="text" name="pdbclass" value="${param.pdbclass}"/></td></tr>
<tr><td>Database URL:</td><td><input type="text" name="pdburl" value="${param.pdburl}"/></td></tr>
<tr><td>Table Prefix:</td><td><input type="text" name="pdbprefix" value="${param.pdbprefix}"/></td></tr>
<tr><td>Database Username:</td><td><input type="text" name="pdbusername" value="${param.pdbusername}"/></td></tr>
<tr><td>Database Password:</td><td><input type="password" name="pdbpassword" value="${param.pdbpassword}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">Large database:</td></tr>
<tr><td>Driver class:</td><td><input type="text" name="ldbclass" value="${param.ldbclass}"/></td></tr>
<tr><td>Database URL:</td><td><input type="text" name="ldburl" value="${param.ldburl}"/></td></tr>
<tr><td>Table Prefix:</td><td><input type="text" name="ldbprefix" value="${param.ldbprefix}"/></td></tr>
<tr><td>Database Username:</td><td><input type="text" name="ldbusername" value="${param.ldbusername}"/></td></tr>
<tr><td>Database Password:</td><td><input type="password" name="ldbpassword" value="${param.ldbpassword}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">The next few settings are for general configuration of the server.
Unless noted, these settings <i>cannot</i> be changed later.</td></tr>
<tr><td>Server port:</td><td><input type="text" name="serverport" value="${param.serverport}"/></td></tr>
<tr><td>Web port:</td><td><input type="text" name="webport" value="${param.webport}"/></td></tr>
<tr><td>Realm name<sup><a target="_blank"
href="http://www.opengroove.org/help/server-realm-name">?</a></sup>:</td>
<td><input type="text" name="serverhostname" value="${param.serverhostname}"/></td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">You're ready to set up your server! Click Setup to begin!
<tr><td>&nbsp;</td><td><input name="submitButton" type="submit" 
onclick="if(!confirm('Are you sure you want to setup the server? This will take a minute or two.'))return false;document.mainForm.submitButton.disabled=true;return true;"
value="Setup"/></td></tr></table>
</body></html>

