<%@ taglib uri="WEB-INF/ADS.tld" prefix="ADS"%>
<ADS:compress>
	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ page import="java.net.URLEncoder" %>
	<%@ page import="java.util.Enumeration" %>
	<%@ page import="java.io.File" %>
	<%@ page import="com.ArduinoDrivingServer.bridge.Bridge" %>
	<%@ page import="com.ArduinoDrivingServer.bridge.AbstractBridge" %>
	<%@ page import="com.ArduinoDrivingServer.web.JSPUtil" %>
	<%@ page import="com.ArduinoDrivingServer.web.beans.User" %>
	<%@ page import="com.ArduinoDrivingServer.web.servlets.ArduinoDriving" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
	    <head>
	        <title>Arduino driving server</title>
	        <link rel="stylesheet" type="text/css" href="style.css">
	        <script type="text/javascript" src="script.js"></script>
	        <script type="text/javascript" src="API.js"></script>
	    </head>
	    <body>
	    <script type="text/javascript">
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
	    </script>
	    	<div class="left">
	    		<div class="block">
	    			<ADS:isUserCo>
						<strong>You are connected as <ADS:username/></strong><br>
						<a href="javascript:submit_goto_user('<%=((User)session.getAttribute("user")).getName()%>')">My account</a><br>
						<a href="javascript:submit_disconnect()">disconnect</a>
					</ADS:isUserCo>
					<ADS:isUserCo invert="true">
						<form method="post" action="connect">
							<H4>CONNECTION</H4>
							<fieldset style="border:none;margin:0;padding:0;">
								<label for="username"><strong>Username</strong></label><br>
								<input type="text" id="username" name="username" value="" style="width:190px;"/>
								<br>
								<label for="username"><strong>Password</strong></label><br>
								<input type="password" id="password" name="password" style="width:190px;"/>
								<br>
								<input type="submit" value="connect" style="float:center;" />
							</fieldset>
						</form>
					</ADS:isUserCo>
	    		</div>
	    		<ADS:isUserCo>
		    		<div class="block">
			    		<%
			    			if(Bridge.getbridges().size() == 0){
			    		%>
			 				<span class="error">There is any hardware connected to the server&nbsp;!</span>
			 				<script type="text/javascript">
				 				ADS.prototype.getHIDs = function(){
				 					
				 					return {};
				 					
				 				}
			 				</script>
			 				<%
			 					}else{
			 				%>
			 				<H4>AVAILABLE DRIVERS&nbsp;:</H4>
			 				<ul style="margin-left:-20px;">
				 				<%
				 					AbstractBridge[] bridges = Bridge.getbridges().values().toArray(new AbstractBridge[Bridge.getbridges().size()]);
				 					
				    				for(AbstractBridge pb : bridges){
				    					
				    					String hid = pb.getHID().hid;
				    					out.print("<li><a href=\"javascript:submit_goto_driver('" + URLEncoder.encode(
				    														hid.replace(" ", "_"), "UTF-8") + "')\">");
				    					
				    					out.print(JSPUtil.maxLineLength(pb.getHID().hid, 20) + "</a></li>");
				    					
				    				}
				 				%>
			    				<script type="text/javascript">
			    					ADS.getHIDs = function(){
					    				<%out.print("return {");
					    				boolean first = true;
					    				
					    				for(AbstractBridge pb : bridges){
					    					
					    					if(!first){
					    						
					    						out.print(",");
					    						first = true;
					    						
					    					}
					    					
					    					out.print("'" + pb.getHID() + "':{");
					    					out.print("HID:'" + pb.getHID().hid + "'");
					    					out.print(",name:'" + pb.getHID().name + "'");
					    					out.print(",creator:'" + pb.getHID().creator + "'");
					    					out.print(",port:'" + pb.getPortName() + "'");
					    					out.print("}");
					    					
					    				}
					    				
					    				out.print("};");%>
			    					}
			    				</script>
		    				</ul>
			   				<%
			    			}
			    		%>
		    		</div>
	    		</ADS:isUserCo>
	    		<ADS:ifPerm permission="administration" minValue="READ">
					<div class="block">
						<H4>ADMINISTRATION</H4>
						<ADS:ifPerm permission="userList" minValue="READ"><a href="javascript:submit_goto_users()">USERS LIST</a></ADS:ifPerm>
					</div>
				</ADS:ifPerm>
	    	</div>
	    	<div class="right">
	    		<div class="topbar"><!-- This div is a bugfix. PLEASE DO NOT REMOVE IT ! --></div>
	    		<%
	    		String file = request.getParameter("file");
	    		
	    		if(session.getAttribute("user") != null && file != null && new File(ArduinoDriving.getRealPath(file)).exists()){
	    			%>
	    			<div style="padding-left:200px;"><!-- padding-left property is a bug-fix. PLEASE DO NOT REMOVE IT -->
	    				<jsp:include page="<%= file %>" flush="true" />
	    			</div>
	    			<%
	    		}else{
	    			%>
	    			<H1>Arduino driving server version 1.0.0</H1>
	    			Created by <strong>thejohncrafter</strong>.<br>
	    			<a href="http://thejohncrafter.github.io">website</a><br>
	    			<a href="https://github.com/thejohncrafter/Arduino-driving-server">Github</a><br>
	    			<%
	    		}
	    		%>
	    	</div>
	    </body>
	</html>
</ADS:compress>