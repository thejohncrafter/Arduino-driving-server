package com.ArduinoDrivingServer.web.users;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This singleton contains methods related to users.
 * 
 * @author Julien Marquet
 *
 */
public class Users {

	/**
	 * Stores the instance of the class.
	 */
	private static Users instance;

	/**
	 * This field stores all the users.
	 */
	private HashMap<Integer, User> users = new HashMap<Integer, User>();

	/**
	 * This field stores all the passswords.
	 */
	private HashMap<Integer, String> passwords = new HashMap<Integer, String>();

	/**
	 * Used for making a singleton.<br>
	 * Loads all the users from the config file and stores it in
	 * <code>users</code>.
	 */
	private Users() {

		System.out.println("Loading all users form /WEB-INF/ADS-cfg.xml...");

		List<?> usersList = ArduinoDriving.getConfigElement("users")
				.getChildren();

		for (int i = 0; i < usersList.size(); i++) {

			Element userElem = (Element) usersList.get(i);
			String name = userElem.getAttributeValue("name");
			String group = userElem.getChild("permissions").getAttributeValue(
					"group");

			System.out.println("Creating user " + name + "...");

			// can't call newUser() because user's datas will be re-written.
			User u = new User();
			u.setName(name);
			u.setId(i);
			u.setPermissionsGroup(group);

			users.put(i, u);
			passwords.put(i, userElem.getAttributeValue("password"));

		}

		System.out.println("Done loading users.");

	}

	/**
	 * This method is used to add a new user.<br>
	 * It will throw nothing if the user can't be found.
	 * 
	 * @param name
	 *            The username.
	 * @param password
	 *            The password.
	 * @throws IOException
	 *             Should never happen.
	 * @throws JDOMException
	 *             Should never happen.
	 */
	public void newUser(String name, String password, String group)
			throws JDOMException, IOException {

		User u = new User();
		u.setName(name);
		u.setId(users.size());
		u.setPermissionsGroup(group);
		users.put(u.getId(), u);
		passwords.put(u.getId(), password);

		Element rootNode = ArduinoDriving.getConfigElement("users");

		Element usrElem = new Element("user");
		usrElem.setAttribute("name", name);
		usrElem.setAttribute("password", password);

		Element groupElem = new Element("permissions");
		groupElem.setAttribute("group", group);

		usrElem.addContent(groupElem);
		rootNode.addContent(usrElem);

		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		output.output(rootNode.getDocument(), new FileOutputStream(
				ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml")));

	}

	/**
	 * This method is used to change a username to another username.<br>
	 * It also automatically changes the "name" property in the user and the
	 * password access key.<br>
	 * It will throw nothing if the user can't be found.
	 * 
	 * @param oldUsername
	 *            The old username.
	 * @param newUsername
	 *            The new username.
	 * @throws IOException
	 *             Should never happen.
	 * @throws JDOMException
	 *             Should never happen.
	 */
	public void changeUsername(String oldUsername, String newUsername)
			throws JDOMException, IOException {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int index : keys) {

			if (users.get(index).getName().equals(oldUsername)) {

				users.get(index).setName(newUsername);

				List<Element> allUsers = ArduinoDriving.getConfigElement(
						"users").getChildren();

				for (int i = 0; i < allUsers.size(); i++) {

					Element e = allUsers.get(i);

					if (e.getAttributeValue("name").equals(oldUsername)) {

						e.setAttribute("name", newUsername);

					}

				}

				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(
						ArduinoDriving.getConfigElement("users").getDocument(),
						new FileOutputStream(ArduinoDriving
								.getRealPath("WEB-INF/ADS-cfg.xml")));

			}

		}

	}

	/**
	 * This method is used to remove a given user.<br>
	 * It will throw nothing if the user can't be found.
	 * 
	 * @param user
	 *            The name of the user to remove.
	 * @throws IOException
	 *             Should never happen.
	 * @throws JDOMException
	 *             Should never happen.
	 */
	public void deleteUser(String user) throws JDOMException, IOException {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int index : keys) {

			if (users.get(index).getName().equals(user)) {

				users.remove(index);
				passwords.remove(index);

				Element rootNode = ArduinoDriving.getConfigElement("users");
				List<Element> allUsers = ArduinoDriving.getConfigElement(
						"users").getChildren();

				for (int i = 0; i < allUsers.size(); i++) {

					Element e = allUsers.get(i);

					if (e.getAttributeValue("name").equals(user)) {

						rootNode.removeContent(e);

					}

				}

				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(rootNode.getDocument(), new FileOutputStream(
						ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml")));

			}

		}

	}

	/**
	 * This method is used to set the password of the user which as the given
	 * name.
	 * 
	 * @param username
	 *            The username.
	 * @param password
	 *            The password.
	 * @throws IOException
	 *             Should never happen.
	 * @throws JDOMException
	 *             Should never happen.
	 */
	public void setPassword(String user, String password) throws JDOMException,
			IOException {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int index : keys) {

			if (users.get(index).getName().equals(user)) {

				passwords.put(index, password);

				Element rootNode = ArduinoDriving.getConfigElement("users");
				List<Element> allUsers = rootNode.getChildren();

				for (int i = 0; i < allUsers.size(); i++) {

					Element e = allUsers.get(i);

					if (e.getAttributeValue("name").equals(user)) {

						e.setAttribute("password", password);

					}

				}

				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(rootNode.getDocument(), new FileOutputStream(
						ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml")));

			}

		}

	}

	/**
	 * This method is used to change a given user's group.
	 * 
	 * @param user
	 *            The user to edit.
	 * @param group
	 *            The new group.
	 * @throws IOException
	 *             Should never happen.
	 * @throws JDOMException
	 *             Should never happen.
	 */
	public void changeGroup(String user, String group) throws JDOMException,
			IOException {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int index : keys) {

			if (users.get(index).getName().equals(user)) {

				users.get(index).setPermissionsGroup(group);

				Element rootNode = ArduinoDriving.getConfigElement("users");
				List<Element> allUsers = rootNode.getChildren();

				for (int i = 0; i < allUsers.size(); i++) {

					Element e = allUsers.get(i);

					if (e.getAttributeValue("name").equals(user)) {

						e.getChild("permissions").setAttribute("group", group);

					}

				}

				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(rootNode.getDocument(), new FileOutputStream(
						ArduinoDriving.getRealPath("WEB-INF/users.xml")));

			}

		}

	}

	/**
	 * This method is used to get all the users contained by a given group.
	 * 
	 * @param gName
	 *            The name of the user group to get.
	 * @return The users contained by the group.
	 */
	public User[] getUsersByGroup(String gName) {

		ArrayList<User> group = new ArrayList<User>();
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int i : keys) {

			if (users.get(i).getPermissionsGroup().equals(gName))
				group.add(users.get(i));

		}

		return group.toArray(new User[group.size()]);

	}

	/**
	 * This method is used to get the user which as the given name.<br>
	 * It will return null if there is no such user.
	 * 
	 * @param username
	 *            The username.
	 * @return The user.
	 */
	public User getUser(String username) {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int i : keys) {

			if (users.get(i).getName().equals(username))
				return users.get(i);

		}

		return null;

	}

	/**
	 * This method is used to get the password of the user which as the given
	 * name.
	 * 
	 * @param username
	 *            The username.
	 * @return The password.
	 */
	public String getPassword(String username) {

		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);

		for (int i : keys) {

			if (users.get(i).getName().equals(username))
				return passwords.get(i);

		}

		return null;

	}

	/**
	 * This method is used to get the users list.
	 * 
	 * @return The users list.
	 * @see users
	 */
	public HashMap<Integer, User> getUsers() {
		return users;
	}

	/**
	 * Used to access the instance.
	 * 
	 * @return The instance (if it isn't null, else it will create and return
	 *         it).
	 */
	public static Users getInstance() {

		return instance != null ? instance : (instance = new Users());

	}

}
