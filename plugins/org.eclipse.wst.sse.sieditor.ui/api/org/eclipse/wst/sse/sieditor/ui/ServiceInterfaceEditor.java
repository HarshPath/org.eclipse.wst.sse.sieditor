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
package org.eclipse.wst.sse.sieditor.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.factory.ServiceInterfaceModelRootFactory;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.validation.ESMModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.EsmXsdModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ValidationListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;

public class ServiceInterfaceEditor extends AbstractEditorWithSourcePage {

    public static final String EDITOR_ID = "org.eclipse.wst.sse.sieditor.ui.sieditor"; //$NON-NLS-1$

    private ESMModelAdapter modelAdapter;

    @Override
    public IWsdlModelRoot getModelRoot() {
        return (IWsdlModelRoot) commonModel;
    }

    @Override
    protected IModelRoot createModelRoot(final Document document) {
        return ServiceInterfaceModelRootFactory.instance().createModelRoot(document);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void reloadModelFromDOM() {
        EmfModelPatcher.instance().patchEMFModelAfterDomChange(getModelRoot(), modelNotifier.getChangedNodes());
    }

    @Override
    protected void addExtraPages(final IStorageEditorInput in) throws PartInitException {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) commonModel;
        final org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description = wsdlModelRoot.getDescription();
        final List<ISchema> schemas = description.getContainedSchemas();

        // validation stuff
        validationService = new ValidationService(commonModel.getEnv().getEditingDomain().getResourceSet(), getModelRoot());
        this.modelAdapter = new ESMModelAdapter(wsdlModelRoot);
        validationService.addModelAdapter(modelAdapter);
        validationService.addModelAdapter(new EsmXsdModelAdapter());
        commonModel.getEnv().addDisposable(validationService);

        final DataTypesEditorPage dtPage;
        final ServiceIntefaceEditorPage sifPage;

        dtPage = new DataTypesEditorPage(this);
        sifPage = new ServiceIntefaceEditorPage(this, dtPage);
        sifPage.setModel(commonModel, isReadOnly, false);
        dtPage.setModel(commonModel, isReadOnly, false);

        addPage(0, sifPage, in);
        addPage(1, dtPage, in);
        // wait for the pages to be initialised and than get the DT
        // controller and give the SI page a reference of it
        sifPage.setDTController(dtPage.getDTController());

        validationService.addValidationListener(createValidationListener(dtPage, sifPage));

        final List<IModelObject> validatedEntitites = new ArrayList<IModelObject>(schemas.size() + 1);
        validatedEntitites.add(description);
        validatedEntitites.addAll(schemas);
    }

    private ValidationListener createValidationListener(final DataTypesEditorPage dtPage, final ServiceIntefaceEditorPage sifPage) {
        final List<AbstractEditorPage> pages = new LinkedList<AbstractEditorPage>();
        pages.add(sifPage);
        pages.add(dtPage);
        return new ValidationListener(pages);
    }

    protected org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription getDescription() {
        return ((IWsdlModelRoot) commonModel).getDescription();
    }

    @Override
    protected void validate() {
        final org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description = getDescription();

        final List<ISchema> schemas = description.getContainedSchemas();
        final List<IModelObject> validatedEntitites = new ArrayList<IModelObject>(schemas.size() + 1);
        validatedEntitites.add(description);
        validatedEntitites.addAll(schemas);

        validate(validatedEntitites);
    }

    @Override
    protected void updateValidator() {
        super.updateValidator();
        modelAdapter.setModelRoot((IWsdlModelRoot) commonModel);

    }

}
