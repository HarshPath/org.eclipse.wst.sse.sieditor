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

import java.net.URI;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.impl.XSDImportImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.ResolveImportedSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class ResolveImportedSchemaCommandTest extends AbstractCommandTest {

    private static final String SCHEMA_FOR_IMPORT_NEW_NAME = "Imported_copy.xsd"; //$NON-NLS-1$
    private static final String SCHEMA_FOR_IMPORT = "pub/self/mix/Imported_copy.xsd"; //$NON-NLS-1$
    private static final String TARGET_NAMESPACE = "http://www.example.org/MultipleNamespaces/"; //$NON-NLS-1$
    private String importLocation;
    private XSDImport importElement;

    class ResetInportElementTestCommand extends AbstractXSDNotificationOperation{
        
        private final XSDImport elementToReset;

        public ResetInportElementTestCommand(final IXSDModelRoot modelRoot, final IModelObject schema, final XSDImport importElement){
            super(modelRoot,schema,"reset import element command for testing"); //$NON-NLS-1$
            this.elementToReset = importElement;
        }

        @Override
        public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            assertTrue(elementToReset instanceof XSDImportImpl);
            ((XSDImportImpl)elementToReset).reset();
            return Status.OK_STATUS;
        }
        
        @Override
        public boolean canUndo() {
            return false;
        }
        @Override
        public boolean canRedo() {
            return false;
        }
    }

    
    
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
/*        ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        Iterator<XSDSchemaContent> iterator = schema.getComponent().getContents().iterator();
        XSDImport importElement = null;
        while(iterator.hasNext()){
            XSDSchemaContent next = iterator.next();
            if(next instanceof XSDImport && importLocation.equals(((XSDImport)next).getSchemaLocation()) || ("./"+importLocation).equals(((XSDImport)next).getSchemaLocation())){ //$NON-NLS-1$
                importElement = (XSDImport) next;
                break;
            }
        }*/
        assertNotNull(importElement);
        assertNotNull(importElement.getResolvedSchema());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
/*        ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        Iterator<XSDSchemaContent> iterator = schema.getComponent().getContents().iterator();
        XSDImport importElement = null;
        while(iterator.hasNext()){
            XSDSchemaContent next = iterator.next();
            if(next instanceof XSDImport && importLocation.equals(((XSDImport)next).getSchemaLocation()) || ("./"+importLocation).equals(((XSDImport)next).getSchemaLocation())){ //$NON-NLS-1$
                importElement = (XSDImport) next;
                break;
            }
        }*/
        assertNotNull(importElement);
        assertNull(importElement.getResolvedSchema());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        ISchema importingSchema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot importingSchemaModelRoot = XSDFactory.getInstance().createXSDModelRoot(importingSchema.getComponent());
        importingSchema = importingSchemaModelRoot.getSchema();
        final IXSDModelRoot importedSchemaModelRoot = getXSDModelRoot(SCHEMA_FOR_IMPORT, SCHEMA_FOR_IMPORT_NEW_NAME);
        final ISchema importedSchema = importedSchemaModelRoot.getSchema();
       
        assertTrue(importedSchema instanceof Schema);

        final URI importURI = ((Schema)importedSchema).getContainingResource();;
        
        importLocation = ResourceUtils.makeRelativeLocation(importingSchema.getComponent().getSchemaLocation(), importURI);

        final Iterator<XSDSchemaContent> iterator = importingSchema.getComponent().getContents().iterator();
        
        importElement = null;
        while(iterator.hasNext()){
            final XSDSchemaContent next = iterator.next();
            if(next instanceof XSDImport && (importLocation.equals(((XSDImport)next).getSchemaLocation()) || ("./"+importLocation).equals(((XSDImport)next).getSchemaLocation()))){ //$NON-NLS-1$
                importElement = (XSDImport) next;
                break;
            }
        }
        assertNotNull(importElement);
        assertTrue(importElement instanceof XSDImportImpl);
        final ResetInportElementTestCommand command = new ResetInportElementTestCommand(importingSchemaModelRoot, importingSchema, importElement);
        assertTrue(modelRoot.getEnv().execute(command).isOK());
        return new ResolveImportedSchemaCommand((XSDImportImpl) importElement, importingSchemaModelRoot, importingSchema);
    }

    @Override
    protected String getWsdlFilename() {
        return "TypesReferringExternalSchema_copy.wsdl";
    }
    
    @Override
    protected String getWsdlFoldername() {
        // TODO Auto-generated method stub
        return Constants.DATA_PUBLIC_SELF_KESHAV;
    }
}
