
<%@page import="java.io.File"%><html>
<body>
<%
    File configFolder = new File(application
            .getRealPath("/WEB-INF/config"));
    if (configFolder.exists())
    {
%>
BZNetwork is already installed on this server, and doesn't need to be
installed again.
<%
    }
    else
    {
%>
<%-- We haven't installed BZNetwork yet. In that case, we'll show the form that allows the user to enter their install information. --%>
Welcome to a new installation of BZNetwork. Before you can log in,
you'll need to provide some information that BZNetwork can use to set
everything up. Make sure that you've placed BZNetwork in a folder whose
full path does not contain any HTML special characters (&gt;, &lt;,
&quot;, and &amp; specifically).
<br />
<form method="post" action="doinstall.jsp" onsubmit="document.thesubmitbutton.disabled=true;return true;">
<table border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td>Database Driver:</td>
		<td><input type="text" name="db-driver" value="org.h2.Driver" /></td>
	</tr>
	<tr>
		<td colspan="2"><small>Currently, this can be either <tt>org.h2.Driver</tt>
		or <tt>com.mysql.jdbc.Driver</tt>. You can place another JDBC driver
		jar file in <tt><%=application.getRealPath("/WEB-INF/lib")%></tt> and
		then use that driver, if you want. The default values (the ones that
		these text fields are pre-populated with) will cause BZNetwork to use
		an embedded database which will store its data in the folder <tt><%=application.getRealPath("/WEB-INF/config/db")%></tt>.
		Change this field to <tt>com.mysql.jdbc.Driver</tt> if you plan on
		using MySQL instead.</small></td>
	</tr>
	<tr>
		<td>Database URL:</td>
		<td><input type="text" name="db-url"
			value="jdbc:h2:<%=application
                                .getRealPath("/WEB-INF/config/db/bznetwork")%>" /></td>
	</tr>
	<tr>
		<td colspan="2"><small>As mentioned above, the default
		values result in an embedded database. If you're planning on using
		MySQL, then use <tt>jdbc:mysql://SERVER-NAME:PORT/DB-NAME</tt>. For
		example, if you wanted BZNetwork to use the database bznetwork on the
		local computer, you'd use <tt>jdbc:mysql://localhost/bznetwork</tt>.</small></td>
	</tr>
	<tr>
		<td>Database Username:</td>
		<td><input type="text" name="db-username" value="sa" /></td>
	</tr>
	<tr>
		<td>Database Password:</td>
		<td><input type="password" name="db-password" /></td>
	</tr>
	<tr>
		<td>Store Folder:</td>
		<td><input type="text" name="store-folder"
			value="<%=application
                                        .getRealPath("/WEB-INF/config/store")%>" /></td>
	<tr>
		<td colspan="2"><small>This folder stores groupdb files,
		maps, bans, and such. Both BZNetwork and bzfs must have read/write
		access to this folder.</small></td>
	</tr>
	<tr>
		<td>Cache Folder:</td>
		<td><input type="text" name="cache-folder"
			value="<%=application
                                        .getRealPath("/WEB-INF/config/cache")%>" /></td>
	</tr>
	<tr>
		<td colspan="2"><small>This folder stores files generated
		when a bzfs server is started by BZNetwork. BZNetwork must have
		read/write access to this folder, but bzfs only needs read access to
		this folder.</small></td>
	</tr>
	<tr>
		<td colspan="2">You're now ready to set up BZNetwork. Make sure
		you've double-checked everything. When you're ready, click <b>Install</b>.
		Then wait a minute while BZNetwork writes configuration files and
		creates database tables.<br />
		<br />
		BZNetwork will try to write to <tt><%=application
                                .getRealPath("/WEB-INF/config/settings.props")%></tt>
		while installing. If this file can't be written to, you will be given
		some text that you will need to copy into this file.</td>
	</tr>
<tr><td>&nbsp;</td><td><input type="submit" name="thesubmitbutton" value="Install"/></td></tr>

</table>
</form>
<%
    }
%>
</body>
</html>


























