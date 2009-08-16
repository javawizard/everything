
<%
    response.setContentType("text/xml");
%>
<%-- The root element of the log viewer document should be a div tag. 
The gwt app requests the log viewer page in an XHR and then extracts 
the root XML node from it via responseXML and adds it to its own
custom widget. The reason for doing this is that since the logs table 
can be fairly large, constructing it on the server side seems to go 
much faster than constructing it on the client-side. --%>

<%@page import="jw.bznetwork.client.data.model.LogRequest"%>
<%@page import="java.util.Date"%>
<%@page import="net.sf.opengroove.common.utils.StringUtils"%>
<%@page import="jw.bznetwork.client.screens.LogsScreen"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="jw.bznetwork.client.Perms"%>
<%@page import="jw.bznetwork.server.rpc.GlobalLinkImpl"%>
<%@page import="jw.bznetwork.server.data.DataStore"%>
<%@page import="jw.bznetwork.client.data.model.LogEvent"%><div
	style="width: 100%">
<%
    String startString = request.getParameter("start");
    String endString = request.getParameter("end");
    String textSearchString = request.getParameter("search");
    String textSearch = StringEscapeUtils.escapeSql(textSearchString);
    String textSearchLower = textSearch.toLowerCase();
    String ignoreCaseString = request.getParameter("caseignore");
    String[] textSearchInStrings = request
            .getParameterValues("searchin");
    String[] filterServerStrings = request.getParameterValues("server");
    //if filterServerStrings is null, then we'll show the logs of all
    //servers that this user has view-in-server-list on.
    String[] filterEvents = request.getParameterValues("event");
    // if filterEvents is null, then we'll show all events.
    int maxResults = 5000;
    //the maximum results number is pretty much hard-coded right now. It's
    //not intended for practical use, as restricting the interval is a much
    //more logical solution; it's simply to prevent a mis-formed request from
    //tying up the server while it does something such as try to get all of 
    //the logs for all time periods out of the database. The client then warns
    //the user if the result count is 5000 that some results were truncated and
    //that they should decrease the time interval.
    LogRequest filterObject = new LogRequest();
    filterObject.setStart(new Date(Long.parseLong(startString)));
    filterObject.setEnd(new Date(Long.parseLong(endString)));
    String filter = "";
    //filter by search string
    if (textSearchInStrings != null)
        filter += " and ('1' == '2' ";
    for (String s : textSearchInStrings)
    {
        if (!StringUtils.isMemberOf(s, LogsScreen.SEARCH_IN))
        {
            throw new RuntimeException("Invalid searchin: " + s);
        }
        String filterColumn = s;
        String textSearchToUse = textSearch;
        if ("true".equalsIgnoreCase(ignoreCaseString))
        {
            filterColumn = " lower(" + filterColumn + ") ";
            textSearchToUse = textSearchLower;
        }
        filter += " or " + filterColumn + " like '" + textSearchToUse
                + "'";
    }
    if (textSearchInStrings != null)
        filter += " ) ";
    //filter by servers
    ArrayList<Integer> serverIds = new ArrayList<Integer>();
    if (filterServerStrings == null)
    {
        //no servers specified by the user, add them all
    }
    else
    {
        //parse the server ids specified by the user
        for (String s : filterServerStrings)
        {
            serverIds.add(Integer.parseInt(s));
        }
    }
    //remove servers the user doesn't have perms to view the logs of
    for (int server : new ArrayList<Integer>(serverIds))
    {
        if (!Perms.server("view-logs", server, GlobalLinkImpl
                .getServerGroupId(server)))
            serverIds.remove(server);
    }
    //add the servers
    filter += " and ( '1' == '2' ";
    for (int server : serverIds)
    {
        filter += " or serverid = " + server + " ";
    }
    filter += " ) ";
    //filter by events
    filter += " and ( '1' == '2' ";
    for (String s : filterEvents)
    {
        filter += " or event == '" + StringEscapeUtils.escapeSql(s)
                + "' ";
    }
    filter += " ) ";
    //filter by max results
    filter += " limit " + maxResults;
    //add the filter
    filterObject.setFilter(filter);
    //We're done with the filtering. Now we run the query.
    LogEvent[] results = DataStore.searchLogs(filterObject); 
%>
<table border="0" cellspacing="1" cellpadding="1">
<%  %>
</div>











