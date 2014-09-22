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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;

public class RulesComparer {
	
	public static final Logger LOGGER = Logger.getLogger(RulesComparer.class);
	private String rootPath = "";
	private String logPathFiredRules = "../log/ruleActivation.log";
	private String logPathAllRules = "../log/allRuleDump.log";
	
	public RulesComparer() {
			rootPath = "/";
	}

	/*
	 * This method dumps all the rules in a log file
	 */
	public static void dumpRule(KnowledgeBase kbase) {

		Iterator<KnowledgePackage> temp = kbase.getKnowledgePackages().iterator();
		//System.out.println("Before Parsing");
		while (temp.hasNext()) {
			Collection<Rule> ruleCollection = temp.next().getRules();
			Iterator<Rule> temp2 = ruleCollection.iterator();
			
			while (temp2.hasNext()) {
				Rule rule = temp2.next();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(rule.getPackageName() + "." + rule.getName());
				}
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RulesComparer ruleComparer = new RulesComparer();
		
		ruleComparer.generateComparisionResult();

	}
	
	/*
	 * This method generates Comparision result from two lists:
	 * 		list of all the rules
	 * 		list of all the rules fired 
	 */
	private void generateComparisionResult() {
		
		BufferedReader br = null;
		ArrayList<String> listOfAllRules = new ArrayList<String>();
		ArrayList<String> listOfFiredRules = new ArrayList<String>();
		Collection<String> different = new HashSet<String>();
	
		String line;
		
		try {
			// read all the rules and store it in a list
			br = new BufferedReader(new FileReader(rootPath+logPathAllRules));
			while ((line = br.readLine()) != null) {
			   String arrayString[] = line.split(" ");
			   listOfAllRules.add(arrayString[6]);
			}

			// read all the fired rules and store it in a list
			br = new BufferedReader(new FileReader(rootPath+logPathFiredRules));
			while ((line = br.readLine()) != null) {
			   String arrayString[] = line.split(" ");
			   listOfFiredRules.add(arrayString[6]);
			}
			
			// remove all duplicates
			listOfAllRules = new ArrayList<String>(new LinkedHashSet<String>(listOfAllRules));
			listOfFiredRules = new ArrayList<String>(new LinkedHashSet<String>(listOfFiredRules));
			
			// print total number of rules
			System.out.println("TOTAL NUMBER OF RULES = [" +listOfAllRules.size()+ "]");
			System.out.println("TOTAL NUMBER OF RULES FIRED = [" +listOfFiredRules.size()+ "]");
			System.out.println("COVERAGE IN PERCENTAGE = [" + (listOfFiredRules.size()*100/listOfAllRules.size()) + " %]");
			
			// compare and print all the rules which are not fired
			Collection<String> similar = new HashSet<String>(listOfAllRules);
			similar.retainAll(listOfFiredRules);
			different.addAll(listOfAllRules);
			different.addAll(listOfFiredRules);
			different.removeAll(similar);
			System.out.println("LIST OF ALL THE MISSED RULES: ");
			
			Iterator<String> temp = different.iterator();
			while (temp.hasNext()) {
				System.out.println(temp.next());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
