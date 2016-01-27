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
package com.redhat.gps.brms.util;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;

/**
 * Utility class for JBoss BRMS {@link KnowledgeBuilder KnowledgeBuilders}. Created to synchronize all 'add' methods to the
 * {@link KnowlegdeBuilder} as a workaround for: https://issues.jboss.org/browse/JBRULES-3283
 * 
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@Deprecated
public class KnowledgeBuilderUtil {

	/**
	 * Synchronizes the call to {@link KnowlegdeBuilder#add} as a workaround for: https://issues.jboss.org/browse/JBRULES-3283
	 * 
	 * @param kbuilder
	 *            the {@link KnowledgeBuilder} to which to add the resource.
	 * @param resource
	 *            the {@link Resource} to add.
	 * @param resourceType
	 *            {@link the ResourceType} of the resource to add.
	 */
	public static synchronized void add(KnowledgeBuilder kbuilder, Resource resource, ResourceType resourceType) {
		kbuilder.add(resource, resourceType);
	}

	/**
	 * Synchronizes the call to {@link KnowlegdeBuilder#add} as a workaround for: https://issues.jboss.org/browse/JBRULES-3283
	 * 
	 * @param kbuilder
	 *            the {@link KnowledgeBuilder} to which to add the resource.
	 * @param resource
	 *            the {@link Resource} to add.
	 * @param resourceType
	 *            {@link the ResourceType} of the resource to add.
	 * @param resourceConfiguration
	 *            the {@link ResourceConfiguration} to add.
	 */
	public static synchronized void add(KnowledgeBuilder kbuilder, Resource resource, ResourceType resourceType,
			ResourceConfiguration resourceConfiguration) {
		kbuilder.add(resource, resourceType, resourceConfiguration);
	}

}
