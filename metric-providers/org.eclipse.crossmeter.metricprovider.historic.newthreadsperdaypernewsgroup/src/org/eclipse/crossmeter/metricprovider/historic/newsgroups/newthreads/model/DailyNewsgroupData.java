/*******************************************************************************
 * Copyright (c) 2014 CROSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.eclipse.crossmeter.metricprovider.historic.newsgroups.newthreads.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class DailyNewsgroupData extends Pongo {
	
	
	
	public DailyNewsgroupData() { 
		super();
		NEWSGROUPNAME.setOwningType("org.eclipse.crossmeter.metricprovider.historic.newsgroups.newthreads.model.DailyNewsgroupData");
		NUMBEROFNEWTHREADS.setOwningType("org.eclipse.crossmeter.metricprovider.historic.newsgroups.newthreads.model.DailyNewsgroupData");
		CUMULATIVENUMBEROFNEWTHREADS.setOwningType("org.eclipse.crossmeter.metricprovider.historic.newsgroups.newthreads.model.DailyNewsgroupData");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsgroupName"); 
	public static NumericalQueryProducer NUMBEROFNEWTHREADS = new NumericalQueryProducer("numberOfNewThreads");
	public static NumericalQueryProducer CUMULATIVENUMBEROFNEWTHREADS = new NumericalQueryProducer("cumulativeNumberOfNewThreads");
	
	
	public String getNewsgroupName() {
		return parseString(dbObject.get("newsgroupName")+"", "");
	}
	
	public DailyNewsgroupData setNewsgroupName(String newsgroupName) {
		dbObject.put("newsgroupName", newsgroupName);
		notifyChanged();
		return this;
	}
	public int getNumberOfNewThreads() {
		return parseInteger(dbObject.get("numberOfNewThreads")+"", 0);
	}
	
	public DailyNewsgroupData setNumberOfNewThreads(int numberOfNewThreads) {
		dbObject.put("numberOfNewThreads", numberOfNewThreads);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfNewThreads() {
		return parseInteger(dbObject.get("cumulativeNumberOfNewThreads")+"", 0);
	}
	
	public DailyNewsgroupData setCumulativeNumberOfNewThreads(int cumulativeNumberOfNewThreads) {
		dbObject.put("cumulativeNumberOfNewThreads", cumulativeNumberOfNewThreads);
		notifyChanged();
		return this;
	}
	
	
	
	
}