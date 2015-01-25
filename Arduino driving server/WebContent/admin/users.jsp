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
		var toDelete;
		
		function submit_goto_users(){
			
			post('ADS', 'post', {file:'admin/users.jsp'});
			
		}
		
		function submit_edit_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:username});
			
		}
		
		function submit_new_user(){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.new'});
			
		}
		
		function setUserToDelete(name){
			
			toDelete = name;
			document.getElementById('user_name').innerHTML = name;
			
		}
		
		function submit_remove_user(){
			
			ADS.removeUser(toDelete,function(answ,err){
				
				if(err){
					
					document.getElementById('errors').innerHTML = err;
					document.getElementById('errors').style.display = 'block';
					
				}else
					submit_goto_users();
				
			});
			
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
		<div class="panel panel-default">
			<h1 class="panel title">
				Users
				<ADS:ifPerm permission="userlist" minValue="READ">
					<button class="btn btn-default" onclick="submit_new_user()" title="Create a new group.">
						<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
					</button>
				</ADS:ifPerm>
			</h1>
			<div class="panel-body">
				<div class="row">
					<%
					HashMap<Integer, User> users = Users.getUsers();
					Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
					
					for(int key : keys){
						
						User user = users.get(key);
						%>
						<div class="col-md-3">
							<div class="panel panel-default">
								<div class="panel-heading panel-title">
									<ADS:ifPerm permission="bridge" minValue="ALL">
										<a class="btn btn-default" onclick="submit_edit_user('<%= user.getName() %>')"
											title="Edit this group">
											<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
										</a>
										<%
										if(!user.getName().equals("sudo")){
											%>
											<button onclick="setUserToDelete('<%= user.getName() %>')" type="button"
												class="btn btn-danger" data-toggle="modal"
												data-target="#modal_remove_user" title="Remove this users group.">
												<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>
											</button>
											<%
										}
										%>
									</ADS:ifPerm>
									<%= user.getName() %>
								</div>
								<div class="panel-body">
									<strong>Name</strong> : <%= user.getName() %><br>
									<strong>Password</strong> : <%= Users.getPassword(user.getName()) %><br>
									<strong>Group</strong> : <%= user.getPermissionsGroup() %>
								</div>
							</div>
						</div>
						<%
					}
					%>
				</div>
				<div class="bs-callout bs-callout-danger" id="errors" style="display:none"></div>
			</div>
		</div>
		<div class="modal fade" id="modal_remove_user" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">Confirm</h4>
					</div>
					<div class="modal-body">
						Are you sure you want to delete user <strong><span id="user_name"></span></strong> ?
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
						<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="submit_remove_user()">Yes</button>
					</div>
				</div>
			</div>
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
				
				var errors = document.getElementById('errors');
				var name = document.getElementById('username').value;
				var password = document.getElementById('password').value;
				var group = document.getElementById('group').value;
				
				function callback(answ, err){
					
					if(answ){
						
						submit_edit_user(name);
						
					}else{
						
						errors.style.display = 'block';
						errors.innerHTML = err;
						
					}
					
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
		</script>
		<%
		User user = Users.getUser((String) request.getParameter("user"));
		
		String action = "users.edit";
		
		if(request.getParameter("action") != null)
			action = request.getParameter("action");
		
		if(action.equals("users.edit")){
			
			if(user== null){
				%>
				<H1>ERROR</H1>
				<span class="error">There is any user named <%= request.getParameter("user") %></span>
				error = true;
				<%
			}else{
				%>
				<div class="panel panel-default">
					<h1 class="panel title">New user</h1>
					<div class="panel-body">
						<div class="form-group">
							<label for="username">Name</label>
							<input class="form-control" type="text" id="username" placeholder="User name"
								value="<%= user.getName() %>"/>
						</div>
						<div class="form-group">
							<label for="password">Password</label>
							<input class="form-control" type="text" id="password" placeholder="Password"
								value="<%= Users.getPassword(user.getName()) %>"/>
						</div>
						<div class="form-group">
							<label for="group">Group</label>
							<input class="form-control" type="text" id="group" placeholder="Group name"
								value="<%= user.getPermissionsGroup() %>"/>
						</div>
						<input type="hidden" id="oldUser" value="<%= user.getName() %>"/>
						<button type="button" class="btn btn-lg btn-primary btn-block"
								id="send_btn" onclick="submit_json_user('edit')">OK</button>
						<div class="bs-callout bs-callout-danger" id="errors" style="display:none"></div>
					</div>
				</div>
				<%
			}
			
		}else if(action.equals("users.new")){
			%>
			<div class="panel panel-default">
				<h1 class="panel title">New user</h1>
				<div class="panel-body">
					<div class="form-group">
						<label for="username">Name</label>
						<input class="form-control" type="text" id="username" placeholder="User name"/>
					</div>
					<div class="form-group">
						<label for="password">Password</label>
						<input class="form-control" type="text" id="password" placeholder="Password"/>
					</div>
					<div class="form-group">
						<label for="group">Group</label>
						<input class="form-control" type="text" id="group" placeholder="Group name"/>
					</div>
					<button type="button" class="btn btn-lg btn-primary btn-block"
							id="send_btn" onclick="submit_json_user('new')">OK</button>
					<div class="bs-callout bs-callout-danger" id="errors" style="display:none"></div>
				</div>
			</div>
			<%
		}else{
			
			out.print("<div class=\"error\">There is any action named " + action + " !</div>");
			
		}
		%>
	</ADS:ifPerm>
	<%
}
%>