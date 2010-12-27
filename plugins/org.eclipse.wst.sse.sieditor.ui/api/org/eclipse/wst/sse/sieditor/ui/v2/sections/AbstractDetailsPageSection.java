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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public abstract class AbstractDetailsPageSection implements IDetailsPageSection {

    private static final int COLUMNS_COUNT = 2;

    private final IFormPageController controller;

    private IManagedForm managedForm;

    private final FormToolkit toolkit;

    protected ITreeNode node;

    protected Section control;

    protected IProblemDecorator problemDecorator;

    public AbstractDetailsPageSection(final IFormPageController controller, final FormToolkit toolkit,
            final IManagedForm managedForm) {
        this.controller = controller;
        this.toolkit = toolkit;
        this.managedForm = managedForm;
    }

    protected FormToolkit getToolkit() {
        return toolkit;
    }

    protected IManagedForm getManagedForm() {
        return managedForm;
    }

    protected IFormPageController getController() {
        return controller;
    }

    protected ITreeNode getNode() {
        return node;
    }

    protected GridLayout setCompositeLayout(final Composite clientComposite) {
        final GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginBottom = 5;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 10; // 10 pixels to show properly error
        // decoration icons
        layout.numColumns = getColumnsCount();
        clientComposite.setLayout(layout);
        return layout;
    }

    protected Section createSection(final Composite parent, final String title, final String description) {
        control = getToolkit().createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE);
        control.setText(title);
        setSectionProperties(title);
        return control;
    }

    protected Section createSection(final Composite parent, final String title) {
        control = getToolkit().createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
        setSectionProperties(title);
        return control;
    }

    private void setSectionProperties(final String title) {
        control.setText(title);
        control.setExpanded(true);
    }

    protected int getColumnsCount() {
        return COLUMNS_COUNT;
    }

    protected void dirtyStateChanged() {
        getManagedForm().dirtyStateChanged();
    }

    public IModelObject getModelObject() {
    	IModelObject modelObject = null;
        if (node != null && node.getModelObject() != null) {
            modelObject = node.getModelObject();
			final EObject component = modelObject.getComponent();
			if(component == null) {
				return null;
			}
			else if(!(component instanceof XSDSchema || component instanceof Definition) && component.eContainer() == null) {
				return null;
			}
        }
        return modelObject;
    }

    public abstract void createContents(Composite parent);

    public abstract void refresh();

    public void commit(final boolean onSave) {
    }

    public void dispose() {
    }

    public void initialize(final IManagedForm form) {
        this.managedForm = form;
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isStale() {
        return false;
    }

    public void setFocus() {
        final Control control = getDefaultControl();
        if (control instanceof Text) {
            ((Text) control).selectAll();
        }
        control.forceFocus();
    }

    protected Control getDefaultControl() {
        return null;
    }

    public boolean setFormInput(final Object input) {
        return false;
    }

    public void selectionChanged(final IFormPart part, final ISelection selection) {
    	node = null;
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			final Object firstElement = structuredSelection.size() == 1 ? structuredSelection.getFirstElement() : null;
            if (firstElement instanceof ITreeNode) {
                node = (ITreeNode) firstElement;
            }
        }
        final IModelObject modelObject = getModelObject();
        getProblemDecorator().setModelObject(modelObject);
        if(modelObject != null) {
        	refresh();
        }
    }

    protected Section getControl() {
        return control;
    }

    protected void setVisible(final boolean visible) {
        control.setVisible(visible);
        final Object layoutData = control.getLayoutData();
        if (layoutData instanceof GridData) {
            final GridData gridData = (GridData) layoutData;
            if (gridData.exclude != !visible) {
                gridData.exclude = !visible;
                control.getParent().layout();
            }
        }
    }

    protected boolean isEditable() {
        if (node == null) {
            return false;
        }
        return !getController().isResourceReadOnly() && !node.isReadOnly();
    }

    /**
     * Sets the problem decorator for this section.
     * 
     * @param problemDecorator
     */
    public void setProblemDecorator(final IProblemDecorator problemDecorator) {
        this.problemDecorator = problemDecorator;
    }

    /**
     * Returns the problem decorator for this section.
     * 
     * @return
     */
    public IProblemDecorator getProblemDecorator() {
        if (problemDecorator == null) {
            problemDecorator = new ProblemDecorator();
        }
        return problemDecorator;
    }

    /**
     * Use this to update the section control after the content of the section
     * has changed (UI controls have been shown/hidden)
     */
    protected void redrawSection() {
        final Composite client = (Composite) control.getClient();
        client.layout();
        client.redraw();
        final Composite parent = control.getParent();
        parent.layout();
        parent.redraw();
        
        control.layout();
        control.redraw();
    }

    protected boolean isWritableElementReference() {
        if (node == null || getController().isResourceReadOnly()) {
            return false;
        }

        if ((node.getCategories() & ITreeNode.CATEGORY_REFERENCE) == 0) {
            return false;
        }

        if (node.getParent() != null && (node.getParent().getCategories() & ITreeNode.CATEGORY_REFERENCE) != 0) {
            return false;
        }

        final IModelObject modelObject = getModelObject();
        final boolean isPartOfTheSameDoc = EmfWsdlUtils.isModelObjectPartOfModelRoot(modelObject.getModelRoot(), modelObject);
        return isPartOfTheSameDoc;
    }
}
