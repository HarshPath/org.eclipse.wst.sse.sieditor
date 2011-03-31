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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Before;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.ExtractNamespaceRunnable;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ExtractNamespaceRunnableWithImportTest extends ExtractNamespaceRunnableWithoutImportTest {

    private boolean importCalled = false;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        importCalled = false;
    }

    @Override
    protected ExtractNamespaceRunnable createExtractRunnable(final ISchema schema, final SchemaNode schemaNode,
            final Set<SchemaNode> dependenciesSet, final IPath wsdlLocationPath) {
        final ExtractNamespaceRunnable extractRunnable = new ExtractNamespaceRunnable(schemaNode, dependenciesSet, false,
                wsdlLocationPath) {
            @Override
            protected IStatus importExtractedSchemas(final IProgressMonitor monitor, final Set<SchemaNode> schemasToExtract) {
                assertFalse(super.keepInlinedNamespaces);
                importCalled = true;
                return Status.OK_STATUS;
            }
        };
        return extractRunnable;
    }

    @Override
    protected void assertPostExtractState(final File[] schemaFiles) throws Exception {
        super.assertPostExtractState(schemaFiles);
        assertTrue("importExtractedSchemas() was not called", importCalled);
    }

}
