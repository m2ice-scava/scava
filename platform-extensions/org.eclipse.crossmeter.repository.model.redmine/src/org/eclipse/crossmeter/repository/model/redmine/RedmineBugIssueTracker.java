/*******************************************************************************
 * Copyright (c) 2014 CROSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Davide Di Ruscio - Implementation,
 *    Juri Di Rocco - Implementation.
 *******************************************************************************/
package org.eclipse.crossmeter.repository.model.redmine;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;

// protected region custom-imports on begin
// protected region custom-imports end

public class RedmineBugIssueTracker extends org.eclipse.crossmeter.repository.model.BugTrackingSystem {
	
	protected List<RedmineIssue> issues = null;
	
	// protected region custom-fields-and-methods on begin
    @Override
    public String getBugTrackerType() {
        return "redmine";
    }

    @Override
    public String getInstanceId() {
        return getUrl() + ':' + getProject();
    }
    // protected region custom-fields-and-methods end
	
	public RedmineBugIssueTracker() { 
		super();
		dbObject.put("issues", new BasicDBList());
		super.setSuperTypes("org.eclipse.crossmeter.repository.model.redmine.BugTrackingSystem");
		NAME.setOwningType("org.eclipse.crossmeter.repository.model.redmine.RedmineBugIssueTracker");
		PROJECT.setOwningType("org.eclipse.crossmeter.repository.model.redmine.RedmineBugIssueTracker");
		LOGIN.setOwningType("org.eclipse.crossmeter.repository.model.redmine.RedmineBugIssueTracker");
		PASSWORD.setOwningType("org.eclipse.crossmeter.repository.model.redmine.RedmineBugIssueTracker");
	}
	
	public static StringQueryProducer NAME = new StringQueryProducer("name"); 
	public static StringQueryProducer PROJECT = new StringQueryProducer("project"); 
	public static StringQueryProducer LOGIN = new StringQueryProducer("login"); 
	public static StringQueryProducer PASSWORD = new StringQueryProducer("password"); 
	
	
	public String getName() {
		return parseString(dbObject.get("name")+"", "");
	}
	
	public RedmineBugIssueTracker setName(String name) {
		dbObject.put("name", name);
		notifyChanged();
		return this;
	}
	public String getProject() {
		return parseString(dbObject.get("project")+"", "");
	}
	
	public RedmineBugIssueTracker setProject(String project) {
		dbObject.put("project", project);
		notifyChanged();
		return this;
	}
	public String getLogin() {
		return parseString(dbObject.get("login")+"", "");
	}
	
	public RedmineBugIssueTracker setLogin(String login) {
		dbObject.put("login", login);
		notifyChanged();
		return this;
	}
	public String getPassword() {
		return parseString(dbObject.get("password")+"", "");
	}
	
	public RedmineBugIssueTracker setPassword(String password) {
		dbObject.put("password", password);
		notifyChanged();
		return this;
	}
	
	
	public List<RedmineIssue> getIssues() {
		if (issues == null) {
			issues = new PongoList<RedmineIssue>(this, "issues", true);
		}
		return issues;
	}
	
	
}