<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<%@ page import="com.ArduinoDrivingServer.bridge.Bridge" %>
<%@ page import="com.ArduinoDrivingServer.bridge.AbstractBridgeInterface" %>
<%@ page import="com.ArduinoDrivingServer.bridge.AbstractBridge" %>
<ADS:ifPerm permission="bridge" minValue="READ" invert="true">
	<ADS:forbiddenMsg message="You're not allowed to see bridge properties."/>
</ADS:ifPerm>
<ADS:ifPerm permission="bridge" minValue="READ">
	<ADS:ifPerm permission="bridge" minValue="ALL">
		<script>
			function submit_edit_bridges(){
				
				post('ADS', 'post', {file:'admin/bridge.jsp', edit:'', action:'bridges.edit'});
				
			}
			
			function submit_edit_bridge(bridge){
				
				post('ADS', 'post', {file:'admin/bridge.jsp', edit:'', action:'bridge.edit', bridge:bridge});
				
			}
			
			function submit_json_bridge(type){
				
				if(type == 'edit'){
					
					var submit_btn = document.getElementById('submit_btn').value;
					var oldName = document.getElementById('old_name').value;
					var activated = document.getElementById('bridge_is_activated').checked;
					var name = document.getElementById('bridge_name').value;
					var desc = document.getElementById('bridge_desc').value;
					
					function callback(answ, err){
						
						submit_btn.disabled = false;
						
						if(answ)
							submit_edit_bridge(name);
						else
							document.getElementById('edit_bridge_error').innerHTML = err;
						
					}
					
					submit_btn.disabled = true;
					
					ADS.editBridge({activated:activated,name:name,desc:desc},oldName,callback);
					
				}
				
			}
		</script>
	</ADS:ifPerm>
	<%
	if(request.getParameter("edit") == null){
		%>
		<H1>BRIDGE PROPERTIES<ADS:ifPerm permission="bridge" minValue="ALL"><a class="button"
											onclick="submit_edit_bridges()">[edit]</a></ADS:ifPerm></H1>
		Opened : <strong><%= Bridge.isOpened() %></strong><br>
		Count of connected hardwares : <strong><%= Bridge.getIFaces().size()%></strong><br>
		Count of different bridges : <strong><%= Bridge.getBridges().size() %></strong>
		<%
		if(Bridge.isOpened()){
			%>
			<H2>Hardwares list<a class="button" id="showHide_hardwares_list" onclick="showHide('hardwares_list','showHide_hardwares_list')">[hide]</a></H2>
			<div id="hardwares_list" class="block">
				<%
				AbstractBridgeInterface[] ifaces = Bridge.getIFaces().values().toArray(
							new AbstractBridgeInterface[Bridge.getIFaces().size()]);
				
				for(AbstractBridgeInterface iface : ifaces){
					%>
					<H3><%= iface.getHID().hid %></H3>
					<div class="block">
						HID : <strong><%= iface.getHID().hid %></strong><br>
						Name : <strong><%= iface.getHID().name %></strong><br>
						Creator : <strong><%= iface.getHID().creator %></strong><br>
						Port : <strong><%= iface.getPortName() %></strong><br>
						Bridge type : <strong><%=iface.getBridgeName()%></strong><br>
					</div>
					<%
				}
				%>
			</div>
			<H2>Bridges list<a class="button" id="showHide_bridges_list" onclick="showHide('bridges_list','showHide_bridges_list')">[hide]</a></H2>
			<div id="bridges_list" class="block">
				<%
				AbstractBridge[] bridges = Bridge.getBridges().values().toArray(
							new AbstractBridge[Bridge.getBridges().size()]);
				
				for(AbstractBridge bridge : bridges){
					%>
					<H3><%= bridge.getName() %></H3>
					<div class="block">
						activated : <strong><%= bridge.isActivated() %></strong><br>
						name : <strong><%= bridge.getName() %></strong><br>
						description :<br>
						<div class="block"><%= bridge.getDesc() %></div>
					</div>
					<%
				}
				%>
			</div>
			<%
		}
		
	}else{
		if(request.getParameter("action") == null){
			%>
			<div class="error">Missing parameter "action" !</div>
			<%
		}else if(request.getParameter("action").equals("bridges.edit")){
			%>
			<script>
				function triggerBridgeState(){
					
					var triggerBtn = document.getElementById('bridge_open_triger');
					var logs = document.getElementById('bridge_trigger_logs');
					
					function callback(answ, err){
						
						if(answ)
							submit_edit_bridges();
						else
							document.getElementById('bridge_trigger_error').innerHTML = err;
						
					}
					
					if(triggerBtn.innerHTML == '[open]')
						ADS.changeBridgeState(true,callback);
					else
						ADS.changeBridgeState(false,callback);
					
				}
			</script>
			<ADS:ifPerm permission="bridge" minValue="ALL" invert="true">
				<ADS:forbiddenMsg message="You are not allowed to edit Bridge !"/>
			</ADS:ifPerm>
			<ADS:ifPerm permission="bridge" minValue="ALL">
				<H1>EDIT BRIDGE</H1>
				Opened : <strong><%= Bridge.isOpened() %></strong><a id="bridge_open_triger" onclick="triggerBridgeState();"
											class="button">[<% if(Bridge.isOpened()){out.print("close");}else{out.print("open");} %>]</a><br>
				<div id="bridge_trigger_error" class="error"></div>
				<%
				if(Bridge.isOpened()){
					%>
					<H2>Bridges list<a class="button" id="showHide_bridges_list" onclick="showHide('bridges_list','showHide_bridges_list')">[hide]</a></H2>
					<div id="bridges_list" class="block">
						<%
						AbstractBridge[] bridges = Bridge.getBridges().values().toArray(
									new AbstractBridge[Bridge.getBridges().size()]);
						
						for(AbstractBridge bridge : bridges){
							%>
							<H3><%= bridge.getName() %><a class="button" onclick="submit_edit_bridge('<%= bridge.getName() %>')">[edit]</a></H3>
							<div class="block">
							activated : <strong><%= bridge.isActivated() %></strong><br>
								name : <strong><%= bridge.getName() %></strong><br>
								description :<br>
								<div class="block"><%= bridge.getDesc() %></div>
							</div>
							<%
						}
						%>
					</div>
					<%
				}
				%>
			</ADS:ifPerm>
			<%
		}else if(request.getParameter("action").equals("bridge.edit")){
			
			if(request.getParameter("bridge") == null){
				%>
				<div class="error">Missing parameter "bridge"</div>
				<%
			}else if(Bridge.getBridges().get(request.getParameter("bridge")) == null){
				%>
				<div class="error">There is any bridge named <%= request.getParameter("bridge") %></div>
				<%
			}else{
				
				AbstractBridge bridge = Bridge.getBridges().get(request.getParameter("bridge"));
				%>
				<H1>EDIT BRIDGE <%= bridge.getName() %></H1>
				<input <% if(bridge.isActivated())out.print("checked"); %> type="checkbox" id="bridge_is_activated"/><strong>Activated</strong><br>
				<strong>Name</strong><br>
				<input type="text" id="bridge_name" value="<%= bridge.getName() %>"/><br>
				<strong>Description</strong><br>
				<textarea id="bridge_desc" rows="5" cols="20"><%= bridge.getDesc() %></textarea><br>
				<button id="submit_btn" onclick="submit_json_bridge('edit')">OK</button>
				<div style="display:none"><input id="old_name" value="<%= bridge.getName() %>"/></div>
				<div id="edit_bridge_error" class="error"></div>
				<%
			}
			
		}
		
	}
	%>
</ADS:ifPerm>