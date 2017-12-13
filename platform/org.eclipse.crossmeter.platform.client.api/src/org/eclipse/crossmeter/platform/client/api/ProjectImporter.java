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
package org.eclipse.crossmeter.platform.client.api;

import org.eclipse.crossmeter.platform.Platform;
import org.eclipse.crossmeter.repository.model.Project;
import org.eclipse.crossmeter.repository.model.eclipse.importer.EclipseProjectImporter;
import org.eclipse.crossmeter.repository.model.github.importer.GitHubImporter;
import org.eclipse.crossmeter.repository.model.importer.exception.WrongUrlException;
import org.eclipse.crossmeter.repository.model.sourceforge.importer.SourceforgeProjectImporter;

public class ProjectImporter {

	
//	TODO: This should be smarter and return helpful error messages
	public Project importProject(String url, Platform platform) throws WrongUrlException {
		Project p = null;
		
		url = url.replace("http://", "");
		url = url.replace("https://", "");
		url = url.replace("www.", "");
		
		System.out.println("front stripped: " + url);
		
		if (url.startsWith("projects.eclipse.org") || url.startsWith("eclipse.org")) {
			
			url = url.replace("projects/", "");
			url = url.replace("projects.", "");
			url = url.replace("eclipse.org/", "");
			if (url.contains("?")) {
				url = url.substring(0, url.indexOf("?"));
			}
			if (url.contains("/")) {
				url = url.substring(0, url.indexOf("/"));
			}
			
			System.out.println("url to import: " + url);
			try {
				EclipseProjectImporter importer = new EclipseProjectImporter();
				p = importer.importProject(url, platform);
			} catch (Exception e) {
				e.printStackTrace(); // FIXME better handling
				return null;
			}
		} else if (url.startsWith("github.com")) {
			url= url.replace("github.com/", "");
			String[] ps = url.split("/");
			if (ps.length != 2) {
				System.err.println("Invalid GithUb url");
				return null; // FIXME
			}
			String uName = ps[0];
			String pName = ps[1];
			
			GitHubImporter importer = new GitHubImporter();//"f280531cd5712b6cbff971b7610155cecc134b02"); //FIXME Temporary token
			try {
				p = importer.importProject(uName+"/" + pName, Platform.getInstance());
			} catch (WrongUrlException e) {
				e.printStackTrace(); // FIXME better handling
				throw e;
			}
		} else if(url.contains("sourceforge.net")) {
			SourceforgeProjectImporter importer = new SourceforgeProjectImporter();
			try {
				p = importer.importProjectByUrl(url, platform);
			} catch (WrongUrlException e) {
				e.printStackTrace();
				throw e;
			}
		}
		
		return p;
	}
}
