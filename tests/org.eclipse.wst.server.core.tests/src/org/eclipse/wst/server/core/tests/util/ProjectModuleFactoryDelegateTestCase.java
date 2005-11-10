/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestProjectModuleFactoryDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import junit.framework.Test;
import junit.framework.TestCase;

public class ProjectModuleFactoryDelegateTestCase extends TestCase {
	protected static ProjectModuleFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ProjectModuleFactoryDelegateTestCase.class, "ProjectModuleFactoryDelegateTestCase");
	}

	public void test00Create() {
		delegate = new TestProjectModuleFactoryDelegate();
	}

	public void test03GetModuleDelegate() {
		delegate.getModuleDelegate(null);
	}

	public void test04GetModules() {
		delegate.getModules();
	}

	public void test06TestProtected() throws Exception {
		((TestProjectModuleFactoryDelegate)delegate).testProtected();
	}
}