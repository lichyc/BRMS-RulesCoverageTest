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

import com.acme.brms.domain.SimpleFact;
import com.redhat.gps.brms.engine.core.AbstractJBossBRMSEngineManager;

/**
 * Simple implementation of a Engine Manager {@link AbstractJBossBRMSEngineManager} to fire rules on facts implementing {@link Workpackage}
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */

public class GenericJBossBRMSEngineManager extends
		AbstractJBossBRMSEngineManager {

	public GenericJBossBRMSEngineManager(String kieBaseName) {
		super(kieBaseName);
	}
	
	SimpleFact executeRulesOnWorkpackage(SimpleFact facts) throws Exception {
		executeRulesOnObject(facts);
		
		return facts;
	}

	public SimpleFact[] executeRulesOnWorkpackage(SimpleFact[] facts) throws Exception {
		executeRulesOnObjectArray(facts);
		return facts;
	}

}
