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
package com.redhat.gps.brms.engine.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.redhat.gps.brms.engine.core.AbstractJBossBRMSEngineManagerCore;
import com.redhat.gps.util.properties.PropertiesManager;


/**
 * Abstract Engine Manager class, responsible for setting the {@link KnowledgeBase} for the particular engine subclass and for executing
 * rules on {@link StatelessKnowledgeSession StatelessKnowledgeSessions created from the {@link KnowledgeBase KnowledgeBases}.
 * <p/>
 * The {@link KnowledgeBase} is created by the {@link AbstractJBossBRMSEngineManagerCore} superclass.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public abstract class AbstractJBossBRMSEngineManager extends AbstractJBossBRMSEngineManagerCore {

	public static final Logger logger = Logger.getLogger(AbstractJBossBRMSEngineManager.class);
	
	private  String rootPath;
	
	private static boolean REUSE_KSESSION = false;
	
	private ThreadLocal<StatefulKnowledgeSession> statefulKnowledgeSession = new ThreadLocal<StatefulKnowledgeSession>() {
        protected synchronized StatefulKnowledgeSession initialValue() {        	
        	StatefulKnowledgeSession statefulKnowledgeSession = getKnowledgeBase().newStatefulKnowledgeSession();
        	attachAgendaEventListeners(statefulKnowledgeSession);
        	
            return statefulKnowledgeSession;
        }
    };

	public AbstractJBossBRMSEngineManager(String rulePath) {
		
			rootPath =PropertiesManager.getInstance().getProperty("rules.home");
			
			if(rulePath.startsWith("http")) {
				if (rulePath.endsWith("ChangeSet.xml")) {
					setKnowledgeBase(createKnowledgeBaseFromRepoWithChangeListener(rulePath));
				} else {
					setKnowledgeBase(createKnowledgeBaseFromRepo(rulePath));
				}
			} else {
				setKnowledgeBase(createKnowledgeBaseFromFiles(rootPath + rulePath));
			}

	}

	protected final void executeRulesOnObjectArray(Object[] obj) throws Exception {
		if(REUSE_KSESSION) {
			StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();
			for (Object nextObject : obj) {
				ksession.insert(nextObject);
			}
			ksession.fireAllRules();
			Iterator<FactHandle> factHandleIterator = ksession.getFactHandles().iterator();
			while (factHandleIterator.hasNext()) {
				FactHandle factHandle = (FactHandle) factHandleIterator.next();
				ksession.retract(factHandle);
			}
		} else {
			StatelessKnowledgeSession ksession = getKnowledgeBase().newStatelessKnowledgeSession();
			attachAgendaEventListeners(ksession);
			List<Command> commands = new ArrayList<Command>();
			for (Object nextObject : obj) {
				commands.add(CommandFactory.newInsert(nextObject));
			}
			ksession.execute(CommandFactory.newBatchExecution(commands));
		}
	}
	
	protected final void executeRulesOnObject(Object obj) throws Exception {
		if(REUSE_KSESSION) {
			StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();
			ksession.insert(obj);
			ksession.fireAllRules();
			Iterator<FactHandle> factHandleIterator = ksession.getFactHandles().iterator();
			while (factHandleIterator.hasNext()) {
				FactHandle factHandle = (FactHandle) factHandleIterator.next();
				ksession.retract(factHandle);
			}
		} else {
			StatelessKnowledgeSession ksession = getKnowledgeBase().newStatelessKnowledgeSession();
			attachAgendaEventListeners(ksession);
			ksession.execute(obj);
		}
	}
	
	protected final void executeRuleFlow(String ruleFlowName, Map<String, Object> parameterMap) throws Exception {
		StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();
		attachAgendaEventListeners(ksession);
		ksession.startProcess(ruleFlowName, parameterMap);
        ksession.fireAllRules();
	}
	
	private StatefulKnowledgeSession getStatefulKnowledgeSession() {
		
        return statefulKnowledgeSession.get();
    }

}
