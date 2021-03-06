/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.module;

import junit.framework.TestCase;

public class DeleteModuleTestCase extends TestCase {
	public void test00DeleteWebModule() throws Exception {
		ModuleHelper.deleteModule(ModuleTestCase.WEB_MODULE_NAME);
	}

	public void test01DeleteClosedProject() throws Exception {
		ModuleHelper.deleteModule(ModuleTestCase.CLOSED_PROJECT);
	}
}