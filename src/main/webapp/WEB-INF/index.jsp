<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
	<head/>
	<body>
		<div class="content">
		    <c:set var = "uiid" scope = "request" value = "${uiid}"/>

			<h2><i>Test Project</i></h2>

			<c:if test="${commandObject.options != null}">
			    <form:form method="post" action="/test" modelAttribute="commandObject">
			    	<form:select path="optionId">
			    		<c:forEach items="${commandObject.options}" var="item" varStatus="loop">
			    			<form:option value="${loop.index}">
			    			    <c:out value="${item}"/>
			    			</form:option>
			    		</c:forEach>
			    	</form:select>
			    	<h2>${uiid}</h2>
			    	<input type="hidden" name="uiid" value="${uiid}"/>
			    	<input type="submit" value="Select Option" />
			    </form:form>
			</c:if>

			<c:if test="${commandObject.option != null}">
			    <h3>You chose ${commandObject.option}</h3>
			</c:if>

			<c:if test="${commandObject.meals != null}">
			    <form:form method="post" action="/test" modelAttribute="commandObject">
                	<form:select path="selection">
                		<c:forEach items="${commandObject.meals}" var="item" varStatus="loop">
                			<form:option value="${item}">
                			    <c:out value="${item}"/>
                			</form:option>
                		</c:forEach>
                	</form:select>
                	<input type="hidden" name="uiid" value="${uiid}"/>
                	<input type="submit" value="Make a selection" />
                </form:form>
            </c:if>

            <c:if test="${commandObject.customers != null}">
                <form:form method="post" action="/test" modelAttribute="commandObject">
                	<form:select path="selection">
                		<c:forEach items="${commandObject.customers}" var="item" varStatus="loop">
                			<form:option value="${item}">
                			    <c:out value="${item}"/>
                			</form:option>
                		</c:forEach>
                	</form:select>
                	<input type="hidden" name="uiid" value="${uiid}"/>
                	<input type="submit" value="Make a selection" />
                </form:form>
            </c:if>

            <c:if test="${commandObject.selection != null}">
                <h3>You selected ${commandObject.selection}</h3>
            </c:if>
		<div/>
	</body>
</html>