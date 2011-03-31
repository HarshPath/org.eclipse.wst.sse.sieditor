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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

public class ImportSchemaCommandXSDTest extends AbstractXSDCommandTest {
	
	protected static final String XSD_FILE = "MySchema.xsd";
	private IFile mySchema1_2File;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		ResourceUtils.copyFileIntoTestProject(
				"pub/xsd/same_ns/MySchema1.xsd", 
				Document_FOLDER_NAME, 
				this.getProject(),
                "MySchema1.xsd");		
		mySchema1_2File = ResourceUtils.copyFileIntoTestProject(
				"pub/xsd/same_ns/MySchema1_2.xsd", 
				Document_FOLDER_NAME, 
				this.getProject(),
                "MySchema1_2.xsd");
	}


    @Override
    protected void assertPostRedoState(IStatus redoStatus, IXSDModelRoot modelRoot) {
    	ISchema mySchema = modelRoot.getSchema();
    	
    	int numberOfImports = 0;
    	for(XSDSchemaContent content : mySchema.getComponent().getContents()) {
    		if(content instanceof XSDImport &&
    				((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
    			numberOfImports++;
    		}
    	}
    	assertEquals(2, numberOfImports);
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IXSDModelRoot modelRoot) {
    	ISchema mySchema = modelRoot.getSchema();
    	
    	int numberOfImports = 0;
    	for(XSDSchemaContent content : mySchema.getComponent().getContents()) {
    		if(content instanceof XSDImport &&
    				((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
    			numberOfImports++;
    		}
    	}
    	assertEquals(1, numberOfImports);
    }

    @Override
    protected AbstractNotificationOperation getOperation(IXSDModelRoot modelRoot) throws Exception {
        ISchema mySchema = modelRoot.getSchema();
    	
    	int numberOfImports = 0;
    	for(XSDSchemaContent content : mySchema.getComponent().getContents()) {
    		if(content instanceof XSDImport &&
    				((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
    			numberOfImports++;
    		}
    	}
    	assertEquals(1, numberOfImports);
        
        ISchema mySchema1_2 = this.getXSDModelRoot(mySchema1_2File).getSchema();
        AbstractType sch2Element1 = (AbstractType) mySchema1_2.getType(true, "Sch2Element1");
        
		ImportSchemaCommand command = new ImportSchemaCommand((IXSDModelRoot)mySchema.getModelRoot(), (Schema)mySchema, sch2Element1);
        return command;
    }

    @Override
    protected String getXSDFilename() {
        return XSD_FILE;
    }

    @Override
    protected String getXSDFoldername() {
        return "pub/xsd/same_ns/";
    }
}
