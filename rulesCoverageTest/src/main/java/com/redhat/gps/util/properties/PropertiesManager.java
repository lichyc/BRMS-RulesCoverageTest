/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.redhat.gps.util.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jboss.vfs.VirtualFile;

import com.acme.brms.engine.GenericRuleEngine;

/**
 * Properties Services to more easy deal with content of multiple and spread properties files.
 * It's a Singleton which hold the properties in memory.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */

public class PropertiesManager {
	
	public static final Logger LOGGER = Logger.getLogger(PropertiesManager.class) ;
	
	private  HashMap<String, String> inMemoryProperties;
	
	private String packageRoot = "/";
	
	private static PropertiesManager myself;
	
	private PropertiesManager(String packageRoot) {
		if (null != packageRoot) {
			this.packageRoot = packageRoot;
		}
		loadPropertiesFiles();
		
	}
	
	public static PropertiesManager getInstance(String packageRoot) {
		if(null == myself) {
			myself = new PropertiesManager(packageRoot);
			myself.loadPropertiesFiles();
		}
		
		return myself;
	}
	
	public static PropertiesManager getInstance() {
		return getInstance(null);
	}
	
	
	public void loadPropertiesFiles() {
		
		String[] urls = getResourcesInPackage(packageRoot, ".*properties");
		
		HashMap<String, String> tmpMap = new HashMap<String, String>();

		ClassLoader classLoader = PropertiesManager.class.getClassLoader();
		assert classLoader != null;

		for (int i = 0; i < urls.length; i++) {
			InputStream is = classLoader.getResourceAsStream(urls[i]);
			if (null != is) {
				Properties props = new Properties();
				try {
					props.load(is);
					
					Enumeration<Object> myEnum = props.keys();
					while (myEnum.hasMoreElements()) {
						String key = (String) myEnum.nextElement();
						if(tmpMap.containsKey(key)) {
							LOGGER.warn("Property "+key+" already exists - value "+ tmpMap.get(key)+" will be overwritten, by "+props.getProperty(key)+"! - Check properties files on consistence.");
						}
						tmpMap.put(key, props.getProperty(key));
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		
		inMemoryProperties = tmpMap;
		overloadSystemProperties();
		
	}
	
	private void overloadSystemProperties() {
		Enumeration<Object> sysPropKeyEnum = System.getProperties().keys();
		while (sysPropKeyEnum.hasMoreElements()) {	
			String sysPropKey = (String) sysPropKeyEnum.nextElement();
			if(inMemoryProperties.containsKey(sysPropKey)) {
				LOGGER.warn("Property "+sysPropKey+" already exists - value "+ inMemoryProperties.get(sysPropKey)+" will be overwritten, by "+System.getProperty(sysPropKey)+" from system properties! ");
			}
			inMemoryProperties.put((String)sysPropKey, (String) System.getProperty(sysPropKey));
		}
	}

	public String getProperty(String key) {
		return inMemoryProperties.get(key);
	}
	
	
	/**
	 * Recursive method used to find all classes in a given path (directory or
	 * zip file url). Directories are searched recursively. 
	 * 
	 * @param url
	 *            The base directory or url from which to search.
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @param regex
	 *            an optional class name pattern. e.g. .*Test
	 * @return The classes
	 */
	private static TreeSet<String> findResources(URL url, String packageName,
			Pattern regex) throws Exception {
		ClassLoader classLoader = PropertiesManager.class.getClassLoader();
		assert classLoader != null;

		TreeSet<String> urls = new TreeSet<String>();

		if (url.getProtocol().equals("jar")) {
			String jarFileName;
			JarFile jf;
			Enumeration<JarEntry> jarEntries;
			String entryName;

			// build jar file name, then loop through zipped entries
			jarFileName = URLDecoder.decode(url.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
			LOGGER.debug("processing external jar: " + jarFileName);
			jf = new JarFile(jarFileName);
			jarEntries = jf.entries();
			while (jarEntries.hasMoreElements()) {
				entryName = jarEntries.nextElement().getName();
				if (entryName.startsWith(packageName)
						&& (regex == null || regex.matcher(entryName).matches())) {
					urls.add(entryName);
					LOGGER.debug("Add resource: " + entryName);
				}
			}
		} else {

			File dir = null;
			if (url.getProtocol().equalsIgnoreCase("vfs")) {
				LOGGER.debug("processing resource in jboss vfs: " + url);
				URLConnection conn = url.openConnection();
				VirtualFile vf = (VirtualFile) conn.getContent();
				if (vf.isDirectory()) {
					List<VirtualFile> childList = vf.getChildrenRecursively();
					for (VirtualFile childVf : childList) {
						if (childVf.isFile()) {
							if (regex == null
									|| regex.matcher(childVf.getName())
											.matches()) {
								urls.add(childVf.asFileURL().getFile());
								LOGGER.debug("Add resource: "
										+ childVf.asFileURL() + " : "
										+ childVf.getName());
							}
						}
					}
				}

			} else {
				dir = new File(url.getPath());

				if (!dir.exists()) {
					LOGGER.warn("path not exists: " + url);
					return urls;
				}

				File[] files = dir.listFiles();
//				log.info("processing dir: " + url);
				for (File file : files) {
					LOGGER.debug("processing file system resource: " + file.getName());
					if (file.isDirectory()) {
						assert !file.getName().contains(".");
						urls.addAll(findResources(file.toURI().toURL(),
								packageName + "." + file.getName(), regex));
					} else {
						String resourceName = file.getAbsolutePath();
						if (regex == null
								|| regex.matcher(resourceName).matches()) {
							urls.add(resourceName);
							LOGGER.info("Add resource: " + resourceName
									+ " : " + file.getName());
						}
					}
				}
			}
		}
		return urls;
	}

	

	public static String[] getResourcesInPackage(String packageName,
			String regexFilter) {
		Pattern regex = null;
		if (regexFilter != null)
			regex = Pattern.compile(regexFilter);

		try {
			ClassLoader classLoader = PropertiesManager.class.getClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<URL> dirs = new ArrayList<URL>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(resource);
				LOGGER.debug("Located package in: " + resource.getFile());
			}

			TreeSet<String> urls = new TreeSet<String>();
			for (URL directory : dirs) {
				urls.addAll(findResources(directory, packageName, regex));
			}

			ArrayList<String> urlList = new ArrayList<String>();

			for (String url : urls) {
				for (URL dir : dirs) {
					if (url.startsWith(dir.getPath())) {
						String finalResourceName = null;
						if(packageName.endsWith("/")) {
							finalResourceName = url.replace(dir.getPath(), packageName);
						} else {
							finalResourceName = url.replace(dir.getPath(), packageName.replace(".", "/")+"/");
						}
						LOGGER.debug("Final Resource Name: " + finalResourceName);
						urlList.add(finalResourceName);
					}
				}
			}

			return urlList.toArray(new String[urls.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void printPropertiesContent(String[] urls) {

		ClassLoader classLoader = PropertiesManager.class.getClassLoader();
		assert classLoader != null;

		for (int i = 0; i < urls.length; i++) {
			InputStream is = classLoader.getResourceAsStream(urls[i]);
			if (null != is) {
//				log.info("SUCCESS !!!!  System-Classloader: "
//						+ classLoader.toString());
//				log.info("SUCCESS !!!!  PropertiesLoader: "
//						+ is.toString());
				Properties props = new Properties();
				try {
					props.load(is);
					
					Enumeration myEnum = props.keys();
					while (myEnum.hasMoreElements()) {
						Object key = (Object) myEnum.nextElement();
						LOGGER.info("ENTRY: key: " + key + " value: "
								+ props.get(key));
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else {
				is = classLoader.getSystemResourceAsStream(urls[i]);
				if (null != is) {
					LOGGER.info("PropertiesLoader: " + is.toString());
				} else {
					LOGGER.fatal("FAILURE !!!!  Classloader: "
							+ classLoader.toString());

					LOGGER.fatal("FAILURE !!!!  System-Classloader: "
							+ classLoader.getSystemClassLoader().toString());

				}
			}
		}

	}
	
	public void printInMemoryProperties() {
		Iterator<String> myEnum = inMemoryProperties.keySet().iterator();
		
		while (myEnum.hasNext()) {
			String key = (String) myEnum.next();
			LOGGER.info("inMemoryProperty: key: " + key + " value: "
					+ inMemoryProperties.get(key));
		}
	}

	public static void main(String[] args) {

		String[] urls = PropertiesManager.getResourcesInPackage("de.clb.jee.test",
				".*properties");

		PropertiesManager.printPropertiesContent(urls);

	}

}
