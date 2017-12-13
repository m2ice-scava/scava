/*******************************************************************************
 * Copyright (c) 2014 CROSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    James Williams - Implementation.
 *******************************************************************************/
package org.eclipse.crossmeter.platform.client.api.tests;

import static org.junit.Assert.*;

import org.eclipse.crossmeter.platform.Platform;
import org.eclipse.crossmeter.platform.client.api.ProjectImporter;
import org.eclipse.crossmeter.repository.model.Project;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.Mongo;

public class TestProjectImporter extends TestAbstractResource {

	static Mongo mongo;
	static Platform platform;
	
	@BeforeClass
	public static void setup() throws Exception {
		mongo = new Mongo();
		platform = new Platform(mongo);
	}
	
	@AfterClass
	public static void close() throws Exception {
		mongo.close();
	}
	
	@Test
	public void testEclipse() throws Exception {
		ProjectImporter importer = new ProjectImporter();
		Project p = importer.importProject("https://projects.eclipse.org/projects/modeling.epsilon", platform);
		
		assertNotNull(p);
		assertEquals("Epsilon", p.getName());
		
		p = importer.importProject("https://projects.eclipse.org/projects/modeling.epsiloon", platform);
		assertNull(p);
	}
	
	@Test
	public void testGitHub() throws Exception {
		ProjectImporter importer = new ProjectImporter();
		Project p = importer.importProject("https://github.com/jrwilliams/gif-hook", platform);
		
		assertNotNull(p);
		assertEquals("gif-hook", p.getName());
		
		p = importer.importProject("https://github.com/jrwilliams/", platform);
		assertNull(p);
	
	}
	
	@Test
	public void testSourceForge() {

	
	}
	
	

}
