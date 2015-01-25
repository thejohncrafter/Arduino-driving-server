/**
 * This method is used to post given parameters to given path with given method.
 * Got from http://stackoverflow.com/questions/133925/javascript-post-request-like-a-form-submit
 * Parameters :
 * path : the page to send to
 * method : the sending method (get, post,...)
 * parameters : The parameters - an object - (like {param1:"param1"}
 */
function post(path, method, params) {
	method = method || "post";
	
	var form = document.createElement("form");
	form.setAttribute("method", method);
	form.setAttribute("action", path);
	
	for(var key in params) {
		
		if(params.hasOwnProperty(key)) {
			
			var hiddenField = document.createElement("input");
			hiddenField.setAttribute("type", "hidden");
			hiddenField.setAttribute("name", key);
			hiddenField.setAttribute("value", params[key]);
			
			form.appendChild(hiddenField);
			
		}
		
	}
	
	document.body.appendChild(form);
	form.submit();
}