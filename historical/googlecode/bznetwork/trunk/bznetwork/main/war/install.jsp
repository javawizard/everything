
<%@page import="java.io.File"%>
<%@page import="jw.bznetwork.server.BZNetworkServer"%><html>
<body style="font-family: sans-serif"
	onload="document.theform.thesubmitbutton.disabled=false">
<%
    request.getSession().invalidate();
    File configFolder = new File(application
            .getRealPath("/WEB-INF/config"));
    if (BZNetworkServer.isInstalled())
    {
%>
BZNetwork is already installed on this server, and doesn't need to be
installed again.
<%
    }
    else
    {
%>
<!-- We haven't installed BZNetwork yet. In that case, we'll show the 
form that allows the user to enter their install information. -->
<%
    if (BZNetworkServer.isWindows)
        {
%>
<b>This BZNetwork server appears to be running on Windows.</b>
Windows is not an officially supported platform. You may encounter some
issues when using BZNetwork. If you do encounter such issues, go to
<a href="http://bznetwork.googlecode.com">bznetwork.googlecode.com</a>
, go to the Issues tab, and file an issue report on the error. 
<hr />
<%
    }
%>
Welcome to a new installation of BZNetwork. Before you can log in,
you'll need to provide some information that BZNetwork can use to set
everything up. Make sure that you've placed BZNetwork in a folder whose
full path does not contain any HTML special characters (&gt;, &lt;,
&quot;, and &amp; specifically).
<%
    String defaultDbDriver = "org.h2.Driver";
        if (System.getenv("BZN_DEV_DB_DRIVER") != null)
            defaultDbDriver = System.getenv("BZN_DEV_DB_DRIVER");
        String defaultDbUrl = "jdbc:h2:"
                + application
                        .getRealPath("/WEB-INF/config/db/bznetwork");
        if (System.getenv("BZN_DEV_DB_URL") != null)
            defaultDbUrl = System.getenv("BZN_DEV_DB_URL");
        String defaultDbUsername = "sa";
        if (System.getenv("BZN_DEV_DB_USERNAME") != null)
            defaultDbUsername = System.getenv("BZN_DEV_DB_USERNAME");
        String defaultDbPassword = "";
        if (System.getenv("BZN_DEV_DB_PASSWORD") != null)
            defaultDbPassword = System.getenv("BZN_DEV_DB_PASSWORD");
        String defaultStoreFolder = application
                .getRealPath("/WEB-INF/config/store");
        if (System.getenv("BZN_DEV_STORE_FOLDER") != null)
            defaultStoreFolder = System.getenv("BZN_DEV_STORE_FOLDER");
        String defaultCacheFolder = application
                .getRealPath("/WEB-INF/config/cache");
        if (System.getenv("BZN_DEV_CACHE_FOLDER") != null)
            defaultCacheFolder = System.getenv("BZN_DEV_CACHE_FOLDER");
%>
<br />
<form name="theform" method="post" action="doinstall.jsp"
	onsubmit="document.theform.thesubmitbutton.disabled=true;return true;">
<table border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td colspan="2">
		<hr />
		</td>
	</tr>
	<tr>
		<td width="250px"><b>Database Driver:</b></td>
		<td><input type="text" name="db-driver"
			value="<%=defaultDbDriver%>" /></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><small>Currently, this can be either <tt>org.h2.Driver</tt>
		or <tt>com.mysql.jdbc.Driver</tt>. You can place another JDBC driver
		jar file in <tt><%=application.getRealPath("/WEB-INF/lib")%></tt> and
		then use that driver, if you want. The default values (the ones that
		these text fields are pre-populated with) will cause BZNetwork to use
		an embedded database which will store its data in the folder <tt><%=application.getRealPath("/WEB-INF/config/db")%></tt>.
		Change this field to <tt>com.mysql.jdbc.Driver</tt> if you plan on
		using MySQL instead.</small></td>
	</tr>
	<tr>
		<td><b>Database URL:</b></td>
		<td><input type="text" name="db-url" value="<%=defaultDbUrl%>" /></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><small>As mentioned above, the default values result
		in an embedded database. If you're planning on using MySQL, then use <tt>jdbc:mysql://SERVER-NAME:PORT/DB-NAME</tt>.
		For example, if you wanted BZNetwork to use the database bznetwork on
		the local computer, you'd use <tt>jdbc:mysql://localhost/bznetwork</tt>.</small></td>
	</tr>
	<tr>
		<td><b>Database Username:</b></td>
		<td><input type="text" name="db-username"
			value="<%=defaultDbUsername%>" /></td>
	</tr>
	<tr>
		<td><b>Database Password:</b></td>
		<td><input type="password" name="db-password"
			value="<%=defaultDbPassword%>" /></td>
	</tr>
	<tr>
		<td><b>Store Folder:</b></td>
		<td><input type="text" name="store-folder"
			value="<%=defaultStoreFolder%>" /></td>
	<tr>
		<td>&nbsp;</td>
		<td><small>This folder stores groupdb files, maps, bans,
		and such. Both BZNetwork and bzfs must have read/write access to this
		folder.</small></td>
	</tr>
	<tr>
		<td><b>Cache Folder:</b></td>
		<td><input type="text" name="cache-folder"
			value="<%=defaultCacheFolder%>" /></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><small>This folder stores files generated when a bzfs
		server is started by BZNetwork. BZNetwork must have read/write access
		to this folder, but bzfs only needs read access to this folder.</small></td>
	</tr>
	<tr>
		<td colspan="2">
		<hr />
		</td>
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
		some text that you will need to copy into this file.<br />
		<br />
		If BZNetwork has already been installed in the specified database, you
		will receive a warning. You will still be able to continue (and
		BZNetwork will preserve your existing data), but you should
		double-check that the store folder that you're using is the same store
		folder that you used with your previous installation. The cache folder
		can be different without any consequences, as its contents are deleted
		every time BZNetwork starts up.<br />
		<br />
		Ok, now you're really ready to set up BZNetwork. Go ahead and click
		that install button.</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><input type="submit" name="thesubmitbutton" value="Install"
			style="font-weight: bold" /></td>
	</tr>

</table>
</form>
<%
    }
%>
</body>
</html>


























