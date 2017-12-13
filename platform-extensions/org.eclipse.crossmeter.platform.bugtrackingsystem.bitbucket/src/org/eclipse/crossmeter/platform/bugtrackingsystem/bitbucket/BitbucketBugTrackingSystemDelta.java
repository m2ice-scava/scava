/*******************************************************************************
 * Copyright (c) 2014 CROSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jacob Carter - Implementation.
 *******************************************************************************/
package org.eclipse.crossmeter.platform.bugtrackingsystem.bitbucket;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.crossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketPullRequest;
import org.eclipse.crossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemDelta;


public class BitbucketBugTrackingSystemDelta extends BugTrackingSystemDelta {

	private static final long serialVersionUID = 1L;
	
	private List<BitbucketPullRequest> pullRequests = new ArrayList<BitbucketPullRequest>();

	public List<BitbucketPullRequest> getPullRequests() {
		return pullRequests;
	}
	
}
