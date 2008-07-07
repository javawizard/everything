<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Welcome to the OpenGroove Realm Server administration interface. Use the tabs above to
navigate around. If you need help, you can use the <a href="${requestScope.actions.help}">
Help</a> tab.<br/><br/>
<c:if test="${requestScope.hasNeededItems}">There are a few items that need your
attention:<br/>
<ul>
<c:forEach items="${requestScope.neededItems}" var="item">
<li>${item}</li>
</c:forEach>
</ul>
</c:if>