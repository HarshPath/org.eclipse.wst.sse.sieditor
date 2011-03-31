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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.core.search.scope.SearchScopeImpl;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ISIComponentSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeResolver;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeResolverFactory;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class CreateSearchListProviderTests extends SIEditorBaseTest {

    private List<ISchema> schemas;

    private XSDSchema[] xsdSchemas;

    private IFile file;

    private IWsdlModelRoot modelRoot;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        file = ResourceUtils.copyFileIntoTestProject("pub/self/mix2/PurchaseOrderConfirmation.wsdl", Document_FOLDER_NAME, this
                .getProject(), "AbstractFormPageControllerTest.wsdl");
        refreshProjectNFile(file);

        modelRoot = getWSDLModelRoot(file);

        schemas = modelRoot.getDescription().getContainedSchemas();

        ITypeResolver typeResolver = TypeResolverFactory.getInstance().createTypeResolver(modelRoot.getDescription());

        xsdSchemas = typeResolver.getLocalSchemas();
    }

    @Test
    public void testAttributes() {
        IStructureType type = (IStructureType) schemas.get(0).getType(false, "Amount");
        IElement attribute = type.getElements("currencyCode").iterator().next();

        doTest(true, attribute, XSDSimpleTypeDefinition.class);
    }

    @Test
    public void testElements() {
        IStructureType type = (IStructureType) schemas.get(0).getType(false, "Address");
        IElement element = type.getElements("OrganisationFormattedName").iterator().next();

        doTest(true, element, XSDComplexTypeDefinition.class, XSDSimpleTypeDefinition.class, XSDElementDeclaration.class);
    }

    @Test
    public void testSimpleTypes() {
        ISimpleType type = (ISimpleType) schemas.get(0).getType(false, "ActionCode");
        doTest(true, type, XSDSimpleTypeDefinition.class);
    }

    @Test
    public void testComplexTypesNonXSDElements() {
        IStructureType type = (IStructureType) schemas.get(0).getType(false, "Address");
        doTest(true, type, XSDComplexTypeDefinition.class, XSDSimpleTypeDefinition.class);
    }

    @Test
    public void testComplexTypesXSDElements() {
        IStructureType type = (IStructureType) schemas.get(0).getType(true, "PurchaseOrderConfirmationRequestResponse");
        doTest(true, type, XSDComplexTypeDefinition.class, XSDSimpleTypeDefinition.class);
    }

    @Test
    public void testBaseTypes() {
        IStructureType type = (IStructureType) schemas.get(0).getType(false, "Address");
        IElement element = type.getElements("OrganisationFormattedName").iterator().next();

        doTest(false, element, XSDSimpleTypeDefinition.class, XSDElementDeclaration.class);
    }

    private void doTest(boolean showComplexTypes, IModelObject type, Class<?>... classes) {
        TestFormPageController controller = new TestFormPageController(modelRoot, false);
        IComponentSearchListProvider provider = controller.createSearchListProvider(type, file, xsdSchemas, showComplexTypes);

        IComponentList componentList = new ComponentList();
        provider.populateComponentList(componentList, new SearchScopeImpl(), null);

        Set<Class<?>> hasSet = new HashSet<Class<?>>();

        for (Iterator<?> i = componentList.iterator(); i.hasNext();) {
            Object obj = i.next();
            Class<?> objClass = obj.getClass();
            boolean expected = false;

            for (Class<?> clazz : classes) {
                if (clazz.isAssignableFrom(objClass)) {
                    hasSet.add(clazz);
                    expected = true;
                    break;
                }
            }

            if (!expected) {
                fail(obj.getClass().getSimpleName() + " was not expected!");
            }
        }

        for (Class<?> clazz : classes) {
            Assert.assertTrue("Instances of " + clazz.getSimpleName() + " were expected, but none was found!", hasSet
                    .contains(clazz));
        }

    }

    private static class TestFormPageController extends AbstractFormPageController {
        public TestFormPageController(IModelRoot model, boolean readOnly) {
            super(model, readOnly);
        }

        public ISIComponentSearchListProvider createSearchListProvider(IModelObject selectedModelObject, IFile contextFile,
                XSDSchema[] schemas, boolean showComplexTypes) {
            return super.createSearchListProvider(selectedModelObject, contextFile, schemas, showComplexTypes);
        }

        @Override
        protected IModelObject getModelObject() {
            return null;
        }

        @Override
        public void editItemNameTriggered(ITreeNode treeNode, String newName) {
        }

        @Override
        protected String getEditorID() {
            return null;
        }

        @Override
        protected ITreeNode getNextTreeNode(ITreeNode selectedTreeNode) {
            return null;
        }

    }

    private static class ComponentList implements IComponentList {

        private List<Object> list;

        public ComponentList() {
            list = new ArrayList<Object>();
        }

        @Override
        public void add(Object o) {
            list.add(o);

        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator iterator() {
            return list.iterator();
        }

    }
}
