/**
 * This method is used to post given parameters to given path with given method.<br>
 * Got from
 * http://stackoverflow.com/questions/133925/javascript-post-request-like-a-form-submit
 * 
 * @param path
 *            {String} the page to send to
 * @param method
 *            {String} the sending method (get, post,...)
 * @param params
 *            {Object} The parameters (like {param1:"param1"})
 */
function post(path, method, params) {
	method = method || "post";

	var form = document.createElement("form");
	form.setAttribute("method", method);
	form.setAttribute("action", path);

	for ( var key in params) {

		if (params.hasOwnProperty(key)) {

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