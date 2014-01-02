<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty param.helpPage}">
<a 
href="${requestScope.actions.help}"><<</a> <big>
<big><%= request.getParameter("helpPage").replace("_"," ").substring(0,request
.getParameter("helpPage").length()-".jsp".length()) %></big></big><br/><br/>
<jsp:include page="/pages/help/contents/${param.helpPage}"/>
</c:if>
<c:if test="${empty param.helpPage}">
Choose a help page from the list below.<br/>
<ul>
<c:forEach items="${requestScope.helpFiles}" var="helpPage">
<li><a href="${requestScope.actions.help}?helpPage=${helpPage.page}"
>${helpPage.title}</li>
</c:forEach>
</ul>
</c:if>