<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- MAKE SURE TO ADD PARAMS FOR WHO TO SEND THE NOTIFICATION TO --%>
<script type="text/javascript">
animatedcollapse.addDiv('${param.divId}','fade=1,speed=400');
function submitNotification_${param.divId}()
{
    var snform = document.${param.divId}_form;
    var subject = snform.subject.value;
    if(subject.indexOf("+") != -1)
    {
        alert("The subject cannot contain the plus character.");
        return;
    }
    var priority = snform.priority.options[snform.priority.selectedIndex].value;
    var dismiss = snform.dismiss.options[snform.dismiss.selectedIndex].value;
    var message = snform.message.value;
    if(message.indexOf("+") != -1)
    {
        alert("The message cannot contain the plus character.");
        return;
    }
    var url = "/sendnotify.jsp?to=" + "<c:if test="${empty param.recipient}">all</c:if>${param.recipient}" + "&subject=" + escape(subject) + "&priority=" + priority + "&dismiss=" + dismiss + "&message=" + escape(message);
    var response = requestAsString(url);
    if(response.length < 1 || response.substring(0,1) != 't')
    {
        alert("Sending the notification failed, for the following reason:\n\n" + response.substring(1));
        return;
    }
    alert("The notification was successfully sent.");
    snform.subject.value="";
    snform.message.value="";
    animatedcollapse.hide("${param.divId}");
}
</script>
<div id="${param.divId}" 
style="border: thin solid #dddddd; background-color: #eeeeee; display:none">
<form name="${param.divId}_form" action="">
<table border="0" cellspacing="0" cellpadding="4">
<tr><td>Subject:&nbsp;</td><td><input type="text" name="subject"/></td></tr>
<tr><td>Priority:&nbsp;</td><td><select name="priority" size="1">
<option value="info" selected="selected">Info</option>
<option value="alert">Alert</option>
<option value="critical">Critical</option>
</select></td></tr>
<tr><td>Dismiss in:</td><td><select name="dismiss" size="1">
<c:forEach begin="1" end="59" varStatus="cti">
<option value="${cti.count}" 
<c:if test="${cti.count==10}">selected="selected"</c:if> >
${cti.count} minute<c:if test="${cti.count != 1}">s</c:if></option>
</c:forEach>
<c:forEach begin="1" end="23" varStatus="cti">
<option value="${cti.count*60}">
${cti.count} hour<c:if test="${cti.count != 1}">s</c:if></option>
</c:forEach>
<c:forEach begin="1" end="6" varStatus="cti">
<option value="${cti.count*60*24}">
${cti.count} day<c:if test="${cti.count != 1}">s</c:if></option>
</c:forEach>
<c:forEach begin="1" end="8" varStatus="cti">
<option value="${cti.count*60*24*7}">
${cti.count} week<c:if test="${cti.count != 1}">s</c:if></option>
</c:forEach>
</select></td></tr>
<tr><td colspan="2"><textarea name="message" rows="10" cols="40">
</textarea></td></tr><tr><td>
<button type="button" onclick="submitNotification_${param.divId}();">Send</button>
<button type="button" onclick="document.${param.divId}_form.subject.value='';document.${param.divId}_form.message.value='';animatedcollapse.hide('${param.divId}');">Cancel</button>
</td><td></td></tr></table>
</form>
</div>
