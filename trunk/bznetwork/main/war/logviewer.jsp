
<%
    response.setContentType("text/xml");
%>
<%-- The root element of the log viewer document should be a div tag. 
The gwt app requests the log viewer page in an XHR and then extracts 
the root XML node from it via responseXML and adds it to its own
custom widget. The reason for doing this is that since the logs table 
can be fairly large, constructing it on the server side seems to go 
much faster than constructing it on the client-side. --%>
<div style="width:100%">
<%
String startString = request.getParameter("start");
String endString = request.getParameter("end");
String textSearchString = request.getParameter("search");
String ignoreCaseString = request.getParameter("caseignore");
%>
<% %>
<% %>
</div>