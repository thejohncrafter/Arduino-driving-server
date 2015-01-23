<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<%@ page isELIgnored="false" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Users" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Permissions" %>
<%@ page import="com.ArduinoDrivingServer.web.beans.User" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<ADS:ifPerm permission="userlist" minValue="ALL">
	<script type="text/javascript">
		
		function submit_edit_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:username});
			
		}
		
		function submit_new_user(){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.new'});
			
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
	%>
	<ADS:ifPerm permission="userlist" minValue="READ" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to consult this page."/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="userlist" minValue="READ">
		<H1>USERS LIST<ADS:ifPerm permission="userlist" minValue="ALL"><a
			onclick="submit_edit_userlist()" class="button">[edit]</a></ADS:ifPerm></H1>
		<div class="block">
			<%
			HashMap<Integer, User> users = Users.getUsers();
			Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
			
			for(int key : keys){
				
				User user = users.get(key);
				%>
				<H2><%= user.getName() %></H2>
				<div class="block">
					<H2>ACCOUNT :</H2>
					username : <strong><%= user.getName() %></strong><br>
					password : <strong><%= Users.getPassword(user.getName()) %></strong><br>
					group : <strong><%= user.getPermissionsGroup() %></strong>
				</div>
				<%
			}
			%>
		</div>
	</ADS:ifPerm>
	<%
}else{
	%>
	<ADS:ifPerm permission="userlist" minValue="ALL" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to edit users."/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="userlist" minValue="ALL">
		<script>
			function submit_json_user(type){
				
				var submit = document.getElementById('submit');
				var errors = document.getElementById('errors');
				var name = document.getElementById('username').value;
				var password = document.getElementById('password').value;
				var group = document.getElementById('group').value;
				
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
					ADS.newUser({name:name,password:password,group:group}, callback);
					break;
				case 'edit' :
					var oldUser = document.getElementById('oldUser').value;
					ADS.editUser({name:name,password:password,group:group}, oldUser, callback);
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
				HashMap<Integer, User> users = Users.getUsers();
				Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
				
				for(int key : keys){
					
					User usr = users.get(key);
					%>
					<H2><%= usr.getName() %><a
						class="button" onclick="submit_edit_user('<%= usr.getName() %>')">[edit]</a><%
							
							if(!usr.getName().equals("sudo")){
								
								%><a class="button" onclick="submit_remove_user('<%= usr.getName() %>')">[remove]</a><%
								
							}
						%></H2>
					<div class="block">
						<H2>ACCOUNT :</H2>
						username : <strong><%= usr.getName() %></strong><br>
						password : <strong><%= Users.getPassword(usr.getName()) %></strong><br>
						group : <strong><%= usr.getPermissionsGroup() %></strong>
					</div>
					<%
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
				<H1>EDIT USER <%= user.getName().toUpperCase() %> </H1>
				<strong>Name</strong><br>
				<input type="text" id="username" value="<%= user.getName() %>"/><br>
				<strong>Password</strong><br>
				<input type="password" id="password" value="<%= Users.getPassword(user.getName()) %>"/>
				<button id="trigger_password" onclick="trigger_show_password()">show</button><br>
				<strong>Group</strong><br>
				<input type="text" id="group" value="<%= user.getPermissionsGroup() %>"/><br>
				<button id="submit" onclick="submit_json_user('edit')">OK</button>
				<input type="hidden" id="oldUser" value="<%= request.getParameter("user") %>"/>
				<div style="color:red" id="errors"></div>
				<%
			}
			
		}else if(action.equals("users.new")){
			%>
			<H1>NEW USER</H1>
			<strong>Name</strong><br>
			<input type="text" id="username"/><br>
			<strong>Password</strong><br>
			<input type="password" id="password"/>
			<button id="trigger_password" onclick="trigger_show_password()">show</button><br>
			<strong>Group</strong><br>
			<input type="text" id="group"/><br>
			<button id="submit" onclick="submit_json_user('new')">OK</button><br>
			<div style="color:red" id="errors"></div>
			<%
		}else{
			
			out.print("<div class=\"error\">There is any action named " + action + " !</div>");
			
		}
		%>
	</ADS:ifPerm>
	<%
}
%>