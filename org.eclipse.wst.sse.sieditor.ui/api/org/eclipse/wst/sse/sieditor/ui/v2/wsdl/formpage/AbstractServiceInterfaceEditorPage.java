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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public abstract class AbstractServiceInterfaceEditorPage extends AbstractEditorPage {

    protected Form containingForm;

    protected Label nsErrorLabel;
    protected Text namespaceTextControl;

    protected abstract void postContentCreated();

    public AbstractServiceInterfaceEditorPage(final FormEditor editor, final String id, final String title) {
        super(editor, id, title);
        masterDetailsBlock = new SIMasterDetailsBlock();
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        containingForm = (Form) managedForm.getForm().getContent();
        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(containingForm);

        createNsComposite(managedForm);
        masterDetailsBlock.createContent(managedForm);

        postContentCreated();
    }

    protected void createNsComposite(final IManagedForm managedForm) {
        final FormToolkit toolkit = managedForm.getToolkit();
        final Composite formBody = managedForm.getForm().getBody();
        final Composite nsComposite = new Composite(formBody, SWT.FLAT);
        nsComposite.setBackground(managedForm.getToolkit().getColors().getBackground());
        nsComposite.setLayout(new FormLayout());

        final FormText namespaceLabel = toolkit.createFormText(nsComposite, true);
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

        final GC gc = new GC(namespaceTextControl);
        formData.width = namespaceTextControl.computeSize(gc.getFontMetrics().getAverageCharWidth() * 50, SWT.DEFAULT).x;
        gc.dispose();
        namespaceTextControl.setLayoutData(formData);

        toolkit.paintBordersFor(nsComposite);
    }

    protected SIMasterDetailsBlock getSIMasterDetailsBlock() {
        return (SIMasterDetailsBlock) masterDetailsBlock;
    }

    protected IWsdlModelRoot getWsdlModelRoot() {
        return (IWsdlModelRoot) modelRoot;
    }

    public SIFormPageController getSIFormPageController() {
        return (SIFormPageController) controller;
    }
}