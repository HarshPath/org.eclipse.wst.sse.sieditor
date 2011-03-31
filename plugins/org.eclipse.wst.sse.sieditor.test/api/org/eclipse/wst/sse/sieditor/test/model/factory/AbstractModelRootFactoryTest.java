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
package org.eclipse.wst.sse.sieditor.test.model.factory;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AnnotationsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AttributesReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.ElementsReconcileAdapter;

public abstract class AbstractModelRootFactoryTest extends SIEditorBaseTest {

    protected FileEditorInput eInput;

    @Test
    public void documentElementsAdapted() {
        final IDocumentProvider documentProvider = editor.getSourcePage().getDocumentProvider();
        final IDocument textDocument = documentProvider.getDocument(eInput);

        final IStructuredModel structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(textDocument);

        Document document = null;
        try {
            if (structuredModel instanceof IDOMModel) {
                document = ((IDOMModel) structuredModel).getDocument();
            }
        } finally {
            if (structuredModel != null)
                structuredModel.releaseFromRead();
        }
        assertNotNull(document);

        checkAdapters(document);
    }

    private void checkAdapters(final Node parentNode) {
        checkNotifiersAdapters((INodeNotifier) parentNode);

        for (Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                checkAdapters(child);
            }
        }
    }

    private void checkNotifiersAdapters(final INodeNotifier iNodeNotifier) {
        final Collection<INodeAdapter> adapters = iNodeNotifier.getAdapters();
        final Iterator<INodeAdapter> iterator = adapters.iterator();

        boolean elementsAdapterFound = false;
        boolean annotationAdapterFound = false;
        boolean attributesAdapterFound = false;

        while (iterator.hasNext()) {
            final INodeAdapter adapter = iterator.next();
            if (adapter instanceof AttributesReconcileAdapter) {
                assertFalse("more than one attributes adapter found for node: " + iNodeNotifier, attributesAdapterFound);
                attributesAdapterFound = true;
            } else if (adapter instanceof ElementsReconcileAdapter) {
                assertFalse("more than one elements adapter found for node: " + iNodeNotifier, elementsAdapterFound);
                elementsAdapterFound = true;
            } else if (adapter instanceof AnnotationsReconcileAdapter) {
                assertFalse("more than one annotations adapter found for node: " + iNodeNotifier, annotationAdapterFound);
                annotationAdapterFound = true;
            }
        }
        assertTrue("attributes adapter was not found for node: " + iNodeNotifier, attributesAdapterFound);
        assertTrue("annotations adapter was not found for node: " + iNodeNotifier, annotationAdapterFound);
        assertTrue("elements adapter was not found for node: " + iNodeNotifier, elementsAdapterFound);
    }

}
