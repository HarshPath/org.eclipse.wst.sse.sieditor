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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.junit.Test;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.factory.IModelRootFactory;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;

/**
 * Made for a a bug fix, csn: C-1239964
 * 
 */
public class AbstractEditorWithSourcePageUndoHandlersDisposeTest {

    private static class TestModelRootFactory implements IModelRootFactory {
        @Override
        public IModelRoot createModelRoot(final Document document) {
            return null;
        }
    }

    private static class AbstractEditorWithSourcePageInheritor extends AbstractEditorWithSourcePage {

        @Override
        protected void addExtraPages(final IStorageEditorInput in) throws PartInitException {
            // TODO Auto-generated method stub
        }

        @Override
        protected IModelRoot createModelRoot(final Document document) {
            return new TestModelRootFactory().createModelRoot( document);
        }

        @Override
        protected void validate() {
            // TODO Auto-generated method stub
            
        }
    
    }

    @Test
    public final void testDispose() throws PartInitException {
        final IEditorSite editorSiteMock = createMock(IEditorSite.class);
        editorSiteMock.setSelectionProvider(isA(ISelectionProvider.class));
        // do not expect any calls of setGlobalActionHandler (called when the
        // undo and redo action handlers are not creadet => they are null
        replay(editorSiteMock);

        final AbstractEditorWithSourcePageInheritor page = new AbstractEditorWithSourcePageInheritor();

        page.init(editorSiteMock, new NullEditorInput());

        try {
            page.dispose();
        } catch (final NullPointerException e) {
            fail();
        }
        verify(editorSiteMock);
    }

    @Test
    public final void testDisposeWitHandlersSet() throws PartInitException {
        final IEditorSite editorSiteMock = createMock(IEditorSite.class);
        editorSiteMock.setSelectionProvider(isA(ISelectionProvider.class));
        final IActionBars actionBarsMock = createNiceMock(IActionBars.class);
        expect(editorSiteMock.getPage()).andReturn(createNiceMock(IWorkbenchPage.class)).anyTimes();
        expect(editorSiteMock.getActionBars()).andReturn(actionBarsMock);
        // calls for setting the action bars' action handlers are made => they
        // are created and not null
        replay(editorSiteMock, actionBarsMock);

        final AbstractEditorWithSourcePageInheritor page = new AbstractEditorWithSourcePageInheritor();
        page.init(editorSiteMock, new NullEditorInput());

        page.setGlobalActionHandlers();
        try {
            page.dispose();
        } catch (final NullPointerException e) {
            fail();
        }
        verify(editorSiteMock, actionBarsMock);

    }
    @Test
    public void testDefaultNullImplementationExists() {
    	final AbstractEditorWithSourcePageInheritor editor = new AbstractEditorWithSourcePageInheritor();
    	final XMLModelNotifierWrapper defaultModelNotifier = editor.getModelNotifier();
    	
    	assertNotNull(defaultModelNotifier);
    	
    	assertNotNull(defaultModelNotifier.getChangedNodes());
    	assertNotNull(defaultModelNotifier.getCollectedNotifications());
    }
    

}
