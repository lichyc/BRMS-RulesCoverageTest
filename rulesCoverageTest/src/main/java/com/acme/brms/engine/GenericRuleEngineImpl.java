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
 
package com.acme.brms.engine;

import java.util.HashMap;

import com.acme.brms.domain.SimpleFact;
import com.redhat.gps.util.properties.PropertiesManager;

/**
 * Implementation of a generic rules engine as Singleton making use of {@link GenericJBossBRMSEngineManager}.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */

public class GenericRuleEngineImpl implements GenericRuleEngine {
	
	static GenericRuleEngine me = null;
	
	HashMap<String, GenericJBossBRMSEngineManager> kbaseMap;
	
	private GenericRuleEngineImpl(){
		super();
		kbaseMap = new HashMap<String, GenericJBossBRMSEngineManager>();
	}
	
	public static GenericRuleEngine getInstance(){
		if (me == null) {
			me = new GenericRuleEngineImpl();
		}
		
		return me;
	}
	
	private GenericJBossBRMSEngineManager getGenericJBossBRMSEngineManager(
			String ruleSetName) throws Exception {
		if (kbaseMap.containsKey(ruleSetName)) {
			return kbaseMap.get(ruleSetName);
		} else {
			String ruleBase = PropertiesManager.getInstance("com.acme.brms").getProperty(ruleSetName);
			if(ruleBase != null && !ruleBase.isEmpty()) {
				GenericJBossBRMSEngineManager nextManager = new GenericJBossBRMSEngineManager(ruleBase);
				kbaseMap.put(ruleSetName, nextManager);
				return nextManager;
			} else {
				throw new Exception(String.format("No rule base location found for name: %s.", ruleSetName));
			}
			
			
		}
	}

	public SimpleFact executeRulesOnWorkpackage(SimpleFact facts, String ruleSetName) throws Exception {
		
		GenericJBossBRMSEngineManager engineManager = getGenericJBossBRMSEngineManager(ruleSetName);

		return engineManager.executeRulesOnWorkpackage(facts);
	}
	
	public SimpleFact[] executeRulesOnWorkpackages(SimpleFact[] facts,
			String ruleSetName) throws Exception {
		
		GenericJBossBRMSEngineManager engineManager = getGenericJBossBRMSEngineManager(ruleSetName);
		
		return engineManager.executeRulesOnWorkpackage(facts);
	}
	
}
