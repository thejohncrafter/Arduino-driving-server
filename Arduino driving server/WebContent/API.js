
/**
 * This is the API object.
 */
var ADS = new ADS();

/**
 * Creates all the methods except for getHIDs().
 */
function ADS(){
	
	/**
	 * This function is used to create a XMLHttprequest.
	 */
	function createXHR(){
		
		var xhr = null;
	    
		if(window.XMLHttpRequest || window.ActiveXObject){
			
		    if(window.ActiveXObject){
		    	
		        try{
		        	
		            xhr = new ActiveXObject("Msxml2.XMLHTTP");
		            
		        }catch(e){
		        	
		            xhr = new ActiveXObject("Microsoft.XMLHTTP");
		            
		        }
		        
		    }else{
		    	
		        xhr = new XMLHttpRequest(); 
		        
		    }
		    
		}else{
			
			throw "The browser does not support XMLHttpRequestes.";
			
		}
		
		return xhr;
		
	}
	
	this.send = function(iface, datas, callback){
		
		var params = JSON.stringify(datas);
		var xhr = createXHR();
		xhr.open("POST", "remote", true);
		
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");
		
		xhr.onreadystatechange = function(evt){
			
			if(xhr.readyState == 4){
				
				if(xhr.status == 200 || xhr.status == 0){
					
					var answ = JSON.parse(xhr.responseText);
					
					if(answ.error){
						
						callback(null, answ.error);
						
					}else{
						
						callback(answ.answer, null);
						
					}
					
				}else{
					
					callback(null, "Page load error bad status");
					
				}
				
			}
			
		}
		
		xhr.send(params);
		
	}
	
	/**
	 * This function returns the hardware interface. It contains these fields & methods :
	 * HID = (field) hardware HID
	 * name = (field) hardware name
	 * creator = (field) hardware creator
	 * port = (field) hardware communication port name
	 */
	this.getHardwareDatas = function(name){
		
		var datas = ADS.getHIDs();
		name = name;
		
		for(var attr in datas){
			
			var data = datas[attr];
			
			if(data["HID"] == name){
				
				var toReturn = new Object();
				toReturn.HID = data["HID"];
				toReturn.name = data["name"];
				toReturn.creator = data["creator"];
				toReturn.port = data["port"];
				
				return data;
				
			}
			
		}
		
		throw "Can't find " + name + ' !';
		
	}
	
}
