package com.ArduinoDrivingServer.web.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.plugins.Plugins;
import com.ArduinoDrivingServer.web.users.Permissions;
import com.ArduinoDrivingServer.web.users.Users;

/**
 * This servlet is the main servlet of the program.
 * 
 * @author Julien Marquet
 *
 */
public class ArduinoDriving extends HttpServlet {

	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 6370473352382504816L;

	/**
	 * Used by the <code>Servlet</code> system.
	 */
	private ServletConfig cfg;

	/**
	 * This is used to store the instance of the Servlet.
	 */
	private static ArduinoDriving instance;

	/**
	 * This field stores the configuration element (from ADS-cfg.xml).
	 */
	private static Element cfgElem;

	public void init(ServletConfig config) throws ServletException {

		cfg = config;

		instance = this;

		System.out.println("========================");
		System.out.println("|arduino driving server|");
		System.out.println("|by Julien Marquet     |");
		System.out.println("|created on            |");
		System.out.println("|20 october 2014       |");
		System.out.println("========================");

		System.out.println("Loading ADS-cfg.xml...");

		try {

			File usrFile = new File(
					ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml"));
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(usrFile);
			cfgElem = document.getRootElement();

		} catch (IOException | JDOMException e) {

			System.out.println("Can't load ADS-cfg.xml :");
			e.printStackTrace();
			System.out.println("Load of Arduino driving server canceled.");
			return;

		}

		System.out.println("Done.");
		System.out.println("Loading users...");

		Permissions.getInstance();

		try {

			Users.getInstance();

		} catch (Error e) {

			throw new ServletException("An error occured : ", e.getCause());

		}

		System.out.println("Done.");
		System.out.println("Loading plugins...");

		try {

			Plugins.getInstance();

		} catch (Error e) {

			throw new ServletException(e);

		}

		System.out.println("Done.");
		System.out.println("Initializing bridge...");

		try {

			if (cfgElem.getChild("bridge").getChildText("opened")
					.equals("true")) {

				Bridge.getInstance();

			}

		} catch (Error e) {

			throw new ServletException(
					"An error has occured when opening Bridge.", e);

		}

		System.out.println("Done.");
		System.out.println("Done loading Arduino driving server.");

	}

	/**
	 * This method is used to get the config element having a given name (from
	 * ADS-cfg.xml).
	 * 
	 * @param name
	 *            The name of the config element to get.
	 * @return The config element (or <code>null</code> if it doesn't exist.
	 */
	public static Element getConfigElement(String name) {

		return cfgElem.getChild(name);

	}

	public void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {

		this.getServletContext().getRequestDispatcher("/index.jsp")
				.forward(req, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.getServletContext().getRequestDispatcher("/index.jsp")
				.forward(request, response);

	}

	/**
	 * This method is used to get the real path to the given file.
	 * 
	 * @param path
	 *            The path to get.
	 * @return The real path.
	 */
	public static String getRealPath(String path) {

		return instance.getServletContext().getRealPath(path);

	}

	public String getServletInfo() {

		return "Arduino driving server is used to drive Arduinos.";

	}

	public ServletConfig getServletConfig() {
		return cfg;
	}

}
