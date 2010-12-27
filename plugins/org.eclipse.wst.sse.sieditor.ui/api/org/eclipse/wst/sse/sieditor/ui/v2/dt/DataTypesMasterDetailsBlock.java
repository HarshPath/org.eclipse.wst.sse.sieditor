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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

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
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractTreeViewerCellModifier;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.actionenablement.DataTypesEditorActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerEditorActivationStrategy;

public class DataTypesMasterDetailsBlock extends AbstractMasterDetailsBlock implements ITypeDisplayer {

    private final DataTypesEditorPage parentEditorPage;

    private Button addNamespacesButton;

    private Button addGlobalElementButton;

    private Button addSimpleTypeButon;

    private Button addComplexTypeButton;

    private Button removeButton;

    private Button addElementButton;

    private Button addAttributeButton;

    private final DataTypesEditorActionEnablementForSelectionManager selectionEnablementManager;

    public DataTypesMasterDetailsBlock(final DataTypesEditorPage parent) {
        this.parentEditorPage = parent;
        this.selectionEnablementManager = new DataTypesEditorActionEnablementForSelectionManager(this);
    }

    @Override
    public void setController(final AbstractFormPageController controller) {
        super.setController(controller);
        selectionEnablementManager.setController(getDtFormPageController());
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        super.createMasterPart(managedForm, parent);

        masterSection.setText(Messages.DataTypesMasterDetailsBlock_master_section_title_data_types);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            // @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                updateButtonsState(selection);
            }
        });

        getTreeViewer().setLabelProvider(createLabelProvider());
        getTreeViewer().setContentProvider(createContentProvider());
        getTreeViewer().setInput(parentEditorPage.getModel());
        getTreeViewer().getTree().addKeyListener(createKeyListener());
        getTreeViewer().setColumnProperties(new String[] { UIConstants.EMPTY_STRING });
        getTreeViewer().setCellEditors(new CellEditor[] { new TextCellEditor(treeViewer.getTree(), SWT.NULL) });
        getTreeViewer().setCellModifier(new DTTreeViewerCellModifier(getDtFormPageController()));

        final SITreeViewerEditorActivationStrategy editorActivationStrategy = new SITreeViewerEditorActivationStrategy(treeViewer);
        editorActivationStrategy.setEnableEditorActivationWithKeyboard(true);
        TreeViewerEditor.create(getTreeViewer(), editorActivationStrategy, TreeViewerEditor.KEYBOARD_ACTIVATION
                | TreeViewerEditor.TABBING_VERTICAL);

        getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                final Object firstElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
                ((AbstractTreeViewerCellModifier) treeViewer.getCellModifier()).setSelectedElement(firstElement);

            }
        });

    }

    protected DataTypesContentProvider createContentProvider() {
        return new DataTypesContentProvider(getDtFormPageController());
    }

    protected DataTypesLabelProvider createLabelProvider() {
        return new DataTypesLabelProvider();
    }

    @Override
    protected void createButtons(final FormToolkit toolkit, final Composite buttonsComposite) {
        final RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.fill = true;
        buttonsComposite.setLayout(layout);

        // inits the button only if the editor is part of SI Editor (the
        // controller is ISiEditorDataTypesFormPageController
        // if there are more differences between the stand alone and integrated
        // editor, this chass should be extended isntead
        if (controller instanceof ISiEditorDataTypesFormPageController) {
            addNamespacesButton = toolkit.createButton(buttonsComposite,
                    Messages.DataTypesMasterDetailsBlock_add_namespace_button, SWT.PUSH);
        }

        addGlobalElementButton = toolkit.createButton(buttonsComposite, Messages.DataTypesMasterDetailsBlock_AddGlobalElement,
                SWT.PUSH);

        addSimpleTypeButon = toolkit.createButton(buttonsComposite, Messages.DataTypesMasterDetailsBlock_add_simple_type_button,
                SWT.PUSH);

        addComplexTypeButton = toolkit.createButton(buttonsComposite,
                Messages.DataTypesMasterDetailsBlock_add_complex_type_button, SWT.PUSH);

        final Button buttonSeparator1 = new Button(buttonsComposite, SWT.PUSH);
        buttonSeparator1.setVisible(false);

        addElementButton = toolkit.createButton(buttonsComposite, Messages.DataTypesMasterDetailsBlock_add_element_button,
                SWT.PUSH);

        addAttributeButton = toolkit.createButton(buttonsComposite, Messages.DataTypesMasterDetailsBlock_add_attribute_button,
                SWT.PUSH);

        final Button buttonSeparator2 = new Button(buttonsComposite, SWT.PUSH);
        buttonSeparator2.setVisible(false);

        removeButton = toolkit.createButton(buttonsComposite, Messages.DataTypesMasterDetailsBlock_remove_button, SWT.PUSH);

        final SelectionListener buttonHandler = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Object source = e.getSource();
                if (source == addGlobalElementButton) {
                    addGlobalElementPressed();
                } else if (source == addNamespacesButton) {
                    addNsPressed();
                } else if (source == addElementButton) {
                    addElementPressed();
                } else if (source == addSimpleTypeButon) {
                    addSimpleTypePressed();
                } else if (source == addComplexTypeButton) {
                    addComplexTypePressed();
                } else if (source == addAttributeButton) {
                    addAttributePressed();
                } else if (source == getRemoveButton()) {
                    removePressed();
                }
            }
        };

        if (addNamespacesButton != null) {
            addNamespacesButton.addSelectionListener(buttonHandler);
        }
        addGlobalElementButton.addSelectionListener(buttonHandler);
        addElementButton.addSelectionListener(buttonHandler);
        addComplexTypeButton.addSelectionListener(buttonHandler);
        addSimpleTypeButon.addSelectionListener(buttonHandler);
        addAttributeButton.addSelectionListener(buttonHandler);
        getRemoveButton().addSelectionListener(buttonHandler);

        updateButtonsState(StructuredSelection.EMPTY);
    }

    protected void addGlobalElementPressed() {
        final IDataTypesTreeNode selectedElement = (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection())
                .getFirstElement();
        getDtFormPageController().handleAddGlobalElementAction(selectedElement);
    }

    /**
     * Called by the treeViewer's ISelectionChangedListener. Purpose - to update
     * the buttons enabled/disabled state regarding the selected element. If the
     * {@link DataTypesMasterDetailsBlock#createMasterPart(IManagedForm, Composite)}
     * is overridden,<br>
     * this method will not be called so ther would be no use of overriding it.
     * 
     * @param selection
     */
    protected void updateButtonsState(final IStructuredSelection selection) {
        selectionEnablementManager.selectionChanged(selection);
    }

    protected void addSimpleTypePressed() {
        final IDataTypesTreeNode selectedElement = (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection())
                .getFirstElement();
        getDtFormPageController().handleAddSimpleTypeAction(selectedElement);
    }

    private void addElementPressed() {
        final IDataTypesTreeNode selectedElement = (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection())
                .getFirstElement();
        getDtFormPageController().handleAddElementAction(selectedElement);
    }

    @Override
    protected void removePressed() {

        getDtFormPageController().handleRemoveAction(((IStructuredSelection) treeViewer.getSelection()).toList());
    }

    protected void addComplexTypePressed() {
        final IDataTypesTreeNode selectedElement = (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection())
                .getFirstElement();
        getDtFormPageController().handleAddStructureTypeAction(selectedElement);

    }

    protected void addNsPressed() {
        ((ISiEditorDataTypesFormPageController) getDtFormPageController()).handleAddNewNamespaceAction();
    }

    protected void addAttributePressed() {
        final IDataTypesTreeNode selectedElement = (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection())
                .getFirstElement();
        getDtFormPageController().handleAddAttributeAction(selectedElement);
    }

    @Override
    protected void createToolBarActions(final IManagedForm managedForm) {
        // TODO Auto-generated method stub

    }

    public void showType(final IType type) {
        showModelObject(type);
    }

    private void showModelObject(final IModelObject modelObjectToShow) {
        if (modelObjectToShow instanceof UnresolvedType) {
            return;
        }

        final TreeNodeMapper treeNodeMapper = getDtFormPageController().getTreeNodeMapper();
        // TODO - what if the element is not contained in the tree??
        boolean proceedSearch = true;
        IModelObject parentObj;
        IModelObject childObj;
        // if no tree node is found in the mapper for this IModelObject
        while (treeNodeMapper.getTreeNode(modelObjectToShow) == null && proceedSearch) {
            // try to find if it's parent has a treeNode registered in the
            // mapper.
            parentObj = modelObjectToShow.getParent();
            while (treeNodeMapper.getTreeNode(parentObj) == null) {
                // if the parent does not have a node registered in the mapper -
                // try the parent's parent
                childObj = parentObj;
                parentObj = parentObj.getParent();
                // if the getParent() method returns null - break the search for
                // a registered ModelObject
                if (parentObj == childObj || parentObj == null) {
                    proceedSearch = false;
                    break;
                }
            }
            treeViewer.expandToLevel(treeNodeMapper.getTreeNode(parentObj), 1);
        }
        if (proceedSearch) {
            treeViewer.setSelection(new StructuredSelection(treeNodeMapper.getTreeNode(modelObjectToShow)), true);
        } else {
            // TODO log type not found!!!
        }
    }

    @Override
    protected IDetailsPageProvider createDetailsPageProvider() {
        return new DataTypesDetailsPageProvider(getDtFormPageController(), this);
    }

    private DataTypesFormPageController getDtFormPageController() {
        return (DataTypesFormPageController) getController();
    }

    // =========================================================
    // getters
    // =========================================================

    public Button getAddAttributeButton() {
        return addAttributeButton;
    }

    public Button getAddComplexTypeButton() {
        return addComplexTypeButton;
    }

    public Button getAddGlobalElementButton() {
        return addGlobalElementButton;
    }

    public Button getAddElementButton() {
        return addElementButton;
    }

    public Button getAddNamespacesButton() {
        return addNamespacesButton;
    }

    public Button getAddSimpleTypeButon() {
        return addSimpleTypeButon;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    };
}
