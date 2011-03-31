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

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

public class SetElementTypeCommandXSDTest extends AbstractXSDCommandTest {
        
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
    protected void assertPostRedoState(final IStatus redoStatus, final IXSDModelRoot modelRoot) {
        final ISchema mySchema = modelRoot.getSchema();
        
        final StructureType element2 = (StructureType)mySchema.getType(true, "Element2");
        final Iterator<IElement> element2Elements = element2.getElements("Sch2Element1").iterator();
        assertTrue("type 'Sch2Element1' is expected here.", element2Elements.hasNext());
        
        final Element xsdElement = element2.getElements("Sch2Element1").iterator().next().getComponent().getElement();
        
        assertTrue("Element2 must has 'ref' to Sch2Element1", xsdElement.getAttribute("ref").length() > 0);
        assertEquals("Element2 must not has 'type' xml attribute.", null, xsdElement.getAttribute("type"));
        assertEquals("Element2 must not has 'name' xml attribute.", null, xsdElement.getAttribute("name"));
        
        int numberOfImports = 0;
        for(final XSDSchemaContent content : mySchema.getComponent().getContents()) {
                if(content instanceof XSDImport &&
                                ((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
                        numberOfImports++;
                }
        }
        assertEquals(2, numberOfImports);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IXSDModelRoot modelRoot) {
        final ISchema mySchema = modelRoot.getSchema();
        final StructureType element2 = (StructureType)mySchema.getType(true, "Element2");
        final Iterator<IElement> element2Elements = element2.getElements("string").iterator();
        assertTrue("type 'string' is expected here.", element2Elements.hasNext());
        
        final Element xsdElement = element2.getElements("string").iterator().next().getComponent().getElement();
        
        assertEquals("Element2 must not has 'ref' to Sch2Element1", null, xsdElement.getAttribute("ref"));
        assertTrue("Element2 must has 'type' xml attribute.", xsdElement.getAttribute("type").length() > 0);
        assertTrue("Element2 must has 'name' xml attribute.", xsdElement.getAttribute("name").length() > 0);
        
        int numberOfImports = 0;
        for(final XSDSchemaContent content : mySchema.getComponent().getContents()) {
                if(content instanceof XSDImport &&
                                ((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
                        numberOfImports++;
                }
        }
        assertEquals(1, numberOfImports);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IXSDModelRoot modelRoot) throws Exception {
        final ISchema mySchema = modelRoot.getSchema();
        final StructureType element2 = (StructureType)mySchema.getType(true, "Element2");
        final IElement stringElement = element2.getElements("string").iterator().next();
        final IType stringType = stringElement.getType();
        
        assertEquals("string", stringType.getName());
        
        int numberOfImports = 0;
        for(final XSDSchemaContent content : mySchema.getComponent().getContents()) {
                if(content instanceof XSDImport &&
                                ((XSDImport) content).getNamespace().equals("http://example.com/MySchema1")) {
                        numberOfImports++;
                }
        }
        assertEquals(1, numberOfImports);
        
        final ISchema mySchema1_2 = this.getXSDModelRoot(mySchema1_2File).getSchema();
        final AbstractType sch2Element1 = (AbstractType) mySchema1_2.getType(true, "Sch2Element1");
        
                final SetElementTypeCommand command = new SetElementTypeCommand(mySchema.getModelRoot(), stringElement, sch2Element1);
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
