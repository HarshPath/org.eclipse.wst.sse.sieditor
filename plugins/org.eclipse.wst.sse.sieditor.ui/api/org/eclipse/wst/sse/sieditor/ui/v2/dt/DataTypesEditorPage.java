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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;

public class DataTypesEditorPage extends AbstractEditorPage implements ITypeDisplayer {

    public static final String ID = "org.eclipse.wst.sse.sieditor.DataTypeEditorPage"; //$NON-NLS-1$

    private ScrolledForm form;

    private boolean isInitialized;

    private boolean isDisposed = false;

    public DataTypesEditorPage(final FormEditor editor) {
        super(editor, ID, Messages.DataTypesEditorPage_form_title);
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        form = managedForm.getForm();
        form.setText(Messages.DataTypesEditorPage_form_title);
        form.setImage(Activator.getDefault().getImage(Activator.DATA_TYPES_TITLE_IMAGE));

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(form.getForm());

        createMasterDetailsBlock(managedForm);
        createContextMenu();

    }

    protected void createMasterDetailsBlock(final IManagedForm managedForm) {
        masterDetailsBlock = new DataTypesMasterDetailsBlock(this);
        getMasterDetailsBlock().setController(controller);
        getMasterDetailsBlock().createContent(managedForm);
        if (!(modelRoot instanceof IXSDModelRoot)) {
            getMasterDetailsBlock().getTreeViewer().expandToLevel(2);
        }
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) {
        super.init(site, input);
        if (!isInitialized) {
            controller = createController(input);
            controller.setEditorInput(input);
            controller.addEventListener(this);
            modelRoot.addChangeListener(this);
            isInitialized = true;
        }
    }

    protected DataTypesFormPageController createController(final IEditorInput input) {
        if (modelRoot instanceof IWsdlModelRoot) {
            return new SiEditorDataTypesFormPageController((IWsdlModelRoot) modelRoot, readOnly);
        }
        return new DataTypesFormPageController(modelRoot, readOnly);
    }

    @Override
    protected IMenuListener createContextMenuListener(final TreeViewer treeViewer) {
        return new DTTreeContextMenuListener(getDTController(), treeViewer);
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        super.dispose();
        isDisposed = true;
    }

    @Override
    public void componentChanged(final IModelChangeEvent event) {
        getMasterDetailsBlock().componentChanged(event);
        if (!isDirty) {
            isDirty = true;
            firePropertyChange(PROP_DIRTY);
        }

        getManagedForm().refresh();
    }

    public void notifyEvent(final ISIEvent event) {
        switch (event.getEventId()) {
        case ISIEvent.ID_ERROR_MSG:
            setErrorMessage((String) event.getEventParams()[0]);
            break;
        case ISIEvent.ID_SELECT_TREENODE:
            getMasterDetailsBlock().getTreeViewer().setSelection(new StructuredSelection(event.getEventParams()));
            break;
        case ISIEvent.ID_TREE_NODE_EXPAND:
            expandTreeNode(event);
            break;
        case ISIEvent.ID_REFRESH_INPUT:
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    getMasterDetailsBlock().getTreeViewer().setInput(modelRoot);
                    componentChanged(new IModelChangeEvent() {
                        public IModelObject getObject() {
                            return modelRoot instanceof IWsdlModelRoot ? ((IWsdlModelRoot) modelRoot).getDescription()
                                    : ((IXSDModelRoot) modelRoot).getSchema();
                        }
                    });
                    getMasterDetailsBlock().getTreeViewer().expandToLevel(2);
                }
            });
            break;
        case ISIEvent.ID_REFRESH_TREE_NODE:
            for (final Object selectedElement : event.getEventParams()) {
                getMasterDetailsBlock().getTreeViewer().refresh(selectedElement, true);
            }
            break;
        case ISIEvent.ID_EDIT_TREENODE:
            masterDetailsBlock.setDetailsPartFocus();
            break;
        default:
            break;
        }
    }

    /**
     * Sets an error message displayed next to the FormPage title
     * 
     * @param errorMsg
     */
    public void setErrorMessage(final String errorMsg) {
        form.setMessage(errorMsg, IMessageProvider.ERROR);
    }

    @Override
    public boolean isDirty() {
        return isDirty || getManagedForm().isDirty();
    }

    /**
     * Sets the dirtyFlag of this editor to the given param value. <br/>
     * The main purpose of this method is for the containing MultiPage editor to
     * be able to set the page's state.
     * 
     * @param isDirty
     *            the dirty flag state
     * @return
     */
    public void setDirty(final boolean isDirty) {
        this.isDirty = isDirty;
        if (!isDirty) {
            getManagedForm().refresh();
        }
    }

    public void showType(final IType type) {
        getEditor().setActiveEditor(this);
        ((DataTypesMasterDetailsBlock) getMasterDetailsBlock()).showType(type);
    }

    public IDataTypesFormPageController getDTController() {
        return (IDataTypesFormPageController) controller;
    }

}
