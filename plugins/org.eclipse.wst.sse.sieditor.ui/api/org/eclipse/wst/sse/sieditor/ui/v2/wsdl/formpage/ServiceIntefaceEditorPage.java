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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeContextMenuListener;

public class ServiceIntefaceEditorPage extends AbstractServiceInterfaceEditorPage {

    public static final String SERVICE_INTERFACE_PAGE_TEXT = Messages.ServiceIntefaceEditorPage_tab_title_service_interfaces;

    public static final String ID = "org.eclipse.wst.sse.sieditor.ServiceInterfaceEditorPage";//$NON-NLS-1$
    /**
     * a flag field used to determine if the namespace text control content
     * differs from the model's definition target namespace
     */
    private boolean isNsDirty;

    protected volatile boolean nsBeingSet;
    private final ITypeDisplayer typeDisplayer;
    private boolean isInitialized;
    private boolean isDisposed = false;

    public ServiceIntefaceEditorPage(final FormEditor editor, final ITypeDisplayer typeDisplayer) {
        super(editor, ID, SERVICE_INTERFACE_PAGE_TEXT);
        this.typeDisplayer = typeDisplayer;
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) {
        if (!isInitialized) {
            super.init(site, input);
            controller = createController(input);
            controller.setEditorInput(input);
            controller.addEventListener(this);
            masterDetailsBlock.setController(controller);
            getSIMasterDetailsBlock().setTypeDisplayer(typeDisplayer);
            modelRoot.addChangeListener(this);
            isInitialized = true;
        }
    }

    protected SIFormPageController createController(final IEditorInput editorInput) {
        return new SIFormPageController(getWsdlModelRoot(), readOnly, false);
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
    protected void postContentCreated() {

        masterDetailsBlock.getTreeViewer().setInput(modelRoot);
        masterDetailsBlock.getTreeViewer().expandToLevel(2);

        createContextMenu();

        containingForm.setText(Messages.ServiceIntefaceEditorPage_form_page_title_service_interfaces);
        containingForm.setImage(Activator.getDefault().getImage(Activator.NODE_SI));

        // setErrorMessage("a sample error msg");
        clearFormMessage();

        // nameSpace editing controls placed under the form title
        if (nsErrorLabel != null) {
            nsErrorLabel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
            nsErrorLabel.setVisible(false);
        }
        final String namespace = getWsdlModelRoot().getDescription().getNamespace();
        if (namespaceTextControl != null) {
            if (namespace != null) {
                namespaceTextControl.setText(namespace);
            }
            namespaceTextControl.setEditable(!controller.isResourceReadOnly());
            namespaceTextControl.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    final String newNamespace = namespaceTextControl.getText().trim();
                    if (!isNsDirty && !newNamespace.equals(getWsdlModelRoot().getDescription().getNamespace())) {
                        isNsDirty = true;
                        firePropertyChange(PROP_DIRTY);
                    }
                }
            });
            namespaceTextControl.addFocusListener(new FocusListener() {
                public void focusLost(final FocusEvent e) {
                    final String newNamespace = namespaceTextControl.getText().trim();
                    if (isNsDirty && !newNamespace.equals(getWsdlModelRoot().getDescription().getNamespace()))
                        try {
                            nsBeingSet = true;
                            getSIFormPageController().editDescriptionNamespaceTriggered(newNamespace);
                        } finally {
                            nsBeingSet = false;
                            isNsDirty = false;
                            firePropertyChange(PROP_DIRTY);
                        }
                }

                public void focusGained(final FocusEvent e) {
                }
            });
            namespaceTextControl.addKeyListener(new CarriageReturnListener());
        }
        masterDetailsBlock.getTreeViewer().refresh();
    }

    @Override
    protected IMenuListener createContextMenuListener(final TreeViewer treeViewer) {
        return new SITreeContextMenuListener(getSIFormPageController(), treeViewer);
    }

    // delegates the event to the master part of the Master Details Block
    @Override
    public void componentChanged(final IModelChangeEvent event) {
        // sets the dirty flag to true - on any model change
        if (!isDirty) {
            setDirty(true);
            firePropertyChange(PROP_DIRTY);
        }

        if (event.getObject() instanceof IDescription) {
            if (namespaceTextControl != null) {
                final String namespace = ((IDescription) event.getObject()).getNamespace();
                if (namespace != null) { // if the namespace is changed
                    if (!namespace.equals(namespaceTextControl.getText().trim())) {
                        namespaceTextControl.setText(namespace);
                    }
                } else {
                    if (!UIConstants.EMPTY_STRING.equals(namespaceTextControl.getText().trim())) {
                        namespaceTextControl.setText(UIConstants.EMPTY_STRING);
                        // setErrorMessage("Namespace is empty");
                    }
                }
                namespaceTextControl.setEditable(!controller.isResourceReadOnly());
            }
            isNsDirty = false;
        }
        masterDetailsBlock.componentChanged(event);
        getManagedForm().refresh();
    }

    public void notifyEvent(final ISIEvent event) {
        switch (event.getEventId()) {
        case ISIEvent.ID_SELECT_TREENODE:
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    masterDetailsBlock.getTreeViewer().setSelection(new StructuredSelection(event.getEventParams()));
                }
            });
            break;
        case ISIEvent.ID_REFRESH_INPUT:
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    masterDetailsBlock.getTreeViewer().setInput(modelRoot);
                    componentChanged(new IModelChangeEvent() {
                        public IModelObject getObject() {
                            return getWsdlModelRoot().getDescription();
                        }
                    });
                    masterDetailsBlock.getTreeViewer().expandToLevel(2);
                }
            });
            break;
        case ISIEvent.ID_REFRESH_TREE_NODE:
            for (final Object selectedElement : event.getEventParams()) {
                getMasterDetailsBlock().getTreeViewer().refresh(selectedElement, true);
            }
            break;
        case ISIEvent.ID_REFRESH_TREE:
            masterDetailsBlock.getTreeViewer().refresh();
            break;
        case ISIEvent.ID_EDIT_TREENODE:
            masterDetailsBlock.setDetailsPartFocus();
            break;
        case ISIEvent.ID_TREE_NODE_EXPAND:
            expandTreeNode(event);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean isDirty() {
        return isDirty || isNsDirty || super.isDirty();
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
    }

    /**
     * Removes the message (if any), which is displayed next to the form title
     */
    public void clearFormMessage() {
        containingForm.setMessage(UIConstants.EMPTY_STRING, IMessageProvider.NONE);
    }

    /**
     * Sets an error message displayed next to the FormPage title
     * 
     * @param errorMsg
     */
    public void setErrorMessage(final String errorMsg) {
        containingForm.setMessage(errorMsg, IMessageProvider.ERROR);
    }

    /**
     * Defines the drag behaviour of the first page's title
     */
    public void setTitleDragSupport(final int operations, final Transfer[] transferTypes, final DragSourceListener listener) {
        containingForm.addTitleDragSupport(operations, transferTypes, listener);
    }

    /**
     * Defines the drop behaviour of the first page's title
     */
    public void setTitleDropSupport(final int operations, final Transfer[] transferTypes, final DropTargetListener listener) {
        containingForm.addTitleDropSupport(operations, transferTypes, listener);
    }

    public void setDTController(final IDataTypesFormPageController dtController) {
        ((SIFormPageController) controller).setDTController(dtController);
    }

}
