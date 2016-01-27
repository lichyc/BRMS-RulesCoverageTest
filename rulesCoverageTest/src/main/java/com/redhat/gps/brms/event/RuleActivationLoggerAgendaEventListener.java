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
package com.redhat.gps.brms.event;

import org.apache.log4j.Logger;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;


/**
 * {@link AgendaEventListener} implementation which logs which rules are activated.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public class RuleActivationLoggerAgendaEventListener extends DefaultAgendaEventListener {
	
	public static final Logger LOGGER = Logger.getLogger(RuleActivationLoggerAgendaEventListener.class);

	@Override
	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		if (LOGGER.isDebugEnabled()) {
			Rule rule = event.getMatch().getRule();
			LOGGER.debug(rule.getPackageName() + "." + rule.getName() + " : " + event.getClass().getSimpleName());
		}
	}
}
