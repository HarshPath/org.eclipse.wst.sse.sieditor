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
package org.eclipse.wst.sse.sieditor.test.all;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.ui.BasePresenterTest;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.ui.DefaultPresenterTest;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.ui.ViewDelegateTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 *  
 *  Adding MVP TestCase class into the suite class.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	BasePresenterTest.class,
	DefaultPresenterTest.class,
	ViewDelegateTest.class
	//WorkbenchPartTest.class
	                 }
		)
public class TestSuiteForMVP {

}
