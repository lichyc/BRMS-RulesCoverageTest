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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.acme.brms.domain.SimpleFact;



/**
 * Rest service to the  generic rules engine making use of {@link GenericRuleEngine}.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$
 */

@Path("/RuleService")
public class GenericRulesEngineRestfulFascade implements GenericRuleEngine {
	
	GenericRuleEngine worker = null;
	
	public GenericRulesEngineRestfulFascade() {
		if(worker == null) {
			worker = GenericRuleEngineImpl.getInstance();
		}
	}

	
	@POST
	@Path("/executeRulesOnWorkpackage/{ruleSet}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SimpleFact executeRulesOnWorkpackage(SimpleFact facts,
			@PathParam("ruleSet") String ruleSetName) throws Exception {
		
		return worker.executeRulesOnWorkpackage(facts, ruleSetName);
	}

	@POST
	@Path("/executeRulesOnWorkpackageArray/{ruleSet}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SimpleFact[] executeRulesOnWorkpackages(SimpleFact[] facts,
			@PathParam("ruleSet") String ruleSetName) throws Exception {

		return worker.executeRulesOnWorkpackages(facts, ruleSetName);
	}

}
