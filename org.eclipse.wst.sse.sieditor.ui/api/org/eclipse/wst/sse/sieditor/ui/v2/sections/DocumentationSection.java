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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

public class DocumentationSection extends AbstractDetailsPageSection {

    private static final int COLUMNS_COUNT = 1;

    protected Text documentationText;

    private boolean isDirty;

    public DocumentationSection(final IFormPageController controller, final FormToolkit toolkit, final IManagedForm managedForm) {
        super(controller, toolkit, managedForm);
    }

    @Override
    public void createContents(final Composite parent) {
        final FormToolkit toolkit = getToolkit();
        control = createSection(parent, toolkit);
        final Composite clientComposite = toolkit.createComposite(control);
        control.setClient(clientComposite);
        setCompositeLayout(clientComposite);

        createDocumentationControl(toolkit, clientComposite);

        toolkit.paintBordersFor(clientComposite);
    }

    @Override
    protected int getColumnsCount() {
        return COLUMNS_COUNT;
    }

    private void createDocumentationControl(final FormToolkit toolkit, final Composite clientComposite) {
        documentationText = toolkit
                .createText(clientComposite, UIConstants.EMPTY_STRING, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.minimumHeight = 100;
        documentationText.setLayoutData(layoutData);
        documentationText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                final String documentation = documentationText.getText();
                if (!isDirty && !documentation.equals(getModelObject().getDocumentation())) {
                    isDirty = true;
                    dirtyStateChanged();
                }
            }
        });
        documentationText.addFocusListener(new FocusListener() {
            public void focusLost(final FocusEvent e) {
                final String documentation = documentationText.getText();
                if (isDirty && documentation != null
                        && !documentation.equals(getModelObject() != null ? getModelObject().getDocumentation() : null)) {
                    getController().editDocumentation(getNode(), documentation);
                    isDirty = false;
                    dirtyStateChanged();
                }
            }

            public void focusGained(final FocusEvent e) {
            }
        });
    }

    private Section createSection(final Composite parent, final FormToolkit toolkit) {
        final Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        section.setText(Messages.DocumentationSection_section_title);
        section.setExpanded(true);
        return section;
    }

    @Override
    public void refresh() {
        documentationText.setText(getModelObject().getDocumentation());

        boolean editable = isEditable();
        if (!editable) {
            editable = isWritableElementReference();
        }
        documentationText.setEditable(editable);

        isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public boolean isStale() {
        final IModelObject modelObject = getModelObject();
        if (modelObject == null) {
            return false;
        }

        return !documentationText.getText().trim().equals(modelObject.getDocumentation());
    }
}