/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.scava.plugin.ui.abstractmvc;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.services.IDisposable;

public abstract class JFaceWindowView<DisplayType extends Window & IDisposable> extends DisplayableView<DisplayType> {
	
	public JFaceWindowView(DisplayType display) {
		super(display);
	}
}