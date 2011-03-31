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
package org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

/**
 * 
 */
public class Arguments {

    private static final class AnyArgumentMatcher<T> implements
        IArgumentMatcher {

        private final Argument<T> argument;

        AnyArgumentMatcher(final Argument<T> argument) {
            this.argument = argument;
        }

        public void appendTo(final StringBuffer buffer) {
            buffer.append("<any argument>"); //$NON-NLS-1$
        }

        @SuppressWarnings("unchecked")
        public boolean matches(final Object o) {
            argument.setValue((T) o);
            return true;
        }
    }

    /**
     * Creates and registers a argument matcher which matches any argument. The
     * argument matcher is composed with the given {@link Argument} and saves
     * the parameter into the argument.
     * 
     * @param <T>
     * @param argument
     *            The argument where to store the parameter while
     *            {@link IArgumentMatcher#matches(Object)} is called.
     * @return <code>null</code>
     */
    public static <T> T any(final Argument<T> argument) {
        EasyMock.reportMatcher(new AnyArgumentMatcher<T>(argument));
        return null;
    }
}
