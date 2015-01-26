<script>
	var iface = ADS.getHardwareDatas('thejohncrafter#3 color led');
	var firstTime = true;
	
	function send(){
		
		var r = document.getElementById('red_input').value;
		var g = document.getElementById('green_input').value;
		var b = document.getElementById('blue_input').value;
		
		if(r > 255 || r < 0 || g > 255 || g < 0 || b > 255 || b < 0){
			
			return;
			
		}
		
		document.getElementById('send_btn').disabled = true;
		document.getElementById('handling').style.display = 'block';
		document.getElementById('waiting').style.display = 'none';
		
		if(r < 10)
			r = '00' + r;
		else if(r < 100)
			r = '0' + r;
		
		if(g < 10)
			g = '00' + g;
		else if(g < 100)
			g = '0' + g;
		
		if(b < 10)
			b = '00' + b;
		else if(r < 100)
			b = '0' + b;
		
		var data = 'SET ' + r + ' ' + g + ' ' + b;
		
		ADS.send(iface, {action:'SEND', port:iface.port, data:data, wait:true}, function(answ, err){
			
			if(firstTime){
				
				firstTime = false;
				document.getElementById("answerText").innerHTML = '';
				
			}
			
			if(err){
				
				console.log('Error !');
				document.getElementById("answerText").innerHTML += '<div style="color:red;">' + err + '<br></div>';
				
			}else{
				
				document.getElementById("answerText").innerHTML += answ + "<br>";
				
			}
			
			document.getElementById('send_btn').disabled = false;
			document.getElementById('handling').style.display = 'none';
			document.getElementById('waiting').style.display = 'block';
			
		});
		
	}
	
	function preset(r, g, b){
		
		document.getElementById("red_input").value = r;
		document.getElementById("green_input").value = g;
		document.getElementById("blue_input").value = b;
		
	}
	
	function clearLogs(){
		
		firstTime = true;
		document.getElementById("answerText").innerHTML = 'Logs cleared.';
		
	}
</script>

<div class="panel panel-default">
	<h1 class="panel title">3 color led driver</h1>
	<div class="panel-body">
		<div class="row">
			<div class="col-md-4 panel panel-default">
				<div class="panel-body">
					<H2>Colors :</H2>
					<p class="hidden-xs bs-callout bs-callout-info">
						Select the color you want than press on the "OK" button !
					</p>
					<div class="form-group">
						<label for="bridge_desc">Red</label>
						<input class="form-control" id="red_input" type="number" min="0" max="255" size="3" value="0"/>
					</div>
					<div class="form-group">
						<label for="bridge_desc">Green</label>
						<input class="form-control" id="green_input" type="number" min="0" max="255" size="3" value="0"/>
					</div>
					<div class="form-group">
						<label for="bridge_desc">Blue</label>
						<input class="form-control" id="blue_input" type="number" min="0" max="255" size="3" value="0"/>
					</div>
					<button type="button" class="btn btn-lg btn-primary btn-block"
						id="send_btn" onclick="send()">OK</button><span id="load"></span>
				</div>
			</div>
			<div class="col-md-8">
				<div class="row">
					<div class="col-md-6 panel panel-default">
						<div class="panel-body">
							<H2>Presets</H2>
							<p class="hidden-xs bs-callout bs-callout-info">
								Here are presets for the color. Choose one of these then press on "OK" !
							</p>
							<div class="btn-group" role="group" aria-label="Presets">
								<button type="button" class="btn btn-default" onclick="preset(255, 0, 0);">RED</button>
								<button type="button" class="btn btn-default" onclick="preset(0, 255, 0);">GREEN</button>
								<button type="button" class="btn btn-default" onclick="preset(0, 0, 255);">BLUE</button>
								<button type="button" class="btn btn-default" onclick="preset(255, 255, 255);">WHITE</button>
								<button type="button" class="btn btn-default" onclick="preset(0, 0, 0);">OFF</button>
							</div>
						</div>
					</div>
					<div class="col-md-6 panel panel-default">
						<div class="panel-body">
							<H2>State</H2>
							<p class="hidden-xs bs-callout bs-callout-info">
								Here is the state of the hardware.
							</p>
							<div id="handling" style="display:none">
								<div class="progress">
									<div class="progress-bar progress-bar-striped active" role="progressbar" style="width:100%">Please wait...</div>
								</div>
								Handling request...
							</div>
							<div id="waiting">
								Waiting for request.
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="hidden-xs col-md-12 panel panel-default">
						<div class="panel-body">
							This is an example driver created for the hardware "3 color led" (HID thejohncrafter#3 color led).<br>
							<a type="button" class="btn btn-primary" href="drivers/thejohncrafter%233_color_led/src.ino">Download source code</a><br>
							Note : commentaries are in French.
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="bs-callout bs-callout-info hidden-xs">
			<H4>Logs : <a class="btn btn-default" onclick="clearLogs()" title="Clear logs">
					<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>
				</a>
			</H4>
			<div class="block" id="answerText">
				Here will be hardware's logs.
			</div>
		</div>
	</div>
</div>