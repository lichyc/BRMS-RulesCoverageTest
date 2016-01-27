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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.junit.AfterClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.gps.brms.event.RuleActivationLoggerAgendaEventListener;
import com.redhat.gps.util.properties.PropertiesManager;

/**
 * Test Case to check coverage of rule tests.
 * Enable {@link RuleActivationLoggerAgendaEventListener} and ensure that logs go into <code>rules-log.txt</code>.
 * Now all rules fired get logged to be evaluated during this test.<br/>
 * Declare a coverage test per knowledge base like this:
 * <p>
 *	<code>
 *  &amp;Test<br/>
 *  public void test&lt;Test Name&gt;Coverage() throws Exception {<br/>
 *		&nbsp;&nbsp;&nbsp;double actualCoverage = computeActualCoverage("&lt;Knowledge Base Key&gt;", "&lt;Test Name&gt;");<br/>
 *		&nbsp;&nbsp;&nbsp;assertTrue(String.format("Test Coverage >90%% expected, but is %.2f%%", actualCoverage), 90 < actualCoverage);<br/>
 *	}</code>
 * </p>
 * 
 * The result will get printed on std-out and a web page (&lt;project-root&gt;/target/classes/rulesSetCoverage/html/index.html) get prepared.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public class CoverageTest {
	
	private static final Logger logger = Logger.getLogger(CoverageTest.class);

	private final String rootPath;

	private static final String LOG_PATH_FIRED_RULES = "rules-log.txt";

	private static final String JSON_LOG_PATH = "/json";

	private static List<RulesSetTestEntity> rstEntities = new ArrayList<RulesSetTestEntity>();

	public CoverageTest() {
		super();
		rootPath =PropertiesManager.getInstance("com").getProperty("rules.home");
	}

	@Test
	public void testTestRulesCoverageA() throws Exception {
		double actualCoverage = computeActualCoverage("test-rules-a", "My Test-Rules A");
		assertTrue(String.format("Test Coverage >90%% expected, but is %.2f%%", actualCoverage), 90 < actualCoverage);
	}
	
	@Test
	public void testTestRulesCoverageB() throws Exception {
		double actualCoverage = computeActualCoverage("test-rules-b", "My Test-Rules B");
		assertTrue(String.format("Test Coverage >90%% expected, but is %.2f%%", actualCoverage), 90 < actualCoverage);
	}
	
	@Test
	public void testTestRulesCoverageC() throws Exception {
		double actualCoverage = computeActualCoverage("test-rules-c", "My Test-Rules C");
		assertTrue(String.format("Test Coverage >90%% expected, but is %.2f%%", actualCoverage), 90 < actualCoverage);
	}
	
	@Test
	public void testTestRulesCoverageD() throws Exception {
		double actualCoverage = computeActualCoverage("test-rules-d", "My Test-Rules D");
		assertTrue(String.format("Test Coverage >90%% expected, but is %.2f%%", actualCoverage), 90 < actualCoverage);
	}

	/**
	 * Generates the HTML report at the end of the run.
	 */
	@AfterClass
	public static void generateHtmlReport() {
		RulesTestCoverageHtmlGenerator.generateHtml(rstEntities);
	}
	

	/**
	 * Computes the actual coverage in percent for a given application and rule path.
	 * 
	 * @param ruleSetName
	 * @param applicationName
	 * @return Percentage of rules that have been fired.
	 * @throws Exception
	 */
	private double computeActualCoverage(String ruleSetName, String applicationName) throws Exception {
		
		String rulePath = PropertiesManager.getInstance().getProperty(ruleSetName);
		
		Map<String, List<String>> allRulesMap = getAllRulesList(createKnowledgeBase(ruleSetName));
		assertNotNull(allRulesMap);

		List<String> firedRulesList = getFiredRulesList(allRulesMap.get("packages"));
		assertNotNull(firedRulesList);
		RulesSetTestEntity rstEntity = compareLists(allRulesMap.get("rules"), firedRulesList, applicationName);
		/*
		 * Adding the entities to the list, so we can generate some HTML reports at the end. This is a bit of hacking ATM, API is not nice
		 * and responsibilities of methods are kinda crap ... I know ;-)
		 */
		rstEntities.add(rstEntity);
		return rstEntity.getPercentageOfRulesFired();
	}

	/**
	 * Computes the percentage of rules that have been fired.
	 * 
	 * @param allRulesList
	 *            List of all rules that could be potentially fired.
	 * @param firedRulesList
	 *            List of all rules concretely fired.
	 * @param moduleName
	 *            Name of the module.
	 * @return Percentage of rules that have been fired.
	 */
	private RulesSetTestEntity compareLists(List<String> allRulesList, List<String> firedRulesList, String moduleName) {

		// remove all duplicates
		Set<String> allRulesSet = new LinkedHashSet<String>(allRulesList);
		Set<String> firedRulesSet = new LinkedHashSet<String>(firedRulesList);

		allRulesList = new ArrayList<String>(allRulesSet);
		firedRulesList = new ArrayList<String>(firedRulesSet);

		RulesSetTestEntity rste = new RulesSetTestEntity(moduleName, allRulesSet, firedRulesSet);

		// print total number of rules
		System.out.println("Total number of available rules in " + rste.getModuleName() + " = [" + rste.getNumberOfRules() + "]");
		System.out.println("Total number of fired rules in " + rste.getModuleName() + " = [" + rste.getNumberOfRulesFired() + "]");
		System.out.println(moduleName + " Coverage = [" + rste.getPercentageOfRulesFired() + " %]");

		// compare and print all the rules which are not fired
		// Create a JSON file of the output so we can use the data in, for example, a Jenkins build.
		BufferedWriter bfWriter = null;
		try {
			try {
				bfWriter = new BufferedWriter(new FileWriter(rootPath+"/"+JSON_LOG_PATH + rste.getModuleName() + ".json"));
			} catch (IOException ioe) {
				String errorMessage = "Unable to open JSON output file.";
				logger.error(errorMessage, ioe);
				throw new RuntimeException(errorMessage, ioe);
			}

			Gson gson = new GsonBuilder().create();
			gson.toJson(rste, bfWriter);
		} finally {
			// Cleanup resources.
			if (bfWriter != null) {
				try {
					bfWriter.close();
				} catch (IOException ioe) {
					String errorMessage = "Error closing JSON writer.";
					logger.warn(errorMessage, ioe);
					// Not much we can do here, so swallowing exception.
				}
			}
		}

		return rste;
	}

	/**
	 * Returns a list of all fired rules.
	 * 
	 * @param packagesList
	 * @return List of all fired rules
	 * @throws Exception
	 */
	private List<String> getFiredRulesList(List<String> packagesList) throws Exception {
		List<String> listOfFiredRules = new ArrayList<String>();
		String line;
		BufferedReader br = null;
		// read all the fired rules and store it in a list
		try {
			br = new BufferedReader(new FileReader(LOG_PATH_FIRED_RULES));
			while ((line = br.readLine()) != null) {
				String arrayString[] = line.split("-");
				String fullRulesName = arrayString[3];
				fullRulesName = fullRulesName.substring(0, fullRulesName.indexOf(":")).trim();
				String rulePackageName = fullRulesName.substring(0, fullRulesName.lastIndexOf("."));

				for (String packageName : packagesList) {
					if (rulePackageName.contains(packageName))
						listOfFiredRules.add(fullRulesName);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return listOfFiredRules;
	}

	/**
	 * Extracts all rules contained into a knowledge base.
	 * 
	 * @param kbase
	 *            knowledge base.
	 * @return Extracted rules.
	 */
	private Map<String, List<String>> getAllRulesList(KieBase kbase) {

		List<String> rulesList = new ArrayList<String>();
		List<String> rulesPackageList = new ArrayList<String>();

		for (KiePackage kiePackage : kbase.getKiePackages()) {
			for (Rule rule : kiePackage.getRules()) {
				rulesList.add(rule.getPackageName() + "." + rule.getName());
				rulesPackageList.add(rule.getPackageName());
			}
		}

		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		resultMap.put("rules", rulesList);
		resultMap.put("packages", new ArrayList<String>(new LinkedHashSet<String>(rulesPackageList)));
		return resultMap;
	}

	/**
	 * Create a BRMS KnowledgeBase based on the DRL and XLS files of the given directory name. Will fail, if the knowledge builder contains
	 * errors after adding the resources.
	 * 
	 * @param rulePath
	 *            absolute directory path of the files to read.
	 * @return KnowledgeBase, containing the rules loaded.
	 */
	private KieBase createKnowledgeBase(String kieBaseName) {
		
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		KieBase kieBase = kieContainer.getKieBase(kieBaseName);
		
		
//		GenericJBossBRMSEngineManager engineManager = new GenericJBossBRMSEngineManager(rulePath);
//		
//		KnowledgeBase kbase = engineManager.getKnowledgeBase();
		// dump rules
		RulesComparer.dumpRule(kieBase);
		return kieBase;
		
	}
}
