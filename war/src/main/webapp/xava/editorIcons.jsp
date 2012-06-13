<% if (p.isKey()) { %>
<img src="<%=request.getContextPath()%>/xava/images/key.gif"/>
<% } else if (p.isRequired()) { %>	
<img src="<%=request.getContextPath()%>/xava/images/required.gif"/>
<% } %> 
<span id="<xava:id name='<%="error_image_" + p.getQualifiedName()%>'/>"> 
<% if (errors.memberHas(p)) { %>
<img src="<%=request.getContextPath()%>/xava/images/error.gif"/>
<% } %>
</span>
