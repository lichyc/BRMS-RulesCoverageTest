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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
//import org.drools.event.rule.AgendaEventListener;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.runtime.StatelessKnowledgeSession;

import org.kie.api.event.rule.AgendaEventListener;

import com.redhat.gps.util.properties.PropertiesManager;



/**
 * Factory which loads {@link AgendaEventListener AgendaEventListeners} to be attache to {@link StatelessKnowledgeSession
 * StatelessKnowledgeSessions} and/or {@link StatefulKnowledgeSession StatefulKnowledgeSessions}.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public class AgendaEventListenerFactory {

	private static Logger LOGGER = Logger.getLogger(AgendaEventListenerFactory.class);

	private static final String AGENDA_EVENT_LISTENERS_PROPERTY = "com.redhat.gps.brms.agendaEventListeners";

	private static List<Class<? extends AgendaEventListener>> eventListenerClasses;

	static {
		eventListenerClasses = new ArrayList<Class<? extends AgendaEventListener>>();
		String agendaEventListeners = System.getProperty(AGENDA_EVENT_LISTENERS_PROPERTY);
		if  (agendaEventListeners == null || ("".equals(agendaEventListeners))) {
			agendaEventListeners = PropertiesManager.getInstance().getProperty(AGENDA_EVENT_LISTENERS_PROPERTY);
		}
		if (agendaEventListeners != null && !("".equals(agendaEventListeners))) {
			LOGGER.debug("Attach AgendaEventListeners to Knowledge Session: " + agendaEventListeners);
			// AgendaEventListeners have been configured, load classes and attach to ksession
			String[] agendaEventListenerArray = agendaEventListeners.split(",");
			for (String nextAgendaEventListener : agendaEventListenerArray) {
				Class<? extends AgendaEventListener> nextAgendaEventListenerClass;
				try {
					nextAgendaEventListenerClass = Class.forName(nextAgendaEventListener).asSubclass(AgendaEventListener.class);
					eventListenerClasses.add(nextAgendaEventListenerClass);
				} catch (ClassNotFoundException cnfe) {
					String message = "Error attaching AgendaEventListener '" + nextAgendaEventListener + "' to StatelessKnowledgeSession.";
					LOGGER.error(message, cnfe);
					throw new RuntimeException(message, cnfe);
				}
			}
		}
	}

	public static List<AgendaEventListener> getAgendaEventListeners() {
		List<AgendaEventListener> listeners = new ArrayList<AgendaEventListener>();
		for (Class<? extends AgendaEventListener> nextAgendaEventListenerClass : eventListenerClasses) {

			AgendaEventListener nextAgendaEventListenerInstance;
			try {
				nextAgendaEventListenerInstance = nextAgendaEventListenerClass.newInstance();
				listeners.add(nextAgendaEventListenerInstance);
			} catch (InstantiationException ie) {
				String message = "Error attaching AgendaEventListener '" + nextAgendaEventListenerClass.getCanonicalName()
						+ "' to StatelessKnowledgeSession.";
				LOGGER.error(message, ie);
				throw new RuntimeException(message, ie);
			} catch (IllegalAccessException iae) {
				String message = "Error attaching AgendaEventListener '" + nextAgendaEventListenerClass.getCanonicalName()
						+ "' to StatelessKnowledgeSession.";
				throw new RuntimeException(message, iae);
			}
		}
		return listeners;
	}
}
