
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="jw.bznetwork.server.data.DataStore"%>
<%@page import="jw.bznetwork.client.data.model.Authgroup"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URL"%>

<%@page import="java.util.ArrayList"%>
<%@page import="jw.bznetwork.server.BZNetworkServer"%>
<%@page import="jw.bznetwork.client.data.model.Callsign"%><html>
<%
    if (request.getSession(false) != null
            && request.getSession().getAttribute("user") != null)
    {
%><head>
<meta http-equiv="refresh"
	content="0;url=<%=request.getContextPath()%>/" />
</head>
<body>
<a href="<%=request.getContextPath()%>/"><%=request.getContextPath()%>/</a>
</body>
<%
    }
    else
    {
        //We should have two parameters, callsign and token.
        String callsign = request.getParameter("callsign");
        if (callsign.contains(":"))
            throw new RuntimeException(
                    "Callsigns with : in them are not allowed.");
        String token = request.getParameter("token");
        if (callsign == null || token == null)
            throw new RuntimeException(
                    "invalid input; callsign and token parameters needed.");
        try
        {
            Authgroup[] authGroups = DataStore.listAuthgroups();
            StringBuffer grouplistBuffer = new StringBuffer();
            for (Authgroup group : authGroups)
            {
                grouplistBuffer.append(
                        URLEncoder.encode(group.getName())).append(
                        "\r\n");
            }
            String grouplist = grouplistBuffer.toString();
            if (grouplist.length() > 0)
                grouplist = grouplist.substring(0,
                        grouplist.length() - 2);
            if (callsign.contains("="))
                throw new RuntimeException(
                        "Callsigns with = in them are not allowed.");
            String checkTokens = callsign + "="
                    + URLEncoder.encode(token);
            URL url = new URL(
                    "http://my.bzflag.org/db/?action=CHECKTOKENS&checktokens="
                            + URLEncoder.encode(checkTokens)
                            + "&groups=" + URLEncoder.encode(grouplist));
            InputStream stream = url.openStream();
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(stream));
            String firstLine = streamReader.readLine();
            String secondLine = streamReader.readLine();
            if (!secondLine.startsWith("TOKGOOD: "))
                throw new RuntimeException(
                        "BZFlag returned invalid data: " + secondLine);
            String[] secondLineSplit = secondLine.split(":");
            ArrayList<String> bzflagGroups = new ArrayList<String>();
            for (int i = 2; i < secondLineSplit.length; i++)
            {
                //We start at 2 because 0 is "TOKGOOD" and 1 is the callsign.
                bzflagGroups.add(secondLineSplit[i]);
            }
            Callsign callsignObject = DataStore
                    .getCallsignByName(callsign);
            if (bzflagGroups.size() == 0 && callsignObject == null)
            {
%>

<body>
You entered a correct callsign and password, but you don't have any
permissions at this server.
<a href="<%=request.getContextPath()%>">Log in with different
credentials</a>
.
</body>
<%
    }
            else
            {
                String targetUrl = request.getContextPath()
                        + "/BZNetwork.html";
                int[] roles = new int[bzflagGroups.size()
                        + (callsignObject != null ? 1 : 0)];
                int index = 0;
                for (String group : bzflagGroups)
                {
                    Authgroup authgroup = DataStore
                            .getAuthgroupByName(group);
                    roles[index++] = authgroup.getRole();
                }
                if (callsignObject != null)
                    roles[roles.length - 1] = callsignObject.getRole();
                BZNetworkServer.login(request, "callsign", callsign,
                        roles);
%>
<head>
<meta http-equiv="refresh" content="0;url=<%=targetUrl%>" />
</head>
<body>
You have successfully logged in.
<a href="<%=targetUrl%>">Continue to the site.</a>
</body>
<%
    }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
%>
</html>