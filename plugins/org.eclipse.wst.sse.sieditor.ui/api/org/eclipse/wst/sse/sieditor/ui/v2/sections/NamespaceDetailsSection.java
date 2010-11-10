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
package org.eclipse.wst.sse.sieditor.ui.v2.sections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;

public class NamespaceDetailsSection extends AbstractDetailsPageSection {

    private Text namespaceText;

    private boolean isNamespaceDirty;

    private LabeledControl<Text> namespaceControl;

    public NamespaceDetailsSection(IFormPageController controller, FormToolkit toolkit, IManagedForm managedForm) {
        super(controller, toolkit, managedForm);
    }

    @Override
    protected int getColumnsCount() {
        return 2;
    }

    @Override
    public void createContents(Composite parent) {
        final Section section = createSection(parent, Messages.NamespaceDetailsSection_section_title);

        FormToolkit toolkit = getToolkit();
        Composite clientComposite = toolkit.createComposite(section);
        section.setClient(clientComposite);
        setCompositeLayout(clientComposite);

        createNamespaceControls(toolkit, clientComposite);

        toolkit.paintBordersFor(clientComposite);
    }

    @Override
    public void refresh() {
        ISchema schema = (ISchema) getModelObject();
        namespaceText.setText(NamespaceNode.getNamespaceDisplayText(schema));
        namespaceText.setEditable(isEditable());
        isNamespaceDirty = false;
    }

    private void createNamespaceControls(FormToolkit toolkit, Composite clientComposite) {

        namespaceControl = new LabeledControl<Text>(toolkit, clientComposite, Messages.NamespaceDetailsSection_namespace_label);
        namespaceControl.setControl(toolkit.createText(clientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));
        namespaceText = namespaceControl.getControl();
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(namespaceText);

        namespaceText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String namespace = ((ISchema) getModelObject()).getNamespace();
                if (!isNamespaceDirty && !namespaceText.getText().trim().equals(namespace)) {
                    isNamespaceDirty = true;
                    dirtyStateChanged();
                }
            }
        });
        namespaceText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isNamespaceDirty) {
                    ISchema schema = (ISchema) getModelObject();
                    String namespace = schema.getNamespace();
                    String namespaceTextValue = namespaceText.getText().trim();
                    if (!namespaceTextValue.equals(namespace)) {
                        ((IDataTypesFormPageController) getController()).renameNamespace(schema, namespaceTextValue);
                    }
                    isNamespaceDirty = false;
                    dirtyStateChanged();
                }
            }
        });
        namespaceText.addKeyListener(new CarriageReturnListener());
    }

    protected ISharedImages getSharedImages() {
        return PlatformUI.getWorkbench().getSharedImages();
    }

    @Override
    public boolean isDirty() {
        return isNamespaceDirty;
    }

    @Override
    public boolean isStale() {
        ISchema modelObject = (ISchema) getModelObject();
        if (modelObject == null) {
            return false;
        }
        String namespaceTextValue = namespaceText.getText().trim();
        return !namespaceTextValue.equals(modelObject.getNamespace());
    }
    
    
    @Override
    protected Text getDefaultControl() {
    	return namespaceText;
    }
}
