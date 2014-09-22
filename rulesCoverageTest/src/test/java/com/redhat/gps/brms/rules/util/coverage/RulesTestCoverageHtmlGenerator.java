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
package com.redhat.gps.brms.rules.util.coverage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.redhat.gps.brms.event.RuleActivationLoggerAgendaEventListener;
import com.redhat.gps.util.properties.PropertiesManager;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Cool stuff by Duncan to generate a HTML page on coverage of rules during
 * testing.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */
public class RulesTestCoverageHtmlGenerator {

	private static final String RESOURCES_PATH = "../test-classes/com/redhat/gps/brms/rules/util/coverage";
	private static final String JS_RESOURCES_PATH = RESOURCES_PATH+"/js";
	private static final Logger LOGGER = Logger
			.getLogger(RulesTestCoverageHtmlGenerator.class);

	public static void generateHtml(List<RulesSetTestEntity> rstEntities) {

		// Configuration
		Writer file = null;
		Configuration cfg = new Configuration();

		String rootPath = PropertiesManager.getInstance().getProperty("rules.home");
		String totalPath = rootPath + "rulesSetCoverage/html";

		try {
			// Set Directory for templates
			cfg.setTemplateLoader(new ClassTemplateLoader(
					RulesSetTestEntity.class, "/com/redhat/gps/brms/rules/util/coverage/templates"));
			// load template
			Template template = cfg.getTemplate("rulesCoverage.ftl");

			// data-model
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("container", "JBoss BRMS");

			// create list
			input.put("rstEntities", rstEntities);

			// File output
			File path = new File(totalPath);
			if (!path.exists()) {
				path.mkdirs();
				// copy js files into target folder
				Files.copy(
						FileSystems.getDefault().getPath(
								rootPath + JS_RESOURCES_PATH,
								"justgage.1.0.1.min.js"),
						FileSystems.getDefault().getPath(totalPath,
								"justgage.1.0.1.min.js"),
						StandardCopyOption.REPLACE_EXISTING);
				Files.copy(
						FileSystems.getDefault()
								.getPath(rootPath + JS_RESOURCES_PATH,
										"raphael.2.1.0.min.js"),
						FileSystems.getDefault().getPath(totalPath,
								"raphael.2.1.0.min.js"),
						StandardCopyOption.REPLACE_EXISTING);
			}
			file = new BufferedWriter(new FileWriter(new File(totalPath
					+ "/index.html")));
			template.process(input, file);
			file.flush();

		} catch (Exception e) {
			String errorMessage = "Error creating HTML report.";
			LOGGER.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ioe) {
					LOGGER.error("Error closing file writer.", ioe);
					// Not much we can about it, so swallowing exception.
				}
			}
		}

	}

}
