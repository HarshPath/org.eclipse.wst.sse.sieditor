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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

public class DocumentationSection {

    private IDocSectionContainer page;

    private Text documentationText;

    public DocumentationSection(IDocSectionContainer page) {
        this.page = page;
    }

    public Section createSection(Composite parent, FormToolkit toolkit) {
        final Section documentationSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        documentationSection.setText(Messages.ParameterDetailsPage_documentation_section_title);
        documentationSection.setExpanded(true);

        Composite descriptionClientComposite = toolkit.createComposite(documentationSection);

        documentationSection.setClient(descriptionClientComposite);
        descriptionClientComposite.setLayout(new FormLayout());

        documentationText = toolkit.createText(descriptionClientComposite, UIConstants.EMPTY_STRING, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 5);
        formData.top = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.height = 100;
        formData.bottom = new FormAttachment(100, -5);
        documentationText.setLayoutData(formData);

        documentationText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                page.documentationTextModified();
            }
        });
        documentationText.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                page.documentationTextFocusLost();
            }

            public void focusGained(FocusEvent e) {}
        });
        toolkit.paintBordersFor(descriptionClientComposite);
        return documentationSection;
    }

    public void update(String text) {
        documentationText.setText(text);
        documentationText.setEditable(!page.isReadOnly());
     }

    public String getDocumentationText() {
        return documentationText.getText().trim();
    }
}
