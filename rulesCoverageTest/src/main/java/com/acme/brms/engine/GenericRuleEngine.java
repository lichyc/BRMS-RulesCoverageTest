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

/**
 * Interface for a generic rules engine.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */
public interface GenericRuleEngine {
	
	/**
	 * Execute a set of rules defined by the <code>ruleSetName</code> on facts implementing {@link SimpleFact}
	 * 
	 * @param facts the {@link SimpleFact} instance to fire the rules against.
	 * @param ruleSetName key used as identifier for the set of rules.
	 * @return changed facts.
	 * @throws Exception thrown from deeper levels, if something fails.
	 */
	public SimpleFact executeRulesOnWorkpackage(SimpleFact facts, String ruleSetName) throws Exception;
	
	/**
	 * Execute a set of rules defined by the <code>ruleSetName</code> on a array of facts implementing {@link SimpleFact}
	 * 
	 * @param facts array of {@link SimpleFact}s to fire the rules against.
	 * @param ruleSetName key used as identifier for the set of rules.
	 * @return changed facts.
	 * @throws Exception thrown from deeper levels, if something fails.
	 */
	public SimpleFact[] executeRulesOnWorkpackages(SimpleFact[] facts, String ruleSetName) throws Exception;

}
