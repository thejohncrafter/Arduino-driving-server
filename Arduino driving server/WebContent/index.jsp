<%@ taglib uri="WEB-INF/ADS.tld" prefix="ADS"%>
<ADS:compress>
	<%@ page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
	<%@ page import="java.net.URLEncoder"%>
	<%@ page import="java.util.Enumeration"%>
	<%@ page import="java.io.File"%>
	<%@ page import="com.ArduinoDrivingServer.bridge.Bridge"%>
	<%@ page
		import="com.ArduinoDrivingServer.bridge.AbstractBridgeInterface"%>
	<%@ page import="com.ArduinoDrivingServer.web.beans.User"%>
	<%@ page import="com.ArduinoDrivingServer.web.users.Users"%>
	<%@ page import="com.ArduinoDrivingServer.web.servlets.ArduinoDriving"%>
	<!DOCTYPE html>
	<html>
<head>
<title>Arduino driving server</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="bootstrap/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/callout.min.css" />
<script type="text/javascript" src="script.js"></script>
<script type="text/javascript" src="API.js"></script>
</head>
<body>
	<script type="text/javascript">
		    	function submit_goto_home(){
		    		
		    		post('ADS', 'post');
		    		
		    	}
		    	
		    	function submit_connect(){
		    		
		    		post('ADS', 'post', {file:'connect.jsp'});
		    		
		    	}
		    	
		    	function submit_goto_driver(hid){
		    		
		    		post('ADS', 'post', {file:'drivers/' + hid + '/driver.jsp'});
		    		
		    	}
		    	
		    	function submit_goto_user(username){
		    		
		    		post('ADS', 'post', {file:'admin/users.jsp', user:username});
		    		
		    	}
		    	
		    	function submit_disconnect(){
		    		
		    		post('connect', 'post', {act:'disconnect'});
		    		
		    	}
		    	
		    	function submit_goto_users(){
		    		
		    		post('ADS', 'post', {file:'admin/users.jsp'});
		    		
		    	}
		    	
		    	function submit_goto_bridge(){
		    		
		    		post('ADS', 'post', {file:'admin/bridge.jsp'});
		    		
		    	}
		    	
				function submit_goto_groups(){
		    		
		    		post('ADS', 'post', {file:'admin/groups.jsp'});
		    		
		    	}
		    </script>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" onclick="submit_goto_home()">Arduino
					driving server</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<ADS:isUserCo invert="true">
						<li><a onclick="submit_connect()">Connection</a></li>
					</ADS:isUserCo>
					<ADS:isUserCo>
						<script>
									ADS.userCo = true;
									ADS.user = {name:"<%=((User) session.getAttribute("user")).getName()%>", password:"<%=Users.getInstance().getPassword(((User) session.getAttribute("user")).getName())%>
							"
							};
						</script>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-expanded="false"><ADS:username /><span
								class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li><a
									onclick="submit_goto_user('<%=((User)session.getAttribute("user")).getName()%>')">My
										account</a></li>
								<li><a onclick="submit_disconnect()">disconnect</a></li>
							</ul></li>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-expanded="false">Bridge<span
								class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li><a onclick="submit_goto_bridge()">Edit</a></li>
								<li role="presentation" class="divider"></li>
								<%
									if(!Bridge.getInstance().isOpened()){
								%>
								<li><a onclick="submit_goto_bridge()">Bridge is closed.</a>
									<script type="text/javascript">
										ADS.getHIDs = function() {

											return {};

										}
									</script></li>
								<%
									}else if(Bridge.getInstance().getIFaces().size() == 0){
								%>
								<li><a onclick="submit_goto_bridge()">There is any
										hardware connected to the server.</a> <script
										type="text/javascript">
											ADS.getHIDs = function() {

												return {};

											}
										</script></li>
								<%
									}else{
								%>
								<li>
									<%
										AbstractBridgeInterface[] ifaces = Bridge.getInstance().getIFaces().values().toArray(
												new AbstractBridgeInterface[Bridge.getInstance().getIFaces().size()]);
										
					    				for(AbstractBridgeInterface iface : ifaces){
					    					
					    					String hid = iface.getHID().hid;
					    					out.print("<li><a onclick=\"submit_goto_driver('" + URLEncoder.encode(
					    														hid.replace(" ", "_"), "UTF-8") + "')\">");
					    					
					    					out.print(iface.getHID() + "</a></li>");
					    					
					    				}
									%> <script type="text/javascript">
														ADS.getHIDs = function() {
													<%out.print("return {");
									    				boolean first = true;
									    				
									    				for(AbstractBridgeInterface iface : ifaces){
									    					
									    					if(!first){
									    						
									    						out.print(",");
									    						first = true;
									    						
									    					}
									    					
									    					out.print("'" + iface.getHID() + "':{");
									    					out.print("HID:'" + iface.getHID().hid + "'");
									    					out.print(",name:'" + iface.getHID().name + "'");
									    					out.print(",creator:'" + iface.getHID().creator + "'");
									    					out.print(",port:'" + iface.getPortName() + "'");
									    					out.print(",type:'" + iface.getBridgeName() + "'");
									    					out.print("}");
									    					
									    				}
									    				
									    				out.print("};");%>
														}
													</script>
								</li>
								<%
									}
								%>
							</ul></li>
						<ADS:ifPerm permission="administration" minValue="READ">
							<li class="dropdown"><a href="#" class="dropdown-toggle"
								data-toggle="dropdown" role="button" aria-expanded="false">Administration<span
									class="caret"></span></a>
								<ul class="dropdown-menu" role="menu">
									<li><a onclick="submit_goto_users()">Users list</a></li>
									<li><a onclick="submit_goto_groups()">Users groups</a></li>
									<li><a onclick="submit_goto_bridge()">Bridge</a></li>
								</ul></li>
						</ADS:ifPerm>
					</ADS:isUserCo>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container">
		<%
			{
					String file = request.getParameter("file");

					if (file != null
							&& new File(ArduinoDriving.getRealPath(file)
									.replace("%23", "#")).exists()) {
						file = file.replace("%23", "#");
		%>
		<jsp:include page="<%=file%>" flush="true" />
		<%
			} else {
		%>
		<div class="panel panel-default">
			<h1 class="panel title">Arduino driving server version 1.0.2</h1>
			<div class="panel-body">
				<ADS:isUserCo>You are connected as <%=((User) session.getAttribute("user"))
									.getName()%>.</ADS:isUserCo>
				<ADS:isUserCo invert="true">You aren't connected.</ADS:isUserCo>
				<br> Created by thejohncrafter (Julien Marquet).<br> <a
					href="http://thejohncrafter.github.io">website</a><br> <a
					href="https://github.com/thejohncrafter/Arduino-driving-server">Github</a><br>
			</div>
		</div>
		<%
			}
				}
		%>
	</div>
	<script src="bootstrap/jquery.min.js"></script>
	<script src="bootstrap/bootstrap.min.js"></script>
</body>
	</html>
</ADS:compress>