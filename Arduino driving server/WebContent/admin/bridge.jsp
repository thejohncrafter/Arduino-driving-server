<%@ taglib uri="../WEB-INF/ADS.tld" prefix="ADS"%>
<%@ page import="com.ArduinoDrivingServer.bridge.Bridge"%>
<%@ page
	import="com.ArduinoDrivingServer.bridge.AbstractBridgeInterface"%>
<%@ page import="com.ArduinoDrivingServer.bridge.AbstractBridge"%>
<ADS:ifPerm permission="bridge" minValue="READ" invert="true">
	<ADS:forbiddenMsg
		message="You're not allowed to see bridge properties." />
</ADS:ifPerm>
<ADS:ifPerm permission="bridge" minValue="READ">
	<ADS:ifPerm permission="bridge" minValue="ALL">
		<script>
			function submit_goto_bridge() {

				post('ADS', 'post', {
					file : 'admin/bridge.jsp'
				});

			}

			function submit_edit_bridge(bridge) {

				post('ADS', 'post', {
					file : 'admin/bridge.jsp',
					edit : '',
					action : 'bridge.edit',
					bridge : bridge
				});

			}

			function submit_json_bridge(type) {

				if (type == 'edit') {

					var submit_btn = document.getElementById('submit_btn').value;
					var oldName = document.getElementById('old_name').value;
					var activated = document
							.getElementById('bridge_is_activated').checked;
					var name = document.getElementById('bridge_name').value;
					var desc = document.getElementById('bridge_desc').value;

					function callback(answ, err) {

						submit_btn.disabled = false;

						if (answ)
							submit_edit_bridge(name);
						else
							document.getElementById('edit_bridge_error').innerHTML = err;

					}

					submit_btn.disabled = true;

					ADS.editBridge({
						activated : activated,
						name : name,
						desc : desc
					}, oldName, callback);

				}

			}

			function closeBridge() {

				ADS
						.changeBridgeState(
								false,
								function(answ, err) {

									if (err) {

										document.getElementById('errors').innerHtml = err;
										document.getElementById('errors').style.dicplay = 'block';

									} else {

										submit_goto_bridge();

									}

								});

			}

			function openBridge() {

				document.getElementById('please_wait_bar').style.display = 'block';

				ADS
						.changeBridgeState(
								true,
								function(answ, err) {

									if (err) {

										document.getElementById('errors').innerHtml = err;
										document.getElementById('errors').style.dicplay = 'block';

									} else {

										submit_goto_bridge();

									}

									document.getElementById('please_wait_bar').style.display = 'none';

								});

			}
		</script>
	</ADS:ifPerm>
	<%
		if(request.getParameter("edit") == null){
	%>
	<div class="panel panel-default">
		<h1 class="panel title">Bridge properties</h1>
		<div class="panel-body">
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-heading panel-title">Properties</div>
						<div class="panel-body">
							<%
								if(Bridge.getInstance().isOpened()){
							%>
							Bridge is opened.
							<button type="button" class="btn btn-default btn-xs"
								onclick="closeBridge()" title="Close the bridge.">Close</button>
							<br> Count of different bridges :
							<%=Bridge.getInstance().getBridges().size()%><br> Count of
							different hardwares :
							<%=Bridge.getInstance().getIFaces().size()%><br>
							<%
								}else{
							%>
							Bridge isn't opened.
							<button type="button" class="btn btn-default btn-xs"
								onclick="openBridge()" title="Open the bridge.">Open</button>
							<%
								}
							%>
							<div class="bs-callout bs-callout-danger" id="errors"
								style="display: none"></div>
							<div class="progress" id="please_wait_bar" style="display: none">
								<div class="progress-bar progress-bar-striped active"
									role="progressbar" style="width: 100%">Please wait...</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<%
				if(Bridge.getInstance().isOpened()){
			%>
			<div class="row">
				<div class="col-md-6">
					<div class="panel panel-default">
						<div class="panel-heading panel-title">Hardwares list</div>
						<div class="panel-body">
							<%
								AbstractBridgeInterface[] ifaces = Bridge.getInstance().getIFaces().values().toArray(
																	new AbstractBridgeInterface[Bridge.getInstance().getIFaces().size()]);
														
														for(AbstractBridgeInterface iface : ifaces){
							%>
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading panel-title"><%=iface.getHID().name%></div>
									<div class="panel-body">
										<strong>HID :</strong>
										<%=iface.getHID().hid%><br> <strong>Name :</strong>
										<%=iface.getHID().name%><br> <strong>Creator :</strong>
										<%=iface.getHID().creator%><br> <strong>Port :</strong>
										<%=iface.getPortName()%><br> <strong>Bridge
											type :</strong>
										<%=iface.getBridgeName()%><br>
									</div>
								</div>
							</div>
							<%
								}
							%>
						</div>
					</div>
				</div>
				<div class="col-md-6">
					<div class="panel panel-default">
						<div class="panel-heading panel-title">Bridges list</div>
						<div class="panel-body">
							<%
								AbstractBridge[] bridges = Bridge.getInstance().getBridges().values().toArray(
																	new AbstractBridge[Bridge.getInstance().getBridges().size()]);
														
														for(AbstractBridge bridge : bridges){
							%>
							<div class="col-md-6">
								<div class="panel panel-default">
									<div class="panel-heading panel-title">
										<ADS:ifPerm permission="bridge" minValue="ALL">
											<a class="btn btn-default"
												onclick="submit_edit_bridge('<%=bridge.getId()%>')"
												title="Edit this bridge"> <span
												class="glyphicon glyphicon-edit" aria-hidden="true"></span>
											</a>
										</ADS:ifPerm>
										<%=bridge.getName()%>
									</div>
									<div class="panel-body">
										<strong>activated :</strong>
										<%=bridge.isActivated()%><br> <strong>name :</strong>
										<%=bridge.getName()%><br> <strong>description
											:</strong><br>
										<div class="block"><%=bridge.getDesc()%></div>
									</div>
								</div>
							</div>
							<%
								}
							%>
						</div>
					</div>
				</div>
			</div>
			<%
				}
			%>
		</div>
	</div>
	<%
		}else{
	%>
	<ADS:ifPerm permission="bridge" minValue="ALL" invert="true">
		<div class="alert alert-danger" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Error:</span> You aren't allowed to edit Bridge
			!
		</div>
	</ADS:ifPerm>
	<ADS:ifPerm permission="bridge" minValue="ALL">
		<%
			if(request.getParameter("action") == null){
		%>
		<div class="alert alert-danger" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Error:</span> Missing "action" parameter !
		</div>
		<%
			}else if(request.getParameter("action").equals("bridges.edit")){
		%>
		<script>
			function triggerBridgeState() {

				var triggerBtn = document.getElementById('bridge_open_triger');
				var logs = document.getElementById('bridge_trigger_logs');

				function callback(answ, err) {

					if (answ)
						submit_edit_bridges();
					else
						document.getElementById('bridge_trigger_error').innerHTML = err;

				}

				if (triggerBtn.innerHTML == '[open]')
					ADS.changeBridgeState(true, callback);
				else
					ADS.changeBridgeState(false, callback);

			}
		</script>
		<ADS:ifPerm permission="bridge" minValue="ALL" invert="true">
			<ADS:forbiddenMsg message="You are not allowed to edit Bridge !" />
		</ADS:ifPerm>
		<ADS:ifPerm permission="bridge" minValue="ALL">
			<H1>EDIT BRIDGE</H1>
					Opened : <strong><%=Bridge.getInstance().isOpened()%></strong>
			<a id="bridge_open_triger" onclick="triggerBridgeState();"
				class="button">[<%
				if (Bridge.getInstance().isOpened()) {
										out.print("close");
									} else {
										out.print("open");
									}
			%>]
			</a>
			<br>
			<div id="bridge_trigger_error" class="error"></div>
			<%
				if (Bridge.getInstance().isOpened()) {
			%>
			<H2>
				Bridges list<a class="button" id="showHide_bridges_list"
					onclick="showHide('bridges_list','showHide_bridges_list')">[hide]</a>
			</H2>
			<div id="bridges_list" class="block">
				<%
					AbstractBridge[] bridges = Bridge
													.getInstance()
													.getBridges()
													.values()
													.toArray(
															new AbstractBridge[Bridge
																	.getInstance()
																	.getBridges()
																	.size()]);

											for (AbstractBridge bridge : bridges) {
				%>
				<H3><%=bridge.getName()%><a class="button"
						onclick="submit_edit_bridge('<%=bridge.getName()%>')">[edit]</a>
				</H3>
				<div class="block">
					activated : <strong><%=bridge.isActivated()%></strong><br>
					name : <strong><%=bridge.getName()%></strong><br>
					description :<br>
					<div class="block"><%=bridge.getDesc()%></div>
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
			} else if (request.getParameter("action").equals(
								"bridge.edit")) {

							if (request.getParameter("bridge") == null) {
		%>
		<div class="alert alert-danger" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Error:</span> Missing parameter "bridge"
		</div>
		<%
			} else if (Bridge.getInstance().getBridges()
									.get(request.getParameter("bridge")) == null) {
		%>
		<div class="alert alert-danger" role="alert">
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			<span class="sr-only">Error:</span> There is any bridge named
			<%=request.getParameter("bridge")%>
		</div>
		<%
			} else {

								AbstractBridge bridge = Bridge.getInstance()
										.getBridges()
										.get(request.getParameter("bridge"));
		%>
		<div class="panel panel-default">
			<h1 class="panel title">
				Edit bridge
				<%=bridge.getName()%></h1>
			<div class="panel-body">
				<div class="form-group">
					<label for="bridge_name">Name</label> <input type="text"
						class="form-control" placeholder="Name" id="bridge_name"
						aria-describedby="basic-addon2" value="<%=bridge.getName()%>" />
				</div>
				<div class="form-group">
					<label for="bridge_desc">Description</label>
					<textarea id="bridge_desc" class="form-control"
						placeholder="Description" aria-describedby="basic-addon2"><%=bridge.getDesc()%></textarea>
				</div>
				<div class="checkbox">
					<label> <input
						<%if (bridge.isActivated())
									out.print("checked");%>
						type="checkbox" id="bridge_is_activated" />Activated
					</label>
				</div>
				<input hidden id="old_name" value="<%=bridge.getName()%>" />
				<button id="submit_btn" onclick="submit_json_bridge('edit')"
					class="btn btn-lg btn-primary btn-block">OK</button>
			</div>
		</div>
		<%
			}
						}
		%>
	</ADS:ifPerm>
	<%
		}
	%>
</ADS:ifPerm>