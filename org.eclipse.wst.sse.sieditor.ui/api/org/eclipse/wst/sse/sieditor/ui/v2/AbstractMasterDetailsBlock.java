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
package org.eclipse.wst.sse.sieditor.ui.v2;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public abstract class AbstractMasterDetailsBlock extends MasterDetailsBlock implements IChangeListener {

    private ImageRegistry imageRegistry;

    protected static final int BUTTONS_WIDTH = 100;

    protected Section masterSection;

    protected SectionPart masterSectionPart;

    protected TreeViewer treeViewer;

    protected AbstractFormPageController controller;

    protected ToolBar titleToolbar;

    @Override
    public void createContent(final IManagedForm managedForm) {
        super.createContent(managedForm);
        detailsPart.setPageProvider(createDetailsPageProvider());
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        final FormToolkit toolkit = managedForm.getToolkit();

        masterSectionPart = new SectionPart(parent, toolkit, Section.TITLE_BAR | Section.EXPANDED);
        managedForm.addPart(masterSectionPart);
        masterSection = masterSectionPart.getSection();

        masterSection.setExpanded(true);
        masterSection.marginWidth = 5;
        masterSection.marginHeight = 5;

        masterSection.setTextClient(titleToolbar);

        // content of the section
        final Composite masterClientComposite = toolkit.createComposite(masterSection);
        masterSection.setLayout(new GridLayout(1, false));
        masterSection.setClient(masterClientComposite);

        createMasterSectionControlls(toolkit, masterClientComposite);

        toolkit.paintBordersFor(masterClientComposite);

        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.addSelectionChangedListener(new TreeViewerSelectionListener(masterSectionPart, managedForm));
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                handleDoubleClickEvent(selection);
                updateButtonsState(selection);
            }

        });
    }

    protected ImageRegistry getImageRegistry() {
        if (imageRegistry == null) {
            imageRegistry = Activator.getDefault().getImageRegistry();
        }
        return imageRegistry;
    }

    private void createMasterSectionControlls(final FormToolkit toolkit, final Composite masterClientComposite) {

        final FilteredTree filteredTree = new FilteredTree(masterClientComposite, SWT.BORDER | SWT.MULTI, new PatternFilter(), true);
        treeViewer = filteredTree.getViewer();

        final Composite buttonsComposite = toolkit.createComposite(masterClientComposite);
        createButtons(toolkit, buttonsComposite);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = false;

        masterClientComposite.setLayout(gridLayout);
        if (buttonsComposite.getChildren().length == 0) {
            gridLayout.numColumns = 1;
        } else {
            gridLayout.numColumns = 2;
            final GridData gridData = new GridData();
            final int width = buttonsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
            gridData.minimumWidth = width;
            gridData.grabExcessHorizontalSpace = false;
            gridData.widthHint = width;
            final int horisontalSpacingFilter = ((GridLayout) filteredTree.getLayout()).horizontalSpacing;
            final int horisontalSpacingParent = ((GridLayout) masterClientComposite.getLayout()).horizontalSpacing;
            final int textFieldHeight = ((Composite) filteredTree.getChildren()[0]).getChildren()[0].computeSize(SWT.DEFAULT,
                    SWT.DEFAULT).y
                    + horisontalSpacingFilter + horisontalSpacingParent;
            gridData.verticalIndent = textFieldHeight;
            gridData.verticalAlignment = SWT.TOP;
            buttonsComposite.setLayoutData(gridData);
        }
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;

        // constraint tree
        gridData.widthHint = 100;
        gridData.heightHint = 20;

        filteredTree.setLayoutData(gridData);
    }

    protected abstract void createButtons(FormToolkit toolkit, Composite buttonsComposite);

    protected abstract IDetailsPageProvider createDetailsPageProvider();
    
    protected abstract void updateButtonsState(final IStructuredSelection structSelection);

    @Override
    protected void createToolBarActions(final IManagedForm managedForm) {
        // this method adds buttons to the bar containing the editor form's
        // title. thus it is not to be used - no such
        // buttons exist in the design
    }

    public void setController(final AbstractFormPageController controller) {
        this.controller = controller;
    }

    public AbstractFormPageController getController() {
        return controller;
    }

    @Override
    protected void registerPages(final DetailsPart detailsPart) {
        // TODO Auto-generated method stub

    }

    /**
     * used to set the tree input by the editorPart
     * 
     * @return the tree viewer of the tree representing the service interface
     */
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }
    
    /**
     * Used to handle doubleClick events in SIE and DTE
     * @param selection is the current selected item in the treeViewer
     */
    private void handleDoubleClickEvent(IStructuredSelection selection){
        if (!selection.isEmpty()) {
            Object firstElement = selection.getFirstElement();
            if (treeViewer.getExpandedState(firstElement)) {
                treeViewer.collapseToLevel(firstElement, 1);
            } else {
                treeViewer.expandToLevel(firstElement, 1);
            }

        }
    }
    
    /**
     * used to refresh the treeViewer
     */
    public void refreshTreeViewer() {
        Object[] expandedTreeItems = treeViewer.getExpandedElements();
        treeViewer.refresh();
        treeViewer.setExpandedElements(expandedTreeItems);
    }
    
    /**
     * Sets the focus to the current detail page.
     * It's up to the page to decide if and how (on which section and widget) to set it.
     */
    public void setDetailsPartFocus() {
    	IDetailsPage currentPage = detailsPart.getCurrentPage();
    	if (currentPage != null) {
    		currentPage.setFocus();
    	}
    }
    
    /**
     * utility method. returns new {@link KeyAdapter} implementation for the
     * SIE/DTE tree
     * 
     * @return the created key adapter
     */
    protected KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (shouldProcessRemoveAction(e)) {
                    removePressed();
                }
            }
        };
    }

    /**
     * utility method. checks if the remove action should be executed
     * 
     * @param e
     *            - the key event that triggered the action
     * @return <code>true</code> if the remove action is to be executed.
     *         <code>false</code> otherwise
     */
    protected boolean shouldProcessRemoveAction(final KeyEvent e) {
        return e.keyCode == SWT.DEL && getRemoveButton().isEnabled();
    }
    
    protected abstract void removePressed();
    protected abstract Button getRemoveButton();
    
    public static class TreeViewerSelectionListener implements ISelectionChangedListener {
    	
    	private final SectionPart masterSectionPart;
		private final IManagedForm managedForm;

		public TreeViewerSelectionListener(SectionPart masterSectionPart, IManagedForm managedForm) {
			this.masterSectionPart = masterSectionPart;
			this.managedForm = managedForm;
		}
    	
        @Override
        public void selectionChanged(final SelectionChangedEvent event) {
            // This way informs the details part for the node change
        	ISelection selection = event.getSelection();
        	if (((IStructuredSelection) selection).size() > 1) {
        		//don't show any details in case of multiple selection
        		selection = null;
        	}
        	managedForm.fireSelectionChanged(masterSectionPart, selection);
        }
    }
}
