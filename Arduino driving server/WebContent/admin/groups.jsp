<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<%@ page isELIgnored="false" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Permissions" %>
<%@ page import="com.ArduinoDrivingServer.web.users.Users" %>
<%@ page import="com.ArduinoDrivingServer.web.beans.User" %>
<%@ page import="java.util.HashMap" %>
<ADS:ifPerm permission="userlist" minValue="ALL" invert="true">
	<ADS:forbiddenMsg message="You are not allowed to consult this page !"/>
</ADS:ifPerm>
<ADS:ifPerm permission="groups" minValue="ALL">
	<script>
		function submit_edit_groups(){
			
			post('ADS', 'post', {file:'admin/groups.jsp', edit:'', action:'groups.edit'});
			
		}
		
		function submit_edit_group(group){
			
			post('ADS', 'post', {file:'admin/groups.jsp', edit:'', action:'group.edit', group:group});
			
		}
		
		function submit_new_group(group){
			
			post('ADS', 'post', {file:'admin/groups.jsp', edit:'', action:'groups.new', group:group});
			
		}
		
		function submit_edit_user(username){
			
			post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:username});
			
		}
		
		function submit_remove_group(name){
			
			var r = confirm(('Are you sure you want to remove ' + name + ' ?'));
			
			if(r == true){
				
				ADS.removeGroup(name,function(answ,err){
					
					if(err)
						document.getElementById('errors').innerHTML = err;
					else
						submit_edit_groups();
					
				});
			    
			}
			
		}
	</script>
</ADS:ifPerm>
<%
if(request.getParameter("edit") == null){
%>
	<ADS:ifPerm permission="groups" minValue="READ" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to consult this page."/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="groups" minValue="READ">
		<H1>USER GROUPS<ADS:ifPerm permission="groups" minValue="ALL"
			><a class="button" onclick="submit_edit_groups()">[edit]</a></ADS:ifPerm></H1>
		<H2>SUDO</H2>
		<div class="block" id="group_elem_sudo">
			<H3>Permissions</H3>
			<div class="block">
				This group has all permissions.
			</div>
			<H3>Users in this group</H3>
				<div class="block">
					<%
					{
						
						User[] group =  Users.getUsersByGroup("sudo");
						
						for(User user : group){
							%>
							<%= user.getName() %><br>
							<%
						}
						
						if(group.length == 0)
							out.print("Any user in this group.");
						
					}
					%>
				</div>
		</div>
		<%
		String[] groups = Permissions.getGroupNames().values().toArray(new String[Permissions.getGroupNames().size()]);
		
		for(String name : groups){
			%>
			<H2><%= name.toUpperCase() %><a class="button" id="showHide_<%= name %>"
				onclick="showHide('group_elem_<%= name %>','showHide_<%= name %>')">[hide]</a></H2>
			<div class="block" id="group_elem_<%= name %>">
				<H3>Permissions</H3>
				<div class="block">
					<%
					HashMap<String, Integer> perms = Permissions.getGroup(name);
					String[] keys = perms.keySet().toArray(new String[perms.size()]);
					
					for(String key : keys){
						
						String val = "ALL";
						
						if(perms.get(key).equals(Permissions.NONE))
							val = "NONE";
						else if(perms.get(key).equals(Permissions.READ))
							val = "READ";
						%>
						<%= key %> : <strong><%= val %></strong><br>
						<%
					}
					%>
				</div>
				<H3>Users in this group</H3>
				<div class="block">
					<%
					User[] group =  Users.getUsersByGroup(name);
					
					for(User user : group){
						%>
						<%= user.getName() %><br>
						<%
					}
					
					if(group.length == 0)
						out.print("Any user in this group.");
					%>
				</div>
			</div>
			<%
		}
		%>
	</ADS:ifPerm>
<%
}else{
%>
	<ADS:ifPerm permission="groups" minValue="ALL" invert="true">
		<ADS:forbiddenMsg message="You are not allowed to edit groups."/>
	</ADS:ifPerm>
	<ADS:ifPerm permission="groups" minValue="ALL">
		<%
		String action = request.getParameter("action");
		
		if(action == null){
			%>
			<div class="error">Any action given !</div><br>
			<a href="ADS">Back to home</a>
			<%
		}else if(action.equals("groups.edit")){
			%>
			<H1>EDIT USER GROUPS</H1>
			<a onclick="submit_new_group()">New group</a>
			<H2>SUDO</H2>
			<div class="block" id="group_elem_sudo">
				<H3>Permissions</H3>
				<div class="block">
					This group has all permissions.
				</div>
				<H3>Users in this group</H3>
					<div class="block">
						<%
						{
							
							User[] group =  Users.getUsersByGroup("sudo");
							
							for(User user : group){
								%>
								<%= user.getName() %><a class="button" onclick="submit_edit_user('<%= user.getName() %>')">[edit]</a><br>
								<%
							}
							
							if(group.length == 0)
								out.print("Any user in this group.");
							
						}
						%>
					</div>
			</div>
			<%
			String[] groups = Permissions.getGroupNames().values().toArray(new String[Permissions.getGroupNames().size()]);
			
			for(String name : groups){
				%>
				<H2><%= name.toUpperCase() %><a class="button" onclick="submit_edit_group('<%= name %>')">[edit]</a
					><a class="button" onclick="submit_remove_group('<%= name %>')">[remove]</a
					><a class="button" id="showHide_<%= name %>"
					onclick="showHide('group_elem_<%= name %>','showHide_<%= name %>')">[hide]</a></H2>
				<div class="block" id="group_elem_<%= name %>">
					<H3>Permissions</H3>
					<div class="block" id="group_elem_<%= name %>">
						<%
						HashMap<String, Integer> perms = Permissions.getGroup(name);
						String[] keys = perms.keySet().toArray(new String[perms.size()]);
						
						for(String key : keys){
							
							String val = "ALL";
							
							if(perms.get(key).equals(Permissions.NONE))
								val = "NONE";
							else if(perms.get(key).equals(Permissions.READ))
								val = "READ";
							%>
							<%= key %> : <strong><%= val %></strong><br>
							<%
						}
						%>
					</div>
					<H3>Users in this group</H3>
					<div class="block">
						<%
						User[] group =  Users.getUsersByGroup(name);
						
						for(User user : group){
							%>
							<%= user.getName() %><a class="button" onclick="submit_edit_user('<%= user.getName() %>')">[edit]</a><br>
							<%
						}
						
						if(group.length == 0)
							out.print("Any user in this group.");
						%>
					</div>
				</div>
				<div class="error" id="errors"></div>
				<%
			}
		}else if(action.equals("group.edit")){
			
			String group = request.getParameter("group");
			
			if(group == null){
				%>
				<div class="error">Any group given !</div><br>
				<a href="ADS">Back to home</a>
				<%
			}else if(Permissions.getGroup(group) == null){
				%>
				<div class="error">No such group !</div><br>
				<a href="ADS">Back to home</a>
				<%
			}else{
				
				HashMap<String, Integer> perms = Permissions.getGroup(group);
				String[] keys = Permissions.getPermissionNames();
				%>
				<script>
					function submit_json_group(){
						
						var name = document.getElementById('name').value;
						var group = {name:name,oldName:'<%= group %>', perms:{}};
						<%
						for(String key : keys){
							%>
							group.perms.<%= key %> = document.getElementById('perm_<%= key %>').value;
							<%
						}
						%>
						ADS.editGroup(group, function(answ, err){
							
							if(err){
								
								document.getElementById('errors').innerHTML = err;
								
							}else{
								
								submit_edit_group(name);
								
							}
							
						});
					}
				</script>
				<H1>EDIT GROUP <%= group.toUpperCase() %></H1>
				name : <input type="text" id="name" value="<%= group %>" size="4"/>
				<H3>Permissions</H3>
					<div class="block">
						<%
						
						for(String key : keys){
							
							String val = Permissions.getPermissionAsString(perms.get(key));
							%>
							<strong><%= key %></strong> : <input type="text" id="perm_<%= key %>" value="<%= val %>" size="4"/><br>
							<%
						}
						%>
					</div>
					<H3>Users in this group</H3>
					<div class="block">
						<%
						User[] groupUsrs =  Users.getUsersByGroup(group);
						
						for(User user : groupUsrs){
							%>
							<%= user.getName() %><a class="button" onclick="submit_edit_user('<%= user.getName() %>')">[edit]</a><br>
							<%
						}
						
						if(groupUsrs.length == 0)
							out.print("Any user in this group.");
						%>
					</div>
					<button onclick="submit_json_group()">OK</button>
					<div class="error" id="errors"></div>
				<%
			}
			
		}else if(action.equals("groups.new")){
			
			String[] keys = Permissions.getPermissionNames();
			%>
			<script>
				function submit_json_group(){
					
					var name = document.getElementById('name').value;
					var group = {name:name,perms:{}};
					<%
					for(String key : keys){
						%>
						group.perms.<%= key %> = document.getElementById('perm_<%= key %>').value;
						<%
					}
					%>
					ADS.newGroup(group, function(answ, err){
						
						if(err){
							
							document.getElementById('errors').innerHTML = err;
							
						}else{
							
							submit_edit_group(name);
							
						}
						
					});
				}
			</script>
			<H1>EDIT GROUP</H1>
			name : <input type="text" id="name" size="4"/>
			<H3>Permissions</H3>
				<div class="block">
					<%
					
					for(String key : keys){
						
						%>
						<strong><%= key %></strong> : <input type="text" id="perm_<%= key %>" size="4"/><br>
						<%
					}
					%>
				</div>
				<button onclick="submit_json_group()">OK</button>
				<div class="error" id="errors"></div>
			<%
		}else{
			%>
			<div class="error">No such action : <%= action %></div><br>
			<a href="ADS">Back to home</a>
			<%
		}
		%>
	</ADS:ifPerm>
<%
}
%>