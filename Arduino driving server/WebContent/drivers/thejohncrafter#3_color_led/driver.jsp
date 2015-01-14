<H1>3 COLOR LED DRIVER</H1>
<script>
	var iface = ADS.getHardwareDatas('thejohncrafter#3 color led');
	
	function send(){
		
		var r = document.getElementById('red_input').value;
		var g = document.getElementById('green_input').value;
		var b = document.getElementById('blue_input').value;
		
		if(r > 255 || r < 0 || g > 255 || g < 0 || b > 255 || b < 0){
			
			return;
			
		}
		
		document.getElementById('send_btn').disabled = true;
		
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
			
			if(err){
				
				console.log('Error !');
				document.getElementById("answerText").innerHTML += '<div style="color:red;">' + err + '<br></div>';
				
			}else{
				
				document.getElementById("answerText").innerHTML += answ + "<br>";
				
			}
			
			document.getElementById('send_btn').disabled = false;
			
		});
		
	}
	
	function preset(r, g, b){
		
		document.getElementById("red_input").value = r;
		document.getElementById("green_input").value = g;
		document.getElementById("blue_input").value = b;
		
	}
</script>
<H2>Colors :</H2>
Red : <input id="red_input" type="number" min="0" max="255" size="3" value="0"/><br>
Green : <input id="green_input" type="number" min="0" max="255" size="3" value="0"/><br>
Blue : <input id="blue_input" type="number" min="0" max="255" size="3" value="0"/><br>
<button id="send_btn" onclick="send()">SET</button><span id="load"></span>
<H3>Presets :</H3>
<button onclick="preset(255, 0, 0);">RED</button>
<button onclick="preset(0, 255, 0);">GREEN</button>
<button onclick="preset(0, 0, 255);">BLUE</button>
<button onclick="preset(255, 255, 255);">WHITE</button>
<button onclick="preset(0, 0, 0);">OFF</button>
<H2>Logs :</H2>
<div class="block" id="answerText"></div>