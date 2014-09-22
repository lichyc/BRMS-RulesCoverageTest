/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package com.acme.brms.rules;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.acme.brms.domain.AcmeFactA;
import com.acme.brms.engine.GenericRuleEngine;
import com.acme.brms.engine.GenericRuleEngineImpl;
import com.redhat.gps.util.properties.PropertiesManager;

/**
* Test Case to perform some simple tests on {@link GenericRuleEngineImpl}.
* Additionally it runs a comparison of execution times between {@link GenericRuleEngineImpl} and {@link JsonRuleService}.
*  
* @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
* @version $Revision$
*/
public class GenericRuleEngineTest {

	private static Logger LOGGER = Logger
			.getLogger(GenericRuleEngineTest.class);

	GenericRuleEngine classUnderTest = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		PropertiesManager.getInstance("");

		if (classUnderTest == null) {
			classUnderTest = GenericRuleEngineImpl.getInstance();
		}

	}

	@Test
	public void testMostSimpleA() throws Exception {
		AcmeFactA facts = new AcmeFactA();

		assertNotNull(classUnderTest);

		long starttime = System.currentTimeMillis();
		classUnderTest.executeRulesOnWorkpackage(facts, "test-rules-a");
		long endttime = System.currentTimeMillis();

		assertNotNull(facts);
		assertNotNull(facts.getName());
		LOGGER.info(String.format("Duration of initial execution was: %d ms",
				endttime - starttime));
	}
	
	@Test
	public void testMostSimpleB() throws Exception {
		AcmeFactA facts = new AcmeFactA();

		assertNotNull(classUnderTest);

		long starttime = System.currentTimeMillis();
		classUnderTest.executeRulesOnWorkpackage(facts, "test-rules-b");
		long endttime = System.currentTimeMillis();

		assertNotNull(facts);
		assertNotNull(facts.getName());
		LOGGER.info(String.format("Duration of initial execution was: %d ms",
				endttime - starttime));
	}
	
	@Test
	public void testMostSimpleC() throws Exception {
		AcmeFactA facts = new AcmeFactA();

		assertNotNull(classUnderTest);

		long starttime = System.currentTimeMillis();
		classUnderTest.executeRulesOnWorkpackage(facts, "test-rules-c");
		long endttime = System.currentTimeMillis();

		assertNotNull(facts);
		assertNotNull(facts.getName());
		LOGGER.info(String.format("Duration of initial execution was: %d ms",
				endttime - starttime));
	}
	
	@Test
	public void testMostSimpleD() throws Exception {
		AcmeFactA facts = new AcmeFactA();

		assertNotNull(classUnderTest);

		long starttime = System.currentTimeMillis();
		classUnderTest.executeRulesOnWorkpackage(facts, "test-rules-d");
		long endttime = System.currentTimeMillis();

		assertNotNull(facts);
		LOGGER.info(String.format("Duration of initial execution was: %d ms",
				endttime - starttime));
	}

}
