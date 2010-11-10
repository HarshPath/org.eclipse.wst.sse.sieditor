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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.factory.DataTypesModelRootFactory;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;
import org.eclipse.wst.sse.sieditor.model.validation.EsmXsdModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ValidationListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StandaloneDtEditorPage;

public class DataTypesEditor extends AbstractEditorWithSourcePage {

    public static final String EDITOR_ID = "org.eclipse.wst.sse.sieditor.ui.dteditor"; //$NON-NLS-1$

    @Override
    protected IModelRoot createModelRoot(final Document document) {
        return DataTypesModelRootFactory.instance().createModelRoot(document);
    }

    @Override
    protected void reloadModelFromDOM() {
        EmfModelPatcher.instance().patchEMFModelAfterDomChange(getModelRoot(), modelNotifier.getChangedNodes());
    }

    @Override
    protected void addExtraPages(final IStorageEditorInput in) throws PartInitException {
        try {
            validationService = new ValidationService(commonModel.getEnv().getEditingDomain().getResourceSet(), getModelRoot());
            commonModel.getEnv().addDisposable(validationService);
            validationService.addModelAdapter(new EsmXsdModelAdapter());

            final DataTypesEditorPage dtPage = new StandaloneDtEditorPage(this);
            dtPage.setModel(commonModel, isReadOnly, false);
            addPage(0, dtPage, in);

            validationService.addValidationListener(createValidationListener(dtPage));

        } catch (final CoreException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add pages to data types editor.", e);//$NON-NLS-1$
        }
    }

    private ValidationListener createValidationListener(final DataTypesEditorPage dtPage) {
        final List<AbstractEditorPage> pages = new LinkedList<AbstractEditorPage>();
        pages.add(dtPage);
        return new ValidationListener(pages);
    }

    @Override
    protected void validate() {
        validate(getAllSchemas());
    }

    private Set<ISchema> getAllSchemas() {
        final ISchema editedSchema = ((IXSDModelRoot) getModelRoot()).getSchema();
        final Set<ISchema> schemas = new HashSet<ISchema>(editedSchema.getAllReferredSchemas());
        schemas.remove(Schema.getSchemaForSchema());
        schemas.add(editedSchema);

        return schemas;
    }
}
