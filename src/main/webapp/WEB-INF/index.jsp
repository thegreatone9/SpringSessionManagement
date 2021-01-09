<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head/>
	<body>
		<div class="content">
			<h2><i>Test Project</i></h2>

			<c:if test="${options != null}">
			    <form method="post" action="/test">
			    	<select name="option">
			    		<c:forEach items="${options}" var="item" varStatus="loop">
			    			<option value="${loop.index}">
			    			    <c:out value="${item}"/>
			    			</option>
			    		</c:forEach>
			    	</select>
			    	<input type="submit" value="Select Option" />
			    </form>
			</c:if>

			<c:if test="${option != null}">
			    <h3>You chose ${option}</h3>
			</c:if>

			<c:if test="${meals != null}">
			    <form method="post" action="/test">
                	<select name="selection">
                		<c:forEach items="${meals}" var="item" varStatus="loop">
                			<option value="${item}">
                			    <c:out value="${item}"/>
                			</option>
                		</c:forEach>
                	</select>
                	<input type="submit" value="Make a selection" />
                </form>
            </c:if>

            <c:if test="${customers != null}">
                <form method="post" action="/test">
                	<select name="selection">
                		<c:forEach items="${customers}" var="item" varStatus="loop">
                			<option value="${item}">
                			    <c:out value="${item}"/>
                			</option>
                		</c:forEach>
                	</select>
                	<input type="submit" value="Make a selection" />
                </form>
            </c:if>

            <c:if test="${selection != null}">
                <h3>You selected ${selection}</h3>
            </c:if>
		<div/>
	</body>
</html>