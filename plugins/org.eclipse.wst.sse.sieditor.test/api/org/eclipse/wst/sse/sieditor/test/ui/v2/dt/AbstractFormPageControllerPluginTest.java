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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import static org.easymock.EasyMock.*;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 *
 * 
 */
public class AbstractFormPageControllerPluginTest extends SIEditorBaseTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testOpenType() {

        IFile file = null;
        try {
            file = ResourceUtils.copyFileIntoTestProject("pub/self/mix2/PurchaseOrderConfirmation.wsdl", Document_FOLDER_NAME,
                    this.getProject(), "AbstractFormPageControllerTest.wsdl");
            refreshProjectNFile(file);

            final IWsdlModelRoot modelRoot = getWSDLModelRoot(file);

            // pick some arbitrary lucky type from the schema
            List<ISchema> schemas = modelRoot.getDescription().getContainedSchemas();
            Collection<IType> types = schemas.get(0).getAllContainedTypes();
            IType type = types.iterator().next();
            XSDNamedComponent xsdType = type.getComponent();

            // simulate as if the dialog opens and returns the lucky type
            final ITypeSelectionDialogDelegate delegate = createMock(ITypeSelectionDialogDelegate.class);
            expect(delegate.open()).andReturn(true).once();
            expect(delegate.getSelectedObject()).andReturn(xsdType).once();
            replay(delegate);

            AbstractFormPageController controller = new AbstractFormPageController(modelRoot, false) {

                @Override
                protected IModelObject getModelObject() {
                    return modelRoot.getDescription();
                }

                @Override
                protected ITreeNode getNextTreeNode(ITreeNode selectedTreeNode) {
                    return null;
                }

                @Override
                protected ITypeSelectionDialogDelegate createTypeSelectionDialog(IFile editorFile, XSDSchema[] schemas,
                        String displayText, IModelObject selectedModelObject, boolean showComplexTypes) {
                    return delegate;
                }

                @Override
                public void editItemNameTriggered(ITreeNode treeNode, String newName) {
                    // TODO Auto-generated method stub

                }

                @Override
                protected String getEditorID() {
                    // TODO Auto-generated method stub
                    return null;
                }

       

      
            };

            controller.setEditorInput(new FileEditorInput(file));

            IType selectedType = controller.openTypesDialog();
            assertEquals(type, selectedType);

            verify(delegate);

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (CoreException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
