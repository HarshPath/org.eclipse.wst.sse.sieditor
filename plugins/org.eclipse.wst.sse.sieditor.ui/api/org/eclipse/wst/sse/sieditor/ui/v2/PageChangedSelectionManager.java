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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.ui.internal.util.WSDLEditorUtil;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;
import org.eclipse.wst.sse.sieditor.ui.view.impl.SISourceEditorPart;

public class PageChangedSelectionManager {
    private SISourceEditorPart sourcePage;

    public PageChangedSelectionManager(SISourceEditorPart sourcePage) {
        this.sourcePage = sourcePage;
    }

    private SISourceEditorPart getSourcePage() {
        return sourcePage;
    }

    public void performSelection(final int newPageIndex, final int currentPageIndex, List pages, IModelRoot modelRoot) {
        if (currentPageIndex == -1) {
            // e.g. there is no selected page already
            return;
        }

        final Object currentPage = pages.get(currentPageIndex);

        final Object newSelectedPage = pages.get(newPageIndex);

        if (newSelectedPage == sourcePage && currentPageIndex != newPageIndex) {
            AbstractEditorPage editorPage = (AbstractEditorPage) pages.get(currentPageIndex);
            final IStructuredSelection elementSelection = (IStructuredSelection) editorPage.getTreeViewer().getSelection();
            setSelectionInSourcePage(elementSelection);

        } else if (newSelectedPage instanceof ServiceIntefaceEditorPage && currentPage == sourcePage) {

            final IStructuredSelection elementSelectionInSource = getSourceSelection();

            setSelectionInSIPage((AbstractEditorPage) newSelectedPage, elementSelectionInSource, modelRoot);

        } else if (newSelectedPage instanceof DataTypesEditorPage && currentPage == sourcePage) {

            final IStructuredSelection elementSelectionInSource = getSourceSelection();

            if (modelRoot instanceof IWsdlModelRoot) {
                setSelectionInDTPage((AbstractEditorPage) newSelectedPage, elementSelectionInSource, modelRoot);
            } else {// in case of IXsdModelRoot
                setSelectionInDTPage((AbstractEditorPage) newSelectedPage, elementSelectionInSource, modelRoot);
            }

        }
    }

    private IStructuredSelection getSourceSelection() {
        return ((IStructuredSelection) getSourcePage().getSelectionProvider().getSelection());
    }

    private void setSelectionInDTPage(final AbstractEditorPage editorPage, final IStructuredSelection elementSelectionInSource,
            final IModelRoot modelRoot) {

        final Element selectedElement = getSelectedElement(elementSelectionInSource);
        if (selectedElement == null) {
            return;
        }

        Collection<ISchema> allContainedSchemas = new ArrayList<ISchema>();
        if (modelRoot instanceof IWsdlModelRoot) {// in case of IWsdlModelRoot
            final Description description = (Description) (modelRoot.getModelObject());
            allContainedSchemas.addAll(description.getContainedSchemas());
        } else { // in case of IXsdModelRoot
            final ISchema schema = (ISchema) (modelRoot.getModelObject());
            allContainedSchemas.add(schema);
        }

        final DataTypesFormPageController controller = (DataTypesFormPageController) editorPage.getController();

        boolean isSelectedElementIsSchema = isSchemaElement(selectedElement);

        for (ISchema currentSchema : allContainedSchemas) {
            if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(currentSchema.getNamespace())) {
                continue;
            }
            if (isSelectedElementIsSchema) {
                String tnsAttribute = selectedElement.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
                if (tnsAttribute==null || !tnsAttribute.equals(currentSchema.getNamespace())) {
                    continue;
                }
                ITreeNode nodeForSelection = controller.getTreeNodeMapper().getITreeNodeForXsdElement(currentSchema,
                        currentSchema.getComponent());
                if (nodeForSelection != null) {
                    setSelection(editorPage, nodeForSelection);
                    return;
                }
            }

            XSDConcreteComponent emfComponent = currentSchema.getComponent().getCorrespondingComponent(selectedElement);
            if ((emfComponent instanceof XSDSchema && !isSelectedElementIsSchema) || emfComponent == null
                    || emfComponent.eContainer() == null) {
                continue;
            }

            ITreeNode nodeForSelection = controller.getTreeNodeMapper().getITreeNodeForXsdElement(currentSchema, emfComponent);
            if (nodeForSelection != null) {
                setSelection(editorPage, nodeForSelection);
                return;
            }
        }
        return;
    }

    private void setSelection(final AbstractEditorPage editorPage, ITreeNode nodeForSelection) {
        final StructuredSelection selection = new StructuredSelection(nodeForSelection);
        editorPage.getTreeViewer().setSelection(selection);
    }

    private boolean isSchemaElement(final Element selectedElement) {
        String selectedElementTagName = selectedElement.getTagName();
        if (XSDConstants.SCHEMA_ELEMENT_TAG.equals(selectedElementTagName.substring(selectedElementTagName.indexOf(":") + 1))) {
            return true;
        }
        return false;
    }

    private void setSelectionInSIPage(final AbstractEditorPage editorPage, final IStructuredSelection elementSelectionInSource,
            final IModelRoot modelRoot) {
        final Element selectedElement = getSelectedElement(elementSelectionInSource);
        if (selectedElement == null) {
            return;
        }
        final Description description = (Description) (modelRoot.getModelObject());

        final SIFormPageController controller = (SIFormPageController) editorPage.getController();

        final Object emfObjectForSelection = WSDLEditorUtil.getInstance().findModelObjectForElement(description.getComponent(),
                selectedElement);

        if (!(emfObjectForSelection instanceof WSDLElement)) {
            return;
        }

        ITreeNode nodeForSelection = controller.getTreeNodeMapper().getITreeNodeForWsdlElement(description,
                (EObject) emfObjectForSelection, controller.getServiceInterfaceNodes(null, description));
        if (nodeForSelection != null) {
            setSelection(editorPage, nodeForSelection);
        }

        return;
    }

    private Element getSelectedElement(final IStructuredSelection elementSelectionInSource) {
        Object selectedElement = elementSelectionInSource.getFirstElement();
        if (selectedElement instanceof Attr) {
            selectedElement = ((Attr) selectedElement).getOwnerElement();
        }
        return selectedElement instanceof Element ? (Element) selectedElement : null;
    }

    protected void setSelectionInSourcePage(IStructuredSelection elementSelection) {
        if (elementSelection == null || elementSelection.isEmpty()) {
            return;
        }
        EObject eObject = ((ITreeNode) (elementSelection).getFirstElement()).getModelObject().getComponent();

        int indexInTheSource = -1;
        if (eObject instanceof WSDLElement) {
            indexInTheSource = EmfWsdlUtils.getIndexInSourcePage((WSDLElement) eObject);
        } else if (eObject instanceof XSDConcreteComponent) {
            indexInTheSource = EmfXsdUtils.getIndexInSourcePage((XSDConcreteComponent) eObject);
        } else {
            return;
        }

        if (indexInTheSource >= 0) {
            getSourcePage().selectAndReveal(indexInTheSource, 0);
        }
    }

}
