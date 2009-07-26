<%@page import="jw.bznetwork.server.BZNetworkServer"%>
<%@page import="jw.bznetwork.client.AuthProvider"%>
<%
    response.setHeader("Cache-control", "no-cache, must-revalidate");
    response.setHeader("Expires", "Fri, 01 Jan 1990 00:00:00 GMT");
%><html>
<head>

<!--
      We need to go see what the default authentication provider is, and
      redirect to that provider's url. If there is no default provider,
      then we'll redirect to BZTraining.html?mode=choose-auth-provider, 
      which will show the list of auth providers to choose from.
      
      BZTraining.html, when called with no arguments and when the user
      has not logged in, will redirect to us. Since it does this, we
      must never redirect to it without specifying some parameters,
      unless the user has already logged in.
-->

<%
    String targetUrl = null;
    String defaultProviderId = BZNetworkServer.getDefaultAuthProvider();
    if (!BZNetworkServer.isInstalled())
    {
        targetUrl = request.getContextPath() + "/install.jsp";
    }
    else
    {
        AuthProvider[] enabledProviders = BZNetworkServer
                .getEnabledAuthProviders();
        if (defaultProviderId == null)
            targetUrl = request.getContextPath()
                    + "/BZNetwork.html?mode=choose-auth-provider";
        else
        {
            for (AuthProvider provider : enabledProviders)
            {
                if (provider.getId().equals(defaultProviderId))
                {
                    targetUrl = provider.getUrl();
                    break;
                }
            }
        }
    }
    if (targetUrl == null)
        throw new ServletException(
                "Malformed configuration; targetUrl is null, defaultProvider is "
                        + defaultProviderId);
%>
<meta http-equiv="refresh" content="0;url=<%=targetUrl%>" />
</head>
<body>
<a href="<%=targetUrl%>"><%=targetUrl%></a>
</body>
</html>


