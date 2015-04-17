/**
 * This is the API object.
 */
var ADS = new ADS();

/**
 * Creates all the methods except for getHIDs().
 */
function ADS() {

	/**
	 * This field is used to know if a user is connected.
	 */
	this.userCo = false;

	/**
	 * This function is used to create a XMLHttprequest.
	 * 
	 * @param callback
	 *            {function} The callback function called when data is received.
	 */
	function createXHR(callback) {

		var xhr = null;

		if (window.XMLHttpRequest || window.ActiveXObject) {

			if (window.ActiveXObject) {

				try {

					xhr = new ActiveXObject("Msxml2.XMLHTTP");

				} catch (e) {

					xhr = new ActiveXObject("Microsoft.XMLHTTP");

				}

			} else {

				xhr = new XMLHttpRequest();

			}

		} else {

			throw "The browser does not support XMLHttpRequestes.";

		}

		if (callback) {

			xhr.onreadystatechange = function(evt) {

				if (xhr.readyState == 4) {

					if (xhr.status == 200 || xhr.status == 0) {

						var answ = JSON.parse(xhr.responseText);

						if (answ.error) {

							callback(null, answ.error);

						} else {

							callback(answ.answer, null);

						}

					} else {

						callback(null, "Page load error bad status");

					}

				}

			}

		}

		return xhr;

	}

	/**
	 * This function returns the hardware interface. It contains these fields &
	 * methods :<br>
	 * HID (field) hardware HID<br>
	 * name = (field) hardware name<br>
	 * creator = (field) hardware creator<br>
	 * port = (field) hardware communication port name<br>
	 */
	this.getHardwareDatas = function(name) {

		var datas = ADS.getHIDs();
		name = name;

		for ( var attr in datas) {

			var data = datas[attr];

			if (data["HID"] == name) {

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

	/**
	 * This function is used to send data to the remote servlet.
	 * 
	 * @param iface
	 *            {Object} The hardware interface to send to
	 * @param datas
	 *            {Object} the datas to send (like {port:"My port", datas:"My
	 *            datas"}
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.send = function(iface, datas, callback) {

		var params = JSON.stringify(datas);
		var xhr = createXHR(callback);

		xhr.open("POST", "remote", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This function is used to open the Bridge.
	 * 
	 * @param state
	 *            {boolean} a boolean. If true, the Bridge will be opened,
	 *            otherwise it will be closed.
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.changeBridgeState = function(state, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params;
		var xhr = createXHR(callback);

		if (state)
			params = JSON.stringify({
				user : {
					name : this.user.name,
					password : this.user.password
				},
				action : "bridge.open"
			});
		else
			params = JSON.stringify({
				user : {
					name : this.user.name,
					password : this.user.password
				},
				action : "bridge.close"
			});

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This function is used to edit a bridge.
	 * 
	 * @param bridge
	 *            {Object} an object like this :
	 *            {activated:true/false,name:'bridge name',desc:'bridge
	 *            description'}
	 * @param oldName
	 *            {String} the old name of the bridge
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.editBridge = function(bridge, oldName, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			bridge : bridge,
			oldName : oldName,
			action : "bridges.edit"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This function is used to create a new user.
	 * 
	 * @param user
	 *            {Object} an object like this :
	 *            {name:"name",password:"password",group:"group"}
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.newUser = function(user, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			newUser : user,
			action : "users.new"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This function is used to create a new user.
	 * 
	 * @param user
	 *            {Object} an object like this :
	 *            {name:"name",password:"password",group:"group"}
	 * @param oldUser
	 *            {String} the old username
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.editUser = function(user, oldUser, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			oldUser : oldUser,
			newUser : user,
			action : "users.edit"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This function is used to create a new user.
	 * 
	 * @param user
	 *            {String} the name of the user to remove
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.removeUser = function(user, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			toRemove : user,
			action : "users.remove"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This method is used to create a new group.
	 * 
	 * @param group
	 *            {Object} an object like this :
	 *            {name:"name",perms:{perm1:"ALL/READ/NONE",perm2:"ALL/READ/NONE",...}}
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.newGroup = function(group, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			group : group,
			action : "groups.new"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This method is used to edit a group.
	 * 
	 * @param group
	 *            {Object} an object like this :
	 *            {name:"name",perms:{perm1:"ALL/READ/NONE",perm2:"ALL/READ/NONE",...}}
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.editGroup = function(group, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			group : group,
			action : "groups.edit"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

	/**
	 * This method is used to remove a given group.
	 * 
	 * @param name
	 *            {String} The name of the group to delete.
	 * @param callback
	 *            {function} a function(answer, error) used as callback.
	 */
	this.removeGroup = function(name, callback) {

		if (!this.userCo) {

			callback(null, "No connected user !");
			return;

		}

		var params = JSON.stringify({
			user : {
				name : this.user.name,
				password : this.user.password
			},
			name : name,
			action : "groups.remove"
		});
		var xhr = createXHR(callback);

		xhr.open("POST", "edit", true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Content-length", params.length);
		xhr.setRequestHeader("Connection", "close");

		xhr.send(params);

	}

}
