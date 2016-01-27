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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.io.impl.UrlResource;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.agent.KnowledgeAgentFactory;

import com.redhat.gps.brms.event.AgendaEventListenerFactory;
import com.redhat.gps.brms.util.KnowledgeBuilderUtil;
import com.redhat.gps.util.properties.PropertiesManager;





/**
 * Abstract JBoss BRMS EngineManager. This component is responsible for loading XLS and DRL rule files and creating BRMS
 * {@link KnowledgeBase KnowledgeBases}.
 * 
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 * @version $Revision$
 */
public abstract class AbstractJBossBRMSEngineManagerCore {

	private static Logger logger = Logger.getLogger(AbstractJBossBRMSEngineManagerCore.class);

	private KieBase kbase = null;

	private static FilenameFilter xlsFilenameFilter = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			if (name.endsWith(".xls")) {
				return true;
			}
			return false;
		}
	};

	private static FilenameFilter drlFilenameFilter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			if (name.endsWith(".drl")) {
				return true;
			}
			return false;
		}
	};
	
	private static FilenameFilter drfFilenameFilter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			if (name.endsWith(".rf")) {
				return true;
			}
			return false;
		}
	};
	
	@Deprecated
	protected final static KnowledgeBase createKnowledgeBaseFromRepo(String repoUrlString) {
		 // Initialize the Knowledge Session.
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        UrlResource urlResource = (UrlResource) ResourceFactory.newUrlResource(repoUrlString);
        urlResource.setBasicAuthentication("enabled");
        urlResource.setUsername(PropertiesManager.getInstance().getProperty("guvnor.user"));
        urlResource.setPassword(PropertiesManager.getInstance().getProperty("guvnor.passwd"));

        kbuilder.add((Resource)urlResource, ResourceType.PKG);
        return kbuilder.newKnowledgeBase();
		
	}
	
	@Deprecated
	protected final static KnowledgeBase createKnowledgeBaseFromRepoWithChangeListener(String repoUrlString) {
	KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "MyAgent" );
	
	UrlResource urlResource = (UrlResource) ResourceFactory.newUrlResource(repoUrlString);
    urlResource.setBasicAuthentication("enabled");  
    urlResource.setUsername(PropertiesManager.getInstance().getProperty("guvnor.user"));
    urlResource.setPassword(PropertiesManager.getInstance().getProperty("guvnor.passwd"));
	
    kagent.applyChangeSet(urlResource);
    
   // KnowledgeBase newKbase = kagent.getKnowledgeBase();
    
    ResourceFactory.getResourceChangeScannerService().start();
    //return newKbase;
	return null;
	}
    
	@Deprecated
	protected final static KnowledgeBase createKnowledgeBaseFromFiles(String rulePath) {
		logger.info("Creating knowledge-base out dir " + rulePath);
		DecisionTableConfiguration dtConf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
		dtConf.setInputType(DecisionTableInputType.XLS);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Iterator<String> xlsFileListIter = getXlsFileList(rulePath).iterator();
		while (xlsFileListIter.hasNext()) {
			String filename = (String) xlsFileListIter.next();
			logger.info("Adding xls-resource " + filename + " to knowledge-base");
			Resource xlsResource = ResourceFactory.newFileResource(filename);
			KnowledgeBuilderUtil.add(kbuilder, xlsResource, ResourceType.DTABLE, dtConf);
					
		}

		Iterator<String> drlFileListIter = getDrlFileList(rulePath).iterator();
		while (drlFileListIter.hasNext()) {
			String filename = (String) drlFileListIter.next();
			logger.info("Adding drl-resource " + filename + " to knowledge-base");
			Resource drlResource = ResourceFactory.newFileResource(filename);
			KnowledgeBuilderUtil.add(kbuilder, drlResource, ResourceType.DRL);
		}
		
		Iterator<String> drfFileListIter = getDrfFileList(rulePath).iterator();
		while (drfFileListIter.hasNext()) {
			String filename = (String) drfFileListIter.next();
			logger.info("Adding drf-resource " + filename + " to knowledge-base");
			Resource drfResource = ResourceFactory.newFileResource(filename);
			KnowledgeBuilderUtil.add(kbuilder, drfResource, ResourceType.DRF);
		}

		if (kbuilder.hasErrors()) {
			logger.error("Errors in knowledge-builder: " + kbuilder.getErrors().toString());
			throw new RuntimeException(kbuilder.getErrors().toString());
		}

		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		return kbase;

	}

	private static List<String> getXlsFileList(String rulePath) {
		File baseDir = new File(rulePath);
		List<String> fileNameList = new ArrayList<String>();
		if (baseDir.isDirectory()) {

			String[] fileNameArray = baseDir.list(xlsFilenameFilter);
			for (int i = 0; i < fileNameArray.length; i++) {
				fileNameList.add(baseDir.getAbsolutePath() + "/" + fileNameArray[i]);
			}
		}
		return fileNameList;
	}

	private static List<String> getDrlFileList(String rulePath) {
		File baseDir = new File(rulePath);
		List<String> fileNameList = new ArrayList<String>();
		if (baseDir.isDirectory()) {
			String[] fileNameArray = baseDir.list(drlFilenameFilter);
			for (int i = 0; i < fileNameArray.length; i++) {
				fileNameList.add(baseDir.getAbsolutePath() + "/" + fileNameArray[i]);
			}
		}
		return fileNameList;
	}
	
	private static List<String> getDrfFileList(String rulePath) {
		File baseDir = new File(rulePath);
		List<String> fileNameList = new ArrayList<String>();
		if (baseDir.isDirectory()) {
			String[] fileNameArray = baseDir.list(drfFilenameFilter);
			for (int i = 0; i < fileNameArray.length; i++) {
				fileNameList.add(baseDir.getAbsolutePath() + "/" + fileNameArray[i]);
			}
		}
		return fileNameList;
	}

	public KieBase getKnowledgeBase() {
		return kbase;
	}

	public void setKnowledgeBase(KieBase knowledgeBase) {
		kbase = knowledgeBase;
	}

	protected void attachAgendaEventListeners(final KieSession ksession) {
		List<AgendaEventListener> listeners = AgendaEventListenerFactory.getAgendaEventListeners();
		for (AgendaEventListener nextListener : listeners) {
			ksession.addEventListener(nextListener);
		}
	}

	protected void attachAgendaEventListeners(final StatelessKieSession ksession) {
		List<AgendaEventListener> listeners = AgendaEventListenerFactory.getAgendaEventListeners();
		for (AgendaEventListener nextListener : listeners) {
			ksession.addEventListener(nextListener);
		}
	}

}
