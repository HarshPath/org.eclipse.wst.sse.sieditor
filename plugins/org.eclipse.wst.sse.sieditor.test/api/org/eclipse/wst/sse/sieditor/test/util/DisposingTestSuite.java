/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.test.util;

import static junit.framework.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import org.junit.AfterClass;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite that calls {@link DisposableTest#dispose()} for all such tests
 * when it is teared down. Useful for scenarios where allocated resources, e.g.
 * projects, should be upheld until the whole suite finishes.
 * 
 *
 */
public class DisposingTestSuite extends TestSuite {

	/**
	 * @see TestSuite#TestSuite()
	 */
	public DisposingTestSuite() {
		super();
	}

	/**
	 * @see TestSuite#TestSuite(String)
	 */
	public DisposingTestSuite(String name) {
		super(name);
	}

	/**
	 * @see TestSuite#TestSuite(Class)
	 */
	@SuppressWarnings("unchecked")
	public DisposingTestSuite(Class clazz) {
		super(clazz);
	}

	/**
	 * @see TestSuite#TestSuite(Class, String)
	 */
	@SuppressWarnings("unchecked")
	public DisposingTestSuite(Class clazz, String name) {
		super(clazz, name);
	}

	@AfterClass
	@SuppressWarnings("unchecked")
	public static void disposeTests(TestSuite suite) {
		StringBuilder errors = new StringBuilder();
		Enumeration<Test> tests = suite.tests();
		dispose(tests, errors);
		if (errors.length() > 0)
			fail(errors.toString());
	}
	
	@SuppressWarnings("unchecked")
	private static void dispose(Enumeration<Test> tests, StringBuilder errors) {
		while (tests.hasMoreElements()) {
			Test test = tests.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				dispose(suite.tests(), errors);
			} else if (test instanceof DisposableTest) {
				DisposableTest disposingTest = (DisposableTest) test;
				try {
					disposingTest.dispose();
				} catch (Exception e) {
					String error = exceptionToString(e);
					errors.append("\nFailure during disposal of test ").append(test.getClass().getName()).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
							.append(error);
				}
			}
		}
	}
	
	/**
	 * Converts the stack trace into a string
	 * 
	 * @return the trace as string
	 */
	public static String exceptionToString(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();
	}

}
