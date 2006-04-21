/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.tests;

import org.eclipse.wst.internet.monitor.core.tests.extension.*;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wst.internet.monitor.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTestSuite(ContentFiltersTestCase.class);
		suite.addTest(new OrderedTestSuite(MonitorTestCase.class));
		suite.addTest(new OrderedTestSuite(MonitorListenerTestCase.class));
		
		// removed this test due to occasional hangs in JUnit tests
		//https://bugs.eclipse.org/bugs/show_bug.cgi?id=135683
		//suite.addTest(new OrderedTestSuite(RequestTestCase.class));
		
		suite.addTest(new OrderedTestSuite(ContentFilterTestCase.class));
		//$JUnit-END$
		return suite;
	}
}