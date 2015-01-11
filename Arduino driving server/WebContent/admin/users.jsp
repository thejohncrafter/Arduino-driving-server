<%@ page isELIgnored="false" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Users" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Permissions" %>
<%@ page import="com.ArduinoDrivingServer.web.beans.User" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<ADS:ifPerm permission="userlist" minValue="ALL">
	<script type="text/javascript">
		
		function submit_edit_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:username});
			
		}
		
		function submit_new_user(){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.new'});
			
		}
		
		function submit_view_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', user:username});
			
		}
		
		function submit_edit_userlist(){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.list.edit'});
			
		}
		
		function submit_remove_user(username){
			
			var r = confirm(('Are you sure you want to remove ' + username + ' ?'));
			
			if(r == true){
				
				post('edit', 'post', {action:'users.remove', user:username});
			    
			}
			
		}
	</script>
</ADS:ifPerm>
<%
if(request.getParameter("edit") == null){
	
	if(request.getParameter("user") == null || Users.getUser((String) request.getParameter("user")) == null){
		%>
		<ADS:ifPerm permission="userlist" minValue="READ" invert="true">
			<ADS:forbiddenMsg message="You are not allowed to consult this page."/>
		</ADS:ifPerm>
		<ADS:ifPerm permission="userlist" minValue="READ">
			<H1>USERS LIST<ADS:ifPerm permission="userlist" minValue="ALL"><a
				href="javascript:submit_edit_userlist()" style="font-size:10px">[edit]</a></ADS:ifPerm></H1>
			<ul>
				<%
				HashMap<String, User> users = Users.getUsers();
				String[] keys = users.keySet().toArray(new String[users.size()]);
				
				for(String key : keys){
					
					User user = users.get(key);
					%>
					<li>
						<a href="javascript:submit_view_user('<%= user.getName() %>')"><%= user.getName() %></a>
					</li>
					<%
				}
				%>
			</ul>
		</ADS:ifPerm>
		<%
	}else{
		
		User user = Users.getUser((String) request.getParameter("user"));
		
		if(session.getAttribute("user") == null // if the session contains any user
				|| (Permissions.getPermission((User) session.getAttribute("user"), "userlist") < Permissions.READ // [ OR if the user hasn't access right
				&& !user.getName().equals(((User) session.getAttribute("user")).getName())) ){ // AND if the "user" param isn't the username ]
			%>
			<ADS:forbiddenMsg message="You are not allowed to consult this page."/>
			<%
		}else{
			%>
			<H1><%= user.getName().toUpperCase() %>'S DATAS<ADS:ifPerm permission="userlist"
							minValue="ALL"><a href="javascript:submit_edit_user('<%= user.getName() %>')"
											style="font-size:10px">[edit]</a></ADS:ifPerm></H1>
			<H2>ACCOUNT :</H2>
			username : <%= user.getName() %><br>
			password : <%= Users.getPassword(user.getName()) %>
			<%
		}
		
	}
	
}else{
	%>
	<ADS:ifPerm permission="userlist" minValue="ALL" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to edit users !"/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="userlist" minValue="ALL">
		<%
		User user = Users.getUser((String) request.getParameter("user"));
		
		String action = "users.edit";
		
		if(request.getParameter("action") != null)
			action = request.getParameter("action");
		
		if(action.equals("users.list.edit")){
			%>
			<H1>EDIT USERS LIST</H1>
			<a href="javascript:submit_new_user()">New user</a>
			<ul>
				<%
				HashMap<String, User> users = Users.getUsers();
				String[] keys = users.keySet().toArray(new String[users.size()]);
				
				for(String key : keys){
					
					User usr = users.get(key);
					%>
					<li>
						<a href="javascript:submit_view_user('<%= usr.getName() %>')"><%= usr.getName() %></a><span 
																								style="font-size:10px">
								<a href="javascript:submit_edit_user('<%= usr.getName() %>')">[edit]</a>
								<%
								if(!usr.getName().equals("sudo")){
									
									%><a href="javascript:submit_remove_user('<%= usr.getName() %>')">[remove]</a><%
									
								}
								%>
							</span>
					</li>
					<%
				}
				%>
			</ul>
			<%
		}else if(action.equals("users.edit")){
			
			if(user== null){
				%>
				<H1>ERROR</H1>
				<span class="error">There is any user named <%= request.getParameter("user") %></span>
				error = true;
				<%
			}else{
				%>
				<H1>EDIT <%= user.getName().toUpperCase() %> </H1>
				<form method="post" action="edit">
					<fieldset style="border:none;margin:0;padding:0;">
						<label for="new_username"><strong>Username</strong></label><br>
						<input <% if(user.getName().equals("sudo")){out.print("readonly");} %>
							type="text" id="new_username" name="new_username" value="<%= user.getName() %>"/>
						<br>
						<label for="new_password"><strong>Password</strong></label><br>
						<input type="text" id="new_password" name="new_password" value="<%= Users.getPassword(user.getName()) %>"/>
						<br>
						<div style="display:none;">
							<input readonly type="text" id="action" name="action" value="<%= action %>"/>
							<input readonly type="text" id="arg:user" name="arg:user" value="<%= request.getParameter("user") %>"/>
						</div>
						<br>
						<input type="submit" value="OK" style="float:center;"/>
					</fieldset>
				</form>
				<%
				if(request.getParameter("error") != null)
					out.print("<span class=\"error\">" + request.getParameter("error") + "</span>");
				
			}
			
		}else if(action.equals("users.new")){
			
			out.print("<H1>NEW USER</H1>");
			%>
			<form method="post" action="edit">
				<fieldset style="border:none;margin:0;padding:0;">
					<label for="new_username"><strong>Username</strong></label><br>
					<input type="text" id="new_username" name="new_username"/>
					<br>
					<label for="new_password"><strong>Password</strong></label><br>
					<input type="text" id="new_password" name="new_password"/>
					<br>
					<div style="display:none;">
						<input readonly type="text" id="action" name="action" value="<%= action %>"/>
					</div>
					<br>
					<input type="submit" value="OK" style="float:center;"/>
				</fieldset>
			</form>
			<%
			if(request.getParameter("error") != null)
				out.print("<span class=\"error\">" + request.getParameter("error") + "</span>");
			
		}else{
			
			out.print("<div class=\"error\">There is any action named " + action + " !</div>");
			
		}
		%>
	</ADS:ifPerm>
	<%
}
%>