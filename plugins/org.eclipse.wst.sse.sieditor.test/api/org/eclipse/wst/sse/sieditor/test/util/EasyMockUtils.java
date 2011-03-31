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

import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;

public class EasyMockUtils {
	
	private static ThreadLocal<Set<Object>> mocks = new ThreadLocal<Set<Object>>() {
		protected Set<Object> initialValue() {
			return new HashSet<Object>();
		}
	};
	
    public static <T> T createNiceMock(Class<T> toMock) {
        T mock = EasyMock.createNiceMock(toMock);
        mocks.get().add(mock);
        return mock;
    }
    
    public static void replay() {
    	Set<Object> allMocks = mocks.get();
    	Object[] mocksArray = new Object[allMocks.size()];
    	allMocks.toArray(mocksArray);
    	EasyMock.replay(mocksArray);
    }
    
    public static void clean() {
    	mocks.get().clear();
    }
}
