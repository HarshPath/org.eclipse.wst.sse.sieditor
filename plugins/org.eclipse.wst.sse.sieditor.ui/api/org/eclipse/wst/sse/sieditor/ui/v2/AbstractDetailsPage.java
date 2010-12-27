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
package org.eclipse.wst.sse.sieditor.ui.v2;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.AbstractDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;

public abstract class AbstractDetailsPage implements IDetailsPage {

    private List<IDetailsPageSection> sections;

    private FormToolkit toolkit;

    private IManagedForm managedForm;

    private final IFormPageController controller;

    protected IProblemDecorator problemDecorator;

    protected ITreeNode treeNode;

    public AbstractDetailsPage(IFormPageController controller) {
        this.controller = controller;
    }

    protected void setSections(List<IDetailsPageSection> sections) {
        this.sections = sections;
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

    protected void dirtyStateChanged() {
        getManagedForm().dirtyStateChanged();
    }

    protected abstract void createSections(Composite parent);

    // @Override
    public final void createContents(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginRight = 5;
        layout.marginLeft = 5;
        layout.marginBottom = 5;
        layout.verticalSpacing = 10;
        parent.setLayout(layout);
        toolkit = new FormToolkit(parent.getDisplay());

        createSections(parent);
        for (Control control : parent.getChildren()) {
            GridData gd = (GridData) control.getLayoutData();
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
        }
    }

    // @Override
    public void initialize(IManagedForm form) {
        this.managedForm = form;
    }

    public boolean isDirty() {
        for (IDetailsPage section : getSections()) {
            if (section.isDirty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isStale() {
    	if(getSections() != null) {
	        for (IDetailsPage section : getSections()) {
	            if (section.isStale()) {
	                return true;
	            }
	        }
    	}
        return getProblemDecorator().decorationNeedsUpdate();
    }

    public void refresh() {
        for (IDetailsPageSection section : getSections()) {
        	if(section instanceof AbstractDetailsPageSection) {
            	// do not refresh pages without model objects being set
        		if(((AbstractDetailsPageSection)section).getModelObject() != null) {
        			section.refresh();
        		}
        	}
        	else {
        		section.refresh();
        	}
        }
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        for (IDetailsPageSection section : getSections()) {
            section.selectionChanged(part, selection);
        }
    }

    public void setFocus() {
    	if (sections == null) {
    		Control control = getDefaultControl();
    		if (control instanceof Text) {
    			((Text) control).selectAll();
    		}
    		if (control != null) {
    			control.forceFocus();
    		}
    	} else if (!sections.isEmpty()) {
    		sections.get(0).setFocus();
    	}
    }
    
    protected Control getDefaultControl() {
    	return null;
    }

    public boolean setFormInput(Object input) {
        return false;
    }

    public void commit(boolean onSave) {
    }

    public void dispose() {
    }

    public List<IDetailsPageSection> getSections() {
        return sections;
    }

    public boolean isReadOnly() {
        boolean isReadOnly = getController().isResourceReadOnly();

        if (treeNode != null) {
            isReadOnly = isReadOnly || treeNode.isReadOnly();
        }
        return isReadOnly;
    }

    /**
     * Sets the problem decorator for this page.
     * 
     * @param problemDecorator
     */
    public void setProblemDecorator(IProblemDecorator problemDecorator) {
        this.problemDecorator = problemDecorator;
    }

    /**
     * Returns the problem decorator for this page.
     * 
     * @return
     */
    public IProblemDecorator getProblemDecorator() {
        if (problemDecorator == null) {
            problemDecorator = new ProblemDecorator();
        }
        return problemDecorator;
    }
}