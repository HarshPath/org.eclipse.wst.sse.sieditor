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
import org.eclipse.xsd.XSDPackage;

import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

/**
 * @deprecated Use {@link StructureTypeDetailsSection} instead
 */
@Deprecated
public class StructureDetailsSection extends AbstractDetailsPageSection {

    protected Text nameText;

    private boolean isNameDirty;

    private LabeledControl<Text> nameControl;
    
    @Deprecated
    public StructureDetailsSection(final IFormPageController controller, final FormToolkit toolkit, final IManagedForm managedForm) {
        super(controller, toolkit, managedForm);
    }

    @Override
    public void createContents(final Composite parent) {
        final FormToolkit toolkit = getToolkit();
        final Section section = createSection(parent, toolkit);
        final Composite clientComposite = toolkit.createComposite(section);
        section.setClient(clientComposite);
        setCompositeLayout(clientComposite);

        createControls(toolkit, clientComposite);

        toolkit.paintBordersFor(clientComposite);
    }

    @Override
    protected int getColumnsCount() {
        return 2;
    }

    protected ISharedImages getSharedImages() {
        return PlatformUI.getWorkbench().getSharedImages();
    }

    protected void createControls(final FormToolkit toolkit, final Composite clientComposite) {

        nameControl = new LabeledControl<Text>(toolkit, clientComposite, Messages.StructureDetailsSection_name_label);
        nameControl.setControl(toolkit.createText(clientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));
        nameText = nameControl.getControl();
        getProblemDecorator().bind(XSDPackage.Literals.XSD_NAMED_COMPONENT__NAME, nameControl);

        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(nameText);

        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                final String nameTextTrimmed = nameText.getText().trim();
                if (!isNameDirty && !nameTextTrimmed.equals(utils().getDisplayName((INamedObject) getModelObject()))) {
                    isNameDirty = true;
                    dirtyStateChanged();
                }
            }
        });
        nameText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                if (isNameDirty) {
                    final IStructureType type = (IStructureType) getModelObject();
                    if (!nameText.getText().trim().equals(utils().getDisplayName(type))) {
                        ((IDataTypesFormPageController) getController()).rename(type, nameText.getText().trim());
                    }
                    isNameDirty = false;
                    dirtyStateChanged();
                }
            }
        });
        nameText.addKeyListener(new CarriageReturnListener());
    }

    private Section createSection(final Composite parent, final FormToolkit toolkit) {
        final Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        section.setText(Messages.StructureDetailsSection_section_title);
        section.setExpanded(true);
        return section;
    }

    @Override
    public boolean isDirty() {
        return isNameDirty;
    }

    @Override
    public boolean isStale() {
        final IStructureType type = (IStructureType) getModelObject();
        if (type == null) {
            return false;
        }
        final String nameTextValue = nameText.getText().trim();
        return !nameTextValue.equals(utils().getDisplayName(type));
    }

    @Override
    public void refresh() {
        final IStructureType type = (IStructureType) getModelObject();
        nameText.setText(utils().getDisplayName(type));
        nameText.setEditable(isEditable());
        isNameDirty = false;

        getProblemDecorator().updateDecorations();
    }

    @Override
    protected Text getDefaultControl() {
    	return nameText;
    }
    
    protected UIUtils utils() {
        return UIUtils.instance();
    }

}
