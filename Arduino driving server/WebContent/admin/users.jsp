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
				
				ADS.removeUser(username,function(answ,err){
					
					if(err)
						document.getElementById('errors').innerHTML = err;
					else
						submit_edit_userlist();
					
				});
			    
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
				onclick="submit_edit_userlist()" class="button">[edit]</a></ADS:ifPerm></H1>
			<div class="block">
				<%
				HashMap<String, User> users = Users.getUsers();
				String[] keys = users.keySet().toArray(new String[users.size()]);
				
				for(String key : keys){
					
					User user = users.get(key);
					%>
					<a onclick="submit_view_user('<%= user.getName() %>')"><%= user.getName() %></a><br>
					<%
				}
				%>
			</div>
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
							minValue="ALL"><a onclick="submit_edit_user('<%= user.getName() %>')"
											class="button">[edit]</a></ADS:ifPerm></H1>
			<H2>ACCOUNT :</H2>
			username : <strong><%= user.getName() %></strong><br>
			password : <strong><%= Users.getPassword(user.getName()) %></strong>
			<%
		}
		
	}
	
}else{
	%>
	<ADS:ifPerm permission="userlist" minValue="ALL" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to edit users !"/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="userlist" minValue="ALL">
		<script>
			function submit_json_user(type){
				
				var submit = document.getElementById('submit');
				var errors = document.getElementById('errors');
				var name = document.getElementById('username').value;
				var password = document.getElementById('password').value;
				
				submit.disabled = true;
				
				function callback(answ, err){
					
					submit.disabled = false;
					
					if(answ){
						
						switch(type){
						
						case 'edit' :
						case 'new' :
							submit_edit_user(name);
							break;
						
						}
						
					}else
						errors.innerHTML = err;
					
				};
				
				switch(type){
				
				case 'new' :
					ADS.newUser({name:name,password:password}, callback);
					break;
				case 'edit' :
					var oldUser = document.getElementById('oldUser').value;
					ADS.editUser({name:name,password:password}, oldUser, callback);
					break;
				
				}
				
			}
			
			function trigger_show_password(){
				
				var password = document.getElementById('password');
				var show_trigger = document.getElementById('trigger_password');
				
				switch(password.type){
				
				case 'password' :
					password.type = 'text';
					show_trigger.innerHTML = 'hide';
					break;
				case 'text' :
					password.type = 'password';
					show_trigger.innerHTML = 'show';
					break;
				
				}
				
			}
		</script>
		<%
		User user = Users.getUser((String) request.getParameter("user"));
		
		String action = "users.edit";
		
		if(request.getParameter("action") != null)
			action = request.getParameter("action");
		
		if(action.equals("users.list.edit")){
			%>
			<H1>EDIT USERS LIST</H1>
			<a onclick="submit_new_user()">New user</a>
			<div class="block">
				<%
				HashMap<String, User> users = Users.getUsers();
				String[] keys = users.keySet().toArray(new String[users.size()]);
				
				for(String key : keys){
					
					User usr = users.get(key);
					%>
					<a onclick="submit_view_user('<%= usr.getName() %>')"><%= usr.getName() %></a><a
						class="button" onclick="submit_edit_user('<%= usr.getName() %>')">[edit]</a><%
							
							if(!usr.getName().equals("sudo")){
								
								%><a class="button" onclick="submit_remove_user('<%= usr.getName() %>')">[remove]</a><br><%
								
							}
				}
				%>
			</div>
			<div style="color:red" id="errors"></div>
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
				<strong>Name</strong><br>
				<input type="text" id="username" value="<%= user.getName() %>"/><br>
				<strong>Password</strong><br>
				<input type="password" id="password" value="<%= Users.getPassword(user.getName()) %>"/>
				<button id="trigger_password" onclick="trigger_show_password()">show</button><br>
				<button id="submit" onclick="submit_json_user('edit')">OK</button>
				<input type="hidden" id="oldUser" value="<%= request.getParameter("user") %>"/>
				<div style="color:red" id="errors"></div>
				<%
				if(request.getParameter("error") != null)
					out.print("<span class=\"error\">" + request.getParameter("error") + "</span>");
				
			}
			
		}else if(action.equals("users.new")){
			%>
			<H1>NEW USER</H1>
			<strong>Name</strong><br>
			<input type="text" id="username"/><br>
			<strong>Password</strong><br>
			<input type="password" id="password"/>
			<button id="trigger_password" onclick="trigger_show_password()">show</button><br>
			<button id="submit" onclick="submit_json_user('new')">OK</button>
			<div style="color:red" id="errors"></div>
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