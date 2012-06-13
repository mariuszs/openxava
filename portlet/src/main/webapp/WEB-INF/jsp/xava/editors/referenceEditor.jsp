<jsp:include page="../detail.jsp"> 
	<jsp:param name="viewObject" value='<%=request.getParameter("viewObject")%>' />
	<jsp:param name="propertyPrefix" value='<%=request.getParameter("propertyPrefix")%>' />
	<jsp:param name="first" value='<%=request.getParameter("first")%>' /> 
	<jsp:param name="last" value='<%=request.getParameter("last")%>' />
</jsp:include>
