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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple Bean to be used as data container during rule coverage calculation.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public class RulesSetTestEntity {
	
	private final String moduleName;
	
	private final Set<String> rules;
	
	private final Set<String> rulesFired;
	
	private final Set<String> rulesNotFired;
	
	private final long numberOfRules;
	
	private final long numberOfFiredRules;
	
	private final long percentageOfRulesFired;
	
	public RulesSetTestEntity(String moduleName, Set<String> rules, Set<String> rulesFired) {
		this.moduleName = moduleName;
		this.rules = rules;
		this.rulesFired = rulesFired;
		this.numberOfRules = rules.size();
		this.numberOfFiredRules = rulesFired.size();
		if (numberOfRules > 0 ) { 
			this.percentageOfRulesFired = ((numberOfFiredRules * 100)/ numberOfRules); 
		} else {
			this.percentageOfRulesFired = 0;
		}
		this.rulesNotFired = getNotFiredRules(rules, rulesFired);
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public long getNumberOfRules() {
		return numberOfRules;
	}
	
	public long getNumberOfRulesFired() {
		return numberOfFiredRules;
	}
	
	public long getPercentageOfRulesFired() {
		return percentageOfRulesFired;
	}
	
	public Set<String> getNotFiredRules(Set<String> rules, Set<String> rulesFired) {
		Set<String> different = new HashSet<String>();
		Collection<String> similar = new HashSet<String>(rules);
		similar.retainAll(rulesFired);
		different.addAll(rules);
		different.addAll(rulesFired);
		different.removeAll(similar);
		return different;
	}
	
}

