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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IDocSectionContainer;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;

public class ParameterDetailsPage extends AbstractDetailsPage implements IDocSectionContainer {

    private static final int NUMBER_OF_COLUMNS_PER_ROW = 3;

    protected Text nameTextControl;

    protected TypePropertyEditor typeEditor;

    /**
     * @deprecated use the problemDecorator field
     */
    protected Label nameErrorLabel;

    protected DocumentationSection documentationSection;

    protected INamedObject input;

    private boolean isNameDirty;

    private boolean isDocDirty;

    protected LabeledControl<Text> nameControl;

    public ParameterDetailsPage(SIFormPageController controller, ITypeDisplayer typeDisplayer) {
        super(controller);
        this.typeEditor = new ParameterTypeEditor(controller, typeDisplayer);
    }

    @Override
    protected void createSections(Composite parent) {
        createGeneralSection(parent);
        documentationSection = new DocumentationSection(this);
        documentationSection.createSection(parent, getToolkit());
    }

    private Section createGeneralSection(Composite parent) {
        FormToolkit toolkit = getToolkit();

        final Section generalSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        generalSection.setText(Messages.ParameterDetailsPage_general_section_title);
        generalSection.setExpanded(true);

        Composite generalClientComposite = toolkit.createComposite(generalSection);
        generalSection.setClient(generalClientComposite);

        GridLayout generalClientCompositeLayout = setGeneralSectionCompositeLayout(generalClientComposite);

        nameControl = new LabeledControl<Text>(toolkit, generalClientComposite, Messages.ParameterDetailsPage_name_label);
        nameControl.setControl(toolkit.createText(generalClientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));
        getProblemDecorator().bind(WSDLPackage.Literals.PART__NAME, nameControl);
        getProblemDecorator().bind(WSDLPackage.Literals.PART__TYPE_DEFINITION, typeEditor);
        getProblemDecorator().bind(WSDLPackage.Literals.MESSAGE_REFERENCE__EMESSAGE, typeEditor);

        nameTextControl = nameControl.getControl();
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(nameTextControl);

        nameTextControl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!isNameDirty && !nameTextControl.getText().trim().equals(input.getName())) {
                    isNameDirty = true;
                    dirtyStateChanged();
                }
            }
        });
        nameTextControl.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                if (isNameDirty && !nameTextControl.getText().trim().equals(input.getName())) {
                    ((SIFormPageController) getController()).editItemNameTriggered(treeNode, nameTextControl.getText());
                    isNameDirty = false;
                    dirtyStateChanged();
                }
            }
        });
        nameTextControl.addKeyListener(new CarriageReturnListener());

        int numberofColumnsLeft = generalClientCompositeLayout.numColumns - NUMBER_OF_COLUMNS_PER_ROW - 1;
        if (numberofColumnsLeft > 0) {
            GridData gridData = new GridData();
            gridData.horizontalSpan = numberofColumnsLeft;
            toolkit.createLabel(generalClientComposite, UIConstants.EMPTY_STRING, SWT.NONE).setLayoutData(gridData);
        }

        typeEditor.createControl(toolkit, generalClientComposite);
        getProblemDecorator().bind(WSDLPackage.Literals.PART__TYPE_DEFINITION, typeEditor);

        toolkit.paintBordersFor(generalClientComposite);

        return generalSection;
    }

    private GridLayout setGeneralSectionCompositeLayout(Composite generalClientComposite) {
        GridLayout generalClientCompositeLayout = new GridLayout();
        generalClientCompositeLayout.marginTop = 0;
        generalClientCompositeLayout.marginBottom = 5;
        generalClientCompositeLayout.marginLeft = 5;
        generalClientCompositeLayout.marginRight = 5;
        generalClientCompositeLayout.verticalSpacing = 5; // set in order the
        // controls to be
        // close positioned
        // like in
        // the PDE editor
        generalClientCompositeLayout.horizontalSpacing = 10; // 10 pixels to
        // show properly
        // error decoration
        // icons
        generalClientComposite.setLayout(generalClientCompositeLayout);

        generalClientCompositeLayout.numColumns = Math.max(NUMBER_OF_COLUMNS_PER_ROW, typeEditor.getNumberOfColumns());

        return generalClientCompositeLayout;
    }

    // @Override
    public void initialize(IManagedForm form) {
        super.initialize(form);
        typeEditor.initialize(form);
    }

    // @Override
    public boolean isDirty() {
        return typeEditor.isDirty() || isNameDirty || isDocDirty;
    }

    // @Override
    public boolean isStale() {
    	
        if (treeNode == null || treeNode.getDisplayName() == null) {
            return false;
        }

        return super.isStale() ||
        		!(treeNode.getDisplayName().equals(nameTextControl.getText().trim())) ||
                typeEditor.isStale() ||
                !input.getDocumentation().equals(documentationSection.getDocumentationText());
        // if
        // the type has been changed or if the initial type does not match the
        // current Model Object's // TODO an optimisation of the case where the
        // user has changed the type manually and after that, // but before the
        // change commitment the model's type is changed
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
                if (firstElement instanceof ParameterNode) {
                    treeNode = (ParameterNode) firstElement;
                    input = (IParameter) treeNode.getModelObject();
                    typeEditor.setInput(treeNode);
                    getProblemDecorator().setModelObject(input);
                }
            }
        } else {
            input = null;
            typeEditor.setInput(null);
        }
        updateWidgets();
    }

    protected void updateWidgets() {
        nameTextControl.setText(UIUtils.getDisplayName(input));
        nameTextControl.setEditable(!isReadOnly());
        typeEditor.update();

        // documentationTexts.setText(input.getDocumentation());
        documentationSection.update(input.getDocumentation());
        isNameDirty = false;
        isDocDirty = false;

        getProblemDecorator().updateDecorations();
    }

    public void documentationTextFocusLost() {
        if (isDocDirty
                && !documentationSection.getDocumentationText().trim().equals(treeNode.getModelObject().getDocumentation())) {
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
