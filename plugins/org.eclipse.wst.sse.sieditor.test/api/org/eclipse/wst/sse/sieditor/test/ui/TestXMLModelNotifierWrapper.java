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
package org.eclipse.wst.sse.sieditor.test.ui;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class TestXMLModelNotifierWrapper extends SIEditorBaseTest {
	
	private static final String TNS_HELLO = "http://hello";

	private IXSDModelRoot modelRoot = null;
	
	@Test 
	public void testEndChangingIsWrappedInEMFCommand() throws Exception {
        modelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        IUndoContext undoContext = modelRoot.getEnv().getUndoContext();
        
        
        XMLModelNotifierTest notifier = new XMLModelNotifierTest();
		XMLModelNotifierWrapper modelWrapper = new XMLModelNotifierWrapper(notifier, modelRoot);
		
		IUndoableOperation[] undoHistoryBefore = modelRoot.getEnv().getOperationHistory().getUndoHistory(undoContext);

		// here Schema TNS will be changed
		// exception will be throw if the wrapper does not wrap the call in emf command
		modelWrapper.endChanging();
		
		IUndoableOperation[] undoHistoryAfter = modelRoot.getEnv().getOperationHistory().getUndoHistory(undoContext);
		
		assertEquals("Wrapper should not record undo/redo operations.", 
				undoHistoryBefore.length, undoHistoryAfter.length);
		
		assertEquals(TNS_HELLO, modelRoot.getSchema().getComponent().getTargetNamespace());
	}
	
	private class XMLModelNotifierTest implements XMLModelNotifier {
		

		public void attrReplaced(Element element, Attr newAttr, Attr oldAttr) {
			// TODO Auto-generated method stub
			
		}

		public void beginChanging() {
			// TODO Auto-generated method stub
			
		}

		public void beginChanging(boolean newModel) {
			// TODO Auto-generated method stub
			
		}

		public void cancelPending() {
			// TODO Auto-generated method stub
			
		}

		public void childReplaced(Node parentNode, Node newChild, Node oldChild) {
			// TODO Auto-generated method stub
			
		}

		public void editableChanged(Node node) {
			// TODO Auto-generated method stub
			
		}

		public void endChanging() {
			// If this edit is not in EMFOpearation an exception will be 
			// throw
			ISchema schema = modelRoot.getSchema();
			schema.getComponent().setTargetNamespace(TNS_HELLO);
		}

		public void endTagChanged(Element element) {
			// TODO Auto-generated method stub
			
		}

		public boolean hasChanged() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isChanging() {
			// TODO Auto-generated method stub
			return false;
		}

		public void propertyChanged(Node node) {
			// TODO Auto-generated method stub
			
		}

		public void startTagChanged(Element element) {
			// TODO Auto-generated method stub
			
		}

		public void structureChanged(Node node) {
			// TODO Auto-generated method stub
			
		}

		public void valueChanged(Node node) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
