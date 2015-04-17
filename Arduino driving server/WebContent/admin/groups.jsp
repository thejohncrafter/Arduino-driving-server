<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<%@ page isELIgnored="false"%>
<%@ page import="com.ArduinoDrivingServer.web.users.Permissions"%>
<%@ page import="com.ArduinoDrivingServer.web.users.Users"%>
<%@ page import="com.ArduinoDrivingServer.web.beans.User"%>
<%@ page import="java.util.HashMap"%>
<ADS:ifPerm permission="userlist" minValue="ALL" invert="true">
	<ADS:forbiddenMsg message="You are not allowed to consult this page !" />
</ADS:ifPerm>
<ADS:ifPerm permission="groups" minValue="ALL">
	<script>
		var toDelete;
		
		function submit_goto_groups(){
			
			post('ADS', 'post', {file:'admin/groups.jsp'});
			
		}
		
		function submit_edit_group(group){
			
			post('ADS', 'post', {file:'admin/groups.jsp', edit:'', action:'group.edit', group:group});
			
		}
		
		function submit_new_group(){
			
			post('ADS', 'post', {file:'admin/groups.jsp', edit:'', action:'groups.new'});
			
		}
		
		function submit_edit_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:username});
			
		}
		
		function setGroupToDelete(name){
			
			document.getElementById('usersGroup_name').innerHTML = name;
			toDelete = name;
			
		}
		
		function submit_remove_group(){
			
			ADS.removeGroup(toDelete,function(answ,err){
				
				if(err){
					
					document.getElementById('errors').innerHTML = err;
					document.getElementById('errors').style.display = 'block';
					
				}else
					submit_goto_groups();
				
			});
			
		}
	</script>
</ADS:ifPerm>
<%
	if(request.getParameter("edit") == null){
%>
<ADS:ifPerm permission="groups" minValue="READ" invert="true">
	<ADS:forbiddenMsg message="You are not allowed to consult this page." />
</ADS:ifPerm>
<ADS:ifPerm permission="groups" minValue="READ">
	<div class="panel panel-default">
		<h1 class="panel title">
			Users groups
			<ADS:ifPerm permission="groups" minValue="ALL">
				<button class="btn btn-default" onclick="submit_new_group()"
					title="Create a new group.">
					<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
				</button>
			</ADS:ifPerm>
		</h1>
		<div class="panel-body">
			<div class="row">
				<div class="col-md-3">
					<div class="panel panel-default">
						<div class="panel-heading panel-title">sudo</div>
						<div class="panel-body">
							<div class="bs-callout bs-callout-primary">
								<H4>Permissions</H4>
								This group has all permissions.
							</div>
							<div class="bs-callout bs-callout-primary">
								<H4>Users in this group</H4>
								<%
									{
																																								
																																								User[] group =  Users.getInstance().getUsersByGroup("sudo");
																																								
																																								for(User user : group){
								%>
								<ADS:ifPerm permission="groups" minValue="ALL">
									<a onclick="submit_edit_user('<%=user.getName()%>')"><%=user.getName()%></a>
									<br>
								</ADS:ifPerm>
								<ADS:ifPerm permission="groups" minValue="ALL" invert="true">
									<%=user.getName()%><br>
								</ADS:ifPerm>
								<%
									}
																																								
																																								if(group.length == 0)
																																									out.print("Any user in this group.");
																																								
																																							}
								%>
							</div>
						</div>
					</div>
				</div>
				<%
					String[] groups = Permissions.getInstance().getGroupNames().values().toArray(new String[Permissions.getInstance().getGroupNames().size()]);
															
															for(String name : groups){
				%>
				<div class="col-md-3">
					<div class="panel panel-default">
						<div class="panel-heading panel-title">
							<ADS:ifPerm permission="bridge" minValue="ALL">
								<a class="btn btn-default"
									onclick="submit_edit_group('<%=name%>')"
									title="Edit this group"> <span
									class="glyphicon glyphicon-edit" aria-hidden="true"></span>
								</a>
								<button onclick="setGroupToDelete('<%=name%>')" type="button"
									class="btn btn-danger" data-toggle="modal"
									data-target="#modal_remove_group"
									title="Remove this users group.">
									<span class="glyphicon glyphicon-remove-sign"
										aria-hidden="true"></span>
								</button>
							</ADS:ifPerm>
							<%=name%>
						</div>
						<div class="panel-body">
							<div class="bs-callout bs-callout-primary">
								<H4>Permissions</H4>
								<%
									HashMap<String, Integer> perms = Permissions.getInstance().getGroup(name);
																																								String[] keys = perms.keySet().toArray(new String[perms.size()]);
																																								
																																								for(String key : keys){
																																									
																																									String val = Permissions.getInstance().getPermissionAsString(perms.get(key));
								%>
								<%=key%>
								: <strong><%=val%></strong><br>
								<%
									}
								%>
							</div>
							<div class="bs-callout bs-callout-primary">
								<H4>Users in this group</H4>
								<%
									User[] group =  Users.getInstance().getUsersByGroup(name);
																																								
																																								for(User user : group){
								%>
								<%=user.getName()%><br>
								<%
									}
																																								
																																								if(group.length == 0)
																																									out.print("Any user in this group.");
								%>
							</div>
						</div>
					</div>
				</div>
				<%
					}
				%>
			</div>
			<div class="bs-callout bs-callout-danger" id="errors"
				style="display: none"></div>
		</div>
	</div>
	<div class="modal fade" id="modal_remove_group" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">Confirm</h4>
				</div>
				<div class="modal-body">
					Are you sure you want to delete users group <strong><span
						id="usersGroup_name"></span></strong> ?
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						onclick="submit_remove_group()">Yes</button>
				</div>
			</div>
		</div>
	</div>
</ADS:ifPerm>
<%
	}else{
%>
<ADS:ifPerm permission="groups" minValue="ALL" invert="true">
	<ADS:forbiddenMsg message="You are not allowed to edit groups." />
</ADS:ifPerm>
<ADS:ifPerm permission="groups" minValue="ALL">
	<%
		String action = request.getParameter("action");
			
			if(action == null){
	%>
	<div class="error">Any action given !</div>
	<br>
	<a href="ADS">Back to home</a>
	<%
		}else if(action.equals("group.edit")){
		
		String group = request.getParameter("group");
		
		if(group == null){
	%>
	<div class="alert alert-danger" role="alert">
		<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
		<span class="sr-only">Error:</span> Any group given !
	</div>
	<%
		}else if(Permissions.getInstance().getGroup(group) == null){
	%>
	<div class="alert alert-danger" role="alert">
		<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
		<span class="sr-only">Error:</span> No such group :
		<%=group%>
		!
	</div>
	<%
		}else{
			
			HashMap<String, Integer> perms = Permissions.getInstance().getGroup(group);
			String[] keys = Permissions.getInstance().getPermissionNames();
	%>
	<script>
					function submit_json_group(){
						
						var name = document.getElementById('name').value;
						var group = {name:name,oldName:'<%=group%>', perms:{}};
						<%for(String key : keys){%>
							group.perms.<%=key%> = document.getElementById('perm_<%=key%>').value;
							<%}%>
						ADS.editGroup(group, function(answ, err){
							
							if(err){
								
								document.getElementById('errors').innerHTML = err;
								
							}else{
								
								submit_edit_group(name);
								
							}
							
						});
					}
				</script>
	<div class="panel panel-default">
		<h1 class="panel title">
			Edit users group
			<%=group%></h1>
		<div class="panel-body">
			<div class="form-group">
				<label for="name">Name</label> <input class="form-control"
					type="text" id="name" placeholder="Group name" value="<%=group%>" />
			</div>
			<label>Permissions</label>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
				<%
					for(String key : keys){
				%>
				<tr>
					<td><%=key%></td>
					<td><input class="form-control" id="perm_<%=key%>" type="text"
						placeholder="NONE, READ or ALL"
						value="<%=Permissions.getInstance().getPermissionAsString(perms.get(key))%>" /></td>
				</tr>
				<%
					}
				%>
			</table>
			<button type="button" class="btn btn-lg btn-primary btn-block"
				id="send_btn" onclick="submit_json_group()">OK</button>
			<span id="load"></span>
			<div class="bs-callout bs-callout-danger" id="errors"
				style="display: none"></div>
		</div>
	</div>
	<%
		}
		
			}else if(action.equals("groups.new")){
		
		String[] keys = Permissions.getInstance().getPermissionNames();
	%>
	<script>
				function submit_json_group(){
					
					var name = document.getElementById('name').value;
					var group = {name:name,perms:{}};
					<%for(String key : keys){%>
						group.perms.<%=key%> = document.getElementById('perm_<%=key%>
		').value;
	<%}%>
		ADS.newGroup(group, function(answ, err) {

				if (err) {

					document.getElementById('errors').innerHTML = err;
					document.getElementById('errors').style.display = 'block';

				} else {

					submit_edit_group(name);

				}

			});
		}
	</script>
	<div class="panel panel-default">
		<h1 class="panel title">New users group</h1>
		<div class="panel-body">
			<div class="form-group">
				<label for="name">Name</label> <input class="form-control"
					type="text" id="name" placeholder="Group name" />
			</div>
			<label>Permissions</label>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
				<%
					for (String key : keys) {
				%>
				<tr>
					<td><%=key%></td>
					<td><input class="form-control" id="perm_<%=key%>" type="text"
						placeholder="NONE, READ or ALL" /></td>
				</tr>
				<%
					}
				%>
			</table>
			<button type="button" class="btn btn-lg btn-primary btn-block"
				id="send_btn" onclick="submit_json_group()">OK</button>
			<span id="load"></span>
			<div class="bs-callout bs-callout-danger" id="errors"
				style="display: none"></div>
		</div>
	</div>
	<%
		} else {
	%>
	<div class="error">
		No such action :
		<%=action%></div>
	<br>
	<a href="ADS">Back to home</a>
	<%
		}
	%>
</ADS:ifPerm>
<%
	}
%>