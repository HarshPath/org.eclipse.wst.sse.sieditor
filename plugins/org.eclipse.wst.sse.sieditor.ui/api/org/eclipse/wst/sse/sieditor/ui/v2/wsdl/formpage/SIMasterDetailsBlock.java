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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLDetailsPageProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerCellModifier;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerEditorActivationStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.actionenablement.SIEActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

public class SIMasterDetailsBlock extends AbstractMasterDetailsBlock {

    private ITypeDisplayer typeDisplayer;
    private Button addServiceBtn;
    private Button addOperationBtn;
    private Button addInParameterBtn;
    private Button addFaultBtn;
    private Button removeBtn;
    private Button addOutParameterBtn;

    private final SIEActionEnablementForSelectionManager enablementManager;

    /*
     * A tool item to show/hide operation categories
     */
    protected ToolItem toggleCategoriesToolItem;

    public SIMasterDetailsBlock() {
        this.enablementManager = new SIEActionEnablementForSelectionManager(this);
    }

    @Override
    public void setController(final AbstractFormPageController controller) {
        super.setController(controller);
        this.enablementManager.setController(this.getSIFormPageController());
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        super.createMasterPart(managedForm, parent);

        titleToolbar = new ToolBar(masterSection, SWT.FLAT | SWT.HORIZONTAL);
        masterSection.setTextClient(titleToolbar);

        toggleCategoriesToolItem = new ToolItem(titleToolbar, SWT.NULL | SWT.CHECK);
        toggleCategoriesToolItem.setToolTipText(Messages.SIMasterDetailsBlock_tooltip_show_categories);
        toggleCategoriesToolItem.setImage(getImageRegistry().get(Activator.TOOLBAR_TOGGLE_CATEGORIES));
        toggleCategoriesToolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final boolean currentFlag = getSIFormPageController().isShowCategoryNodes();
                getSIFormPageController().setShowCategoryNodes(!currentFlag);
            }
        });

        masterSection.setText(Messages.SIMasterDetailsBlock_master_block_title);
        masterSection.setDescription(Messages.SIMasterDetailsBlock_master_block_description);

        treeViewer.setLabelProvider(new WSDLLabelProvider());
        treeViewer.setContentProvider(new WSDLContentProvider(getSIFormPageController()));

        treeViewer.getTree().addKeyListener(createKeyListener());
        treeViewer.setColumnProperties(new String[] { UIConstants.EMPTY_STRING });
        treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(treeViewer.getTree(), SWT.NULL) });
        treeViewer.setCellModifier(new SITreeViewerCellModifier(getSIFormPageController()));

        final SITreeViewerEditorActivationStrategy editorActivationStrategy = new SITreeViewerEditorActivationStrategy(treeViewer);
        editorActivationStrategy.setEnableEditorActivationWithKeyboard(true);
        TreeViewerEditor.create(treeViewer, editorActivationStrategy, TreeViewerEditor.KEYBOARD_ACTIVATION
                | TreeViewerEditor.TABBING_VERTICAL);

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                final Object firstElement = selection.getFirstElement();
                ((SITreeViewerCellModifier) treeViewer.getCellModifier()).setSelectedElement(firstElement);
                updateButtonsState(selection);

            }
        });
    }

    /**
     * utility method
     * 
     * @param structSelection
     */
    protected void updateButtonsState(final IStructuredSelection structSelection) {
        enablementManager.selectionChanged(structSelection);
    }

    protected void addInParameterPressed() {
        final ITreeNode selectedElement = (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
        getSIFormPageController().addNewParameter(selectedElement, OperationCategory.INPUT);
    }

    protected void addOutParameterPressed() {
        final ITreeNode selectedElement = (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
        getSIFormPageController().addNewParameter(selectedElement, OperationCategory.OUTPUT);
    }

    protected void addServicePressed() {
        getSIFormPageController().addNewServiceInterface();
    }

    protected void addOperationPressed() {
        final ITreeNode selectedElement = (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
        getSIFormPageController().addNewOperation(selectedElement);

    }

    protected void addFaultPressed() {
        final ITreeNode selectedElement = (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
        getSIFormPageController().addNewFault(selectedElement);
    }

    @Override
    protected void removePressed() {
        final List<ITreeNode> listItems = ((IStructuredSelection) treeViewer.getSelection()).toList();
        getSIFormPageController().deleteItemsTriggered(listItems);
    }

    /**
     * Retrieves the controller used to change the model and keep view updated
     * 
     * @return the controller
     */
    private SIFormPageController getSIFormPageController() {
        return (SIFormPageController) controller;
    }
    /**
     * Method called when initialising the containing form (before
     * createPartControll) It's purpose is to set the object displaying the type
     * from the Parameter details page, when the hyper link is clicked,
     * 
     * @param typeDisplayer
     */

    public void setTypeDisplayer(final ITypeDisplayer typeDisplayer) {
        this.typeDisplayer = typeDisplayer;
    }

    @Override
    protected void createButtons(final FormToolkit toolkit, final Composite buttonsComposite) {
        final RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.fill = true;
        buttonsComposite.setLayout(layout);

        addServiceBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_add_interface_button, SWT.PUSH);

        addOperationBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_add_operation_button, SWT.PUSH);

        addInParameterBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_add_input_parameter_button,
                SWT.PUSH);

        addOutParameterBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_add_output_parameter_button,
                SWT.PUSH);

        addFaultBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_add_fault_button, SWT.PUSH);

        // This is a pretty dull trick - a button is used, because of the exact
        // height
        new Button(buttonsComposite, SWT.PUSH).setVisible(false);

        removeBtn = toolkit.createButton(buttonsComposite, Messages.SIMasterDetailsBlock_remove_button, SWT.PUSH);

        final SelectionListener buttonHandler = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Object source = e.getSource();
                if (source == addServiceBtn) {
                    addServicePressed();
                } else if (source == addOperationBtn) {
                    addOperationPressed();
                } else if (source == addInParameterBtn) {
                    addInParameterPressed();
                } else if (source == addOutParameterBtn) {
                    addOutParameterPressed();
                } else if (source == addFaultBtn) {
                    addFaultPressed();
                } else if (source == getRemoveButton()) {
                    removePressed();
                }
            }
        };

        addServiceBtn.addSelectionListener(buttonHandler);
        addOperationBtn.addSelectionListener(buttonHandler);
        addInParameterBtn.addSelectionListener(buttonHandler);
        addOutParameterBtn.addSelectionListener(buttonHandler);
        addFaultBtn.addSelectionListener(buttonHandler);
        getRemoveButton().addSelectionListener(buttonHandler);

        addServiceBtn.setEnabled(((SIFormPageController) getController()).isAddNewServiceInterfaceEnabled(null));
        updateButtonsState(StructuredSelection.EMPTY);
    }

    @Override
    protected IDetailsPageProvider createDetailsPageProvider() {
        return new WSDLDetailsPageProvider(getSIFormPageController(), typeDisplayer);
    }

    // ===========================================================
    // helpers
    // ===========================================================

    @Override
    public Button getRemoveButton() {
        return removeBtn;
    }

    public Button getAddFaultBtn() {
        return addFaultBtn;
    }

    public Button getAddInParameterBtn() {
        return addInParameterBtn;
    }

    public Button getAddOperationBtn() {
        return addOperationBtn;
    }

    public Button getAddOutParameterBtn() {
        return addOutParameterBtn;
    }

    public Button getAddServiceBtn() {
        return addServiceBtn;
    }

}
