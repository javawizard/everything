
<%
    response.setContentType("text/xml");
%>
<%-- The root element of the log viewer document should be a div tag. 
The gwt app requests the log viewer page in an XHR and then extracts 
the root XML node from it via responseXML and adds it to its own
custom widget. The reason for doing this is that since the logs table 
can be fairly large, constructing it on the server side seems to go 
much faster than constructing it on the client-side. --%>
<div style="width: 100%">
<%
    String startString = request.getParameter("start");
    String endString = request.getParameter("end");
    String textSearchString = request.getParameter("search");
    String ignoreCaseString = request.getParameter("caseignore");
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
    //the logs for all time periods out of the database.
%>
</div>











