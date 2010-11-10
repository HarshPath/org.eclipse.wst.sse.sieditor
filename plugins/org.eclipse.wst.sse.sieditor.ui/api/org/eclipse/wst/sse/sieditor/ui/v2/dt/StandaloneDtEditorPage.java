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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;

/**
 * Extends the {@link DataTypesEditorPage} to provide UI control for the Schema
 * Namespace The Standalone Data Types Editor is to be used by the
 * {@link DataTypesEditor}. In this case only one Schema is present, therefore
 * it is shown in the UI
 * 
 * 
 * 
 */
public class StandaloneDtEditorPage extends DataTypesEditorPage {

    /**
     * a flag field used to determine if the namespace text control content
     * differs from the model's definition target namespace
     */
    private boolean isNsDirty;
    protected volatile boolean nsBeingSet;
    protected Label nsErrorLabel;
    protected Text namespaceTextControl;

    public StandaloneDtEditorPage(FormEditor editor) {
        super(editor);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        createNsComposite(managedForm);

        super.createFormContent(managedForm);

        initNs();

    }
    
    @Override
    public IXSDModelRoot getModel() {
    	return (IXSDModelRoot) super.getModel();
    }

    protected void initNs() {
        // nameSpace editing controls placed under the form title
        nsErrorLabel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
        nsErrorLabel.setVisible(false);
        String namespace = getModel().getSchema().getNamespace();
        namespace = NamespaceNode.getNamespaceDisplayText(namespace);
        if (NamespaceNode.NO_NS_STRING.equals(namespace)) {
            namespaceTextControl.setText(""); //$NON-NLS-1$
        } else {
            namespaceTextControl.setText(namespace);
        }
        namespaceTextControl.setEditable(!controller.isResourceReadOnly());
        namespaceTextControl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String newNamespace = namespaceTextControl.getText().trim();
                if (!isNsDirty && !newNamespace.equals(getModel().getSchema().getNamespace())) {
                    isNsDirty = true;
                    firePropertyChange(PROP_DIRTY);
                }
            }
        });
        namespaceTextControl.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                String newNamespace = namespaceTextControl.getText().trim();
                if (isNsDirty && !newNamespace.equals(getModel().getSchema().getNamespace()))
                    try {
                        nsBeingSet = true;
                        getDTController().renameNamespace(getModel().getSchema(), newNamespace);
                    } finally {
                        nsBeingSet = false;
                        isNsDirty = false;
                        firePropertyChange(PROP_DIRTY);
                    }
            }

            public void focusGained(FocusEvent e) {
            }
        });
        namespaceTextControl.addKeyListener(new CarriageReturnListener());
    }

    protected void createNsComposite(IManagedForm managedForm) {
        FormToolkit toolkit = managedForm.getToolkit();
        Composite formBody = managedForm.getForm().getBody();
        Composite nsComposite = new Composite(formBody, SWT.FLAT);
        nsComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        nsComposite.setLayout(new FormLayout());

        FormText namespaceLabel = toolkit.createFormText(nsComposite, true);
        namespaceLabel.setText(Messages.AbstractServiceInterfaceEditorPage_namespace_label, false, false);
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 5);
        formData.top = new FormAttachment(0, 13);
        formData.bottom = new FormAttachment(100, -5);
        namespaceLabel.setLayoutData(formData);

        nsErrorLabel = toolkit.createLabel(nsComposite, UIConstants.EMPTY_STRING, SWT.NONE);
        formData = new FormData();
        formData.left = new FormAttachment(namespaceLabel, 1);
        formData.top = new FormAttachment(0, 15);
        nsErrorLabel.setLayoutData(formData);

        namespaceTextControl = toolkit.createText(nsComposite, UIConstants.EMPTY_STRING, SWT.SINGLE);
        formData = new FormData();
        formData.left = new FormAttachment(nsErrorLabel, 1);
        formData.top = new FormAttachment(0, 13);
        formData.right = new FormAttachment(100, -5);

        GC gc = new GC(namespaceTextControl);
        formData.width = namespaceTextControl.computeSize(gc.getFontMetrics().getAverageCharWidth() * 50, SWT.DEFAULT).x;
        gc.dispose();
        namespaceTextControl.setLayoutData(formData);

        toolkit.paintBordersFor(nsComposite);
    }

    @Override
    public void componentChanged(IModelChangeEvent event) {
        if (nsBeingSet) {
            nsBeingSet = false;
            return;// so far no details page is using the Wsdl Definition NS -
            // so no update of the masterDetails block is necessary.
            // If this condition changes - remove the return statement
            // getManagedForm().refresh();
        }
        if (event.getObject() instanceof ISchema) {
            ISchema schema = (ISchema) event.getObject();
            String namespace = schema.getNamespace();
            // if the namespace is changed and the schema DOM element exists
            if (namespace != null && schema.getComponent().getElement() != null) {
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
            isNsDirty = false;
        }

        super.componentChanged(event);

    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || isNsDirty;
    }
}
