package com.ArduinoDrivingServer.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.zip.ZipFile;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.ArduinoDrivingServer.bridge.AbstractBridge;
import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This class contains the main part of the plugins system.
 * 
 * @author thejohncrafter
 *
 */
public class Plugins {
	
	/**
	 * This method loads all the plugins from the folder named <code>plugins</code>.
	 * @throws PluginException If an excaption occurs.
	 */
	public static HashMap<String, String> load() throws PluginException{
		
		if(!new File(ArduinoDriving.getRealPath("drivers")).exists()){
			
			System.out.println("Missing drivers directory. Creating...");
			
			if(!new File(ArduinoDriving.getRealPath("drivers")).mkdir())
				throw new PluginException("Can't create drivers directory !");
			
		}
		
		if(!new File(ArduinoDriving.getRealPath("plugins")).exists()){
			
			System.out.println("Missing plugins directory. Creating...");
			
			if(!new File("plugins").mkdir())
				throw new PluginException("Can't create plugins directory !");
			
		}
		
		try{
			
			HashMap<String, String> answ = new HashMap<String, String>();
			File[] pluginJars = new File(ArduinoDriving.getRealPath("plugins")).listFiles();
			System.out.println(pluginJars.length + " plugins to load.");
			
			for(File pluginJar : pluginJars){
				
				ZipFile zip = null;
				
				try{
					
					{
						
						File f = new File(pluginJar.getAbsolutePath() + "/plugin.jar");
						
						if(f.exists())
							zip = new ZipFile(f);
						else
							throw new PluginException("Can't load plugin " + pluginJar.getName() + " : missing plugin.jar !");
						
					}
					
					SAXBuilder builder = new SAXBuilder();
					Document document = builder.build(zip.getInputStream(zip.getEntry("ADS-plugin.xml")));
					Element rootNode = document.getRootElement();
					String pluginName = rootNode.getAttributeValue("name");
					
					System.out.println("Loading plugin " + pluginName + "...");
					
					loadContent(rootNode.getChild("content").getChildren().toArray(
							new Element[rootNode.getChild("content").getChildren().size()]), zip);
						
					zip.close();
					System.out.println("Done loading plugin " + pluginName + "...");
					
				}catch(Exception e){
					
					if(zip != null){
						
						zip.close();
						
					}
					
					System.err.println("Can't load plugin " + pluginJar.getName() + " !");
					e.printStackTrace();
					answ.put(pluginJar.getName(), e.getMessage());
					
				}
				
			}
			
			return answ;
			
		}catch(Exception e){
			
			throw new PluginException(e);
			
		}
		
	}
	
	/**
	 * This method is used to load the content of a given plugin.
	 * @param content The content element of the plugin.
	 * @param archive The plugin's archive.
	 * @throws IOException If an I/O exception occurs.
	 * @throws PluginException If another exception occurs.
	 */
	private static void loadContent(Element[] content, ZipFile archive) throws IOException, PluginException{
		
		for(Element e : content){
			
			if(e.getName().equals("driver")){
				
				String hid = e.getChildText("HID");
				Element[] files = e.getChild("files").getChildren().toArray(
						new Element[e.getChild("files").getChildren().size()]);
				
				for(Element file : files){
					
					String src = file.getAttributeValue("src");
					String out = file.getAttributeValue("out");
					System.out.println("Getting file from " + src + " and putting to " + out + "...");
					InputStream is = archive.getInputStream(archive.getEntry(src));
					new File(ArduinoDriving.getRealPath("drivers/" + hid.replace(" ", "_") + out)).createNewFile();
					FileOutputStream os = new FileOutputStream(new File(ArduinoDriving.getRealPath("drivers/" + hid.replace(" ", "_") + out)));
					
					while(is.available() > 0)
						os.write(is.read());
					
					os.close();
					
				}
				
			}else if(e.getName().equals("bridge")){
				
				loadBridge(new File(archive.getName()), e);
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to load w bridge from a plugin config element.
	 * @param archive The folder where the plugin is stored.
	 * @param cfg The bridge configuration element.
	 * @throws PluginException If an error occurs.
	 */
	private static void loadBridge(File archive, Element cfg) throws PluginException{
		
		String name = cfg.getAttributeValue("name");
		String desc = cfg.getChildText("desc");
		String id = cfg.getAttributeValue("id");
		String classname = cfg.getChildText("classname");
		Class<?> bridgeClass;
		AbstractBridge bridge;
		
		if(name == null)
			throw new PluginException("Can't load a bridge from plugin " + archive.getName() + " : missing attribute name !");
		
		if(classname == null)
			throw new PluginException("Can't load bridge " + name + " from plugin " + archive.getName() + " : missing classname !");
		
		if(desc == null)
			throw new PluginException("Can't load bridge " + name + " from plugin " + archive.getName() + " : missing <desc> child !");
		
		if(id == null)
			throw new PluginException("Can't load bridge " + name + " from plugin " + archive.getName() + " : missing attribute id !");
		
		try {
			
			URLClassLoader loader = new URLClassLoader(new URL[]{archive.toURI().toURL()}, Bridge.class.getClassLoader());
			bridgeClass = Class.forName(classname, true, loader);
			
		} catch (Exception e) {
			
			throw new PluginException("Can't load bridge " + name + " from plugin " + archive.getName() + " !", e);
			
		}
		
		try{
			
			bridge = (AbstractBridge) bridgeClass.newInstance();
			
		}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | ClassCastException e){
			
			throw new PluginException("Can't load bridge " + name + " from plugin " + archive.getName() + " : Can't instantiate " + bridgeClass.getName(), e);
			
		}
		
		bridge.setName(name);
		bridge.setDesc(desc);
		bridge.setId(id);
		bridge.setActivated(Bridge.isOpened());
		Bridge.getBridges().put(id, bridge);
		
	}
	
}
