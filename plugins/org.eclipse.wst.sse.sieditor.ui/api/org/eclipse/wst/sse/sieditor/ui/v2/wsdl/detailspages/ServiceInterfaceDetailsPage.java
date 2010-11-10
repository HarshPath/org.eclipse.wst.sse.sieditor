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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IDocSectionContainer;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

public class ServiceInterfaceDetailsPage extends AbstractDetailsPage implements IDocSectionContainer {

    protected IServiceInterface input;

    protected Section generalSection;

    protected DocumentationSection documentationSection;

    protected Text nameTextControl;

    private boolean isNameDirty;

    private boolean isDocDirty;

    private LabeledControl<Text> nameControl;

    public ServiceInterfaceDetailsPage(SIFormPageController controller) {
        super(controller);
    }

    @Override
    protected void createSections(Composite parent) {
        createGeneralSection(parent);
        documentationSection = new DocumentationSection(this);
        documentationSection.createSection(parent, getToolkit());
    }

    private Section createGeneralSection(Composite parent) {
        FormToolkit toolkit = getToolkit();
        generalSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        generalSection.setText(Messages.ServiceInterfaceDetailsPage_general_section_title);
        generalSection.setExpanded(true);

        Composite generalClientComposite = toolkit.createComposite(generalSection);
        generalSection.setClient(generalClientComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10; // 10 pixels to show properly error
                                       // decoration icons
        generalClientComposite.setLayout(layout);

        nameControl = new LabeledControl<Text>(toolkit, generalClientComposite,
                Messages.ServiceInterfaceDetailsPage_general_name_label);
        nameControl.setControl(toolkit.createText(generalClientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));
        getProblemDecorator().bind(WSDLPackage.Literals.PORT_TYPE__QNAME, nameControl);
        nameTextControl = nameControl.getControl();
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(nameTextControl);

        toolkit.paintBordersFor(generalClientComposite);
        postGeneralSectionCreate();
        return generalSection;
    }

    private void postGeneralSectionCreate() {
        nameTextControl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!isNameDirty && !nameTextControl.getText().equals(input.getName())) {
                    isNameDirty = true;
                    dirtyStateChanged();
                }
            }
        });
        nameTextControl.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                if (isNameDirty && !nameTextControl.getText().equals(input.getName())) {
                    ((SIFormPageController) getController()).editItemNameTriggered(treeNode, nameTextControl.getText());
                    isNameDirty = false;
                    dirtyStateChanged();
                }
            }

            public void focusGained(FocusEvent e) {
            }
        });
        nameTextControl.addKeyListener(new CarriageReturnListener());
    }

    // @Override
    public boolean isDirty() {
        return isNameDirty || isDocDirty;
    }

    // @Override
    public boolean isStale() {
        return super.isStale() || 
        	treeNode != null && !(treeNode.getDisplayName().equals(nameTextControl.getText().trim())
                && treeNode.getModelObject().getDocumentation().equals(documentationSection.getDocumentationText()));
    }

    // @Override
    public void refresh() {
        updateWidgets();
    }

    // @Override
    public void selectionChanged(IFormPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection sSelection = (IStructuredSelection) selection;
            if (sSelection.size() == 1) {
                Object firstElement = sSelection.getFirstElement();
                if (firstElement instanceof ServiceInterfaceNode) {
                    treeNode = (ServiceInterfaceNode) firstElement;
                    input = (IServiceInterface) treeNode.getModelObject();
                    getProblemDecorator().setModelObject(input);
                }
            }
        } else {
            input = null;
        }
        updateWidgets();
    }

    protected void updateWidgets() {
        nameTextControl.setText(input.getName());
        nameTextControl.setEditable(!isReadOnly());
        documentationSection.update(input.getDocumentation());
        isNameDirty = false;
        isDocDirty = false;

        getProblemDecorator().updateDecorations();
    }

    public void documentationTextFocusLost() {
        if (isDocDirty && !documentationSection.getDocumentationText().equals(treeNode.getModelObject().getDocumentation())) {
            getController().editDocumentation(treeNode, documentationSection.getDocumentationText());
        }
        isDocDirty = false;
        dirtyStateChanged();
    }

    public void documentationTextModified() {
        if (!isDocDirty && !documentationSection.getDocumentationText().trim().equals(input.getDocumentation())) {
            isDocDirty = true;
            dirtyStateChanged();
        }
    }

    @Override
    protected Text getDefaultControl() {
    	return nameTextControl;
    }
}
