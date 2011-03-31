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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.actionenablement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.actionenablement.DataTypesEditorActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.SIMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.actionenablement.SIEActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestSIEActionEnablementForSelectionManager  extends SIEditorBaseTest {

    AbstractEditorWithSourcePage editor = null;
    
    IFile file_NewWSDLFile = null;
    
    IFile file_MySchema = null;

    @BeforeClass
    public static void setUpBefore() {
        StatusUtils.isUnderJunitExecution = true;
    }

    @AfterClass
    public static void tearDownAfter() {
        StatusUtils.isUnderJunitExecution = false;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/wsdl_xsd/NewWSDLFile2.wsdl", Document_FOLDER_NAME, this.getProject(),
        "NewWSDLFile2.wsdl");
		refreshProjectNFile(file);
		
		file = ResourceUtils.copyFileIntoTestProject("pub/import/wsdl_xsd/MySchema2.xsd", Document_FOLDER_NAME, this.getProject(),
		"MySchema2.xsd");
		refreshProjectNFile(file);
		
		file_MySchema = ResourceUtils.copyFileIntoTestProject("pub/import/wsdl_xsd/MySchema.xsd", Document_FOLDER_NAME, this.getProject(),
		"MySchema.xsd");
		refreshProjectNFile(file_MySchema);
		
		file_NewWSDLFile = ResourceUtils.copyFileIntoTestProject("pub/import/wsdl_xsd/NewWSDLFile.wsdl", Document_FOLDER_NAME, this.getProject(),
		"NewWSDLFile.wsdl");
		refreshProjectNFile(file_NewWSDLFile);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);

    }

    @Test
    public void testSIECorrectnessOfButtonsEnablement() throws Throwable {
    	final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final FileEditorInput eInput = new FileEditorInput(file_NewWSDLFile);
		final IWorkbenchPage workbenchActivePage = window.getActivePage();
		
		editor = (ServiceInterfaceEditor) workbenchActivePage.openEditor(eInput, ServiceInterfaceEditor.EDITOR_ID);
		
        final IWsdlModelRoot modelRoot = ((ServiceInterfaceEditor) editor).getModelRoot();

        final ServiceIntefaceEditorPage siePage = (ServiceIntefaceEditorPage)((ServiceInterfaceEditor) editor).getActiveEditor();
        final SIFormPageController siFormPageController = siePage.getSIFormPageController();
        final IDataTypesFormPageController dtController = siFormPageController.getDtController();
        final DataTypesContentProvider dataTypesContentProvider = new DataTypesContentProvider(dtController);
        
        doTestWithSIEManager(siFormPageController, modelRoot);
        
        doTestWithDTEManager(dataTypesContentProvider, modelRoot);
    }
    
    @Test
    public void testDTECorrectnessOfButtonsEnablement() throws Throwable {
    	final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final FileEditorInput eInput = new FileEditorInput(file_MySchema);
		final IWorkbenchPage workbenchActivePage = window.getActivePage();
		
		editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
		
        final IXSDModelRoot modelRoot = (IXSDModelRoot)((DataTypesEditor) editor).getModelRoot();

        final DataTypesEditorPage siePage = (DataTypesEditorPage)((DataTypesEditor) editor).getActiveEditor();
        final IDataTypesFormPageController dtController = siePage.getDTController();
        final DataTypesContentProvider dataTypesContentProvider = new DataTypesContentProvider(dtController);
        
        doTestWithDTEManager(dataTypesContentProvider, modelRoot);
    }
    
    private void doTestWithSIEManager(final SIFormPageController siFormPageController, final IWsdlModelRoot modelRoot) {
    	final Object[] serviceInterfaceNodes = siFormPageController.getAllServiceInterfaceNodes(modelRoot.getDescription());
        final List<ITreeNode> allServiceInterfaceNodes = getAllTreeNodesRecursive(serviceInterfaceNodes);
        
    	final SIEActionEnablementForSelectionManagerExposer sieManager = new SIEActionEnablementForSelectionManagerExposer((SIMasterDetailsBlock)null);
        sieManager.setController(siFormPageController);
        
        // no selection
        doTestNode(null, sieManager, true, false, false, false, false, false, false);
        
        final ITreeNode nodeNewWSDLFile = getTreeNodeWithName(allServiceInterfaceNodes, "NewWSDLFile");
        doTestNode(nodeNewWSDLFile, sieManager, true, true, false, false, false, true, false);
        
        final ITreeNode nodeNewOperation = getTreeNodeWithName(allServiceInterfaceNodes, "NewOperation");
        doTestNode(nodeNewOperation, sieManager, true, true, true, true, true, true, false);
        
        final ITreeNode nodeParameters = getTreeNodeWithName(allServiceInterfaceNodes, "parameters");
        doTestNode(nodeParameters, sieManager, true, true, true, true, true, true, false);
        
        final ITreeNode nodeImportedServices = getTreeNodeWithName(allServiceInterfaceNodes, "Imported Services");
        doTestNode(nodeImportedServices, sieManager, true, false, false, false, false, false, false);
        
        final ITreeNode nodeNewWSDLFile2 = getTreeNodeWithName(allServiceInterfaceNodes, "NewWSDLFile2");
        doTestNode(nodeNewWSDLFile2, sieManager, false, false, false, false, false, false, true);
    }
    
    private void doTestWithDTEManager(final DataTypesContentProvider dtContentProvider, final IModelRoot modelRoot) {
    	final Object[] dataTypesNodes = dtContentProvider.getElements(modelRoot);
        final List<ITreeNode> allDataTypesNodes = getAllTreeNodesRecursive(dataTypesNodes);
        
        final DataTypesEditorActionEnablementForSelectionManagerExposer dteManager = new DataTypesEditorActionEnablementForSelectionManagerExposer((DataTypesMasterDetailsBlock)null);
        dteManager.setController(dtContentProvider.getController());
        
        // DTE page is from SIE, and has one more button
        if(modelRoot instanceof IWsdlModelRoot) {
        	doTestDTEasSIEpage(dteManager, allDataTypesNodes);
        }
        else {
        	doTestDTEstandalone(dteManager, allDataTypesNodes);
        }
    }
    
    private void doTestDTEasSIEpage(final DataTypesEditorActionEnablementForSelectionManagerExposer dteManager, final List<ITreeNode> allDataTypesNodes) {
    	// no selection
        doTestNode(null, dteManager, true, false, false, false, false, false, false, false, false, false);
        
        final ITreeNode nodeNS = getTreeNodeWithName(allDataTypesNodes, "http://www.example.org/NewWSDLFile/");
        doTestNode(nodeNS, dteManager, true, true, true, true, false, false, true, false, false, false);
        
        final ITreeNode nodeNewOperation = getTreeNodeWithName(allDataTypesNodes, "NewOperation");
        doTestNode(nodeNewOperation, dteManager, true, true, true, true, true, true, true, true, false, false);
        
        final ITreeNode nodeImportedTypes = getTreeNodeWithName(allDataTypesNodes, "Imported Types");
        doTestNode(nodeImportedTypes, dteManager, true, false, false, false, false, false, false, false, false, false);
        
        final ITreeNode nodeMySchema = getTreeNodeWithName(allDataTypesNodes, "http:///test_730_sp1/test/import/MySchema.xsd (MySchema.xsd)");
        doTestNode(nodeMySchema, dteManager, false, false, false, false, false, false, false, false, false, true);
    }
    
    private void doTestDTEstandalone(final DataTypesEditorActionEnablementForSelectionManagerExposer dteManager, final List<ITreeNode> allDataTypesNodes) {
    	// no selection
        doTestNode(null, dteManager, false, true, true, true, false, false, false, false, false, false);
        
        final ITreeNode nodeGlobalElement = getTreeNodeWithName(allDataTypesNodes, "GlobalElement");
        doTestNode(nodeGlobalElement, dteManager, false, true, true, true, true, true, true, true, false, false);
        
        final ITreeNode nodeImportedTypes = getTreeNodeWithName(allDataTypesNodes, "Imported Types");
        doTestNode(nodeImportedTypes, dteManager, false, true, true, true, false, false, false, false, false, false);
        
        final ITreeNode nodeMySchema = getTreeNodeWithName(allDataTypesNodes, "http:///test_730_sp1/test/import/MySchema2.xsd (MySchema2.xsd)");
        doTestNode(nodeMySchema, dteManager, false, false, false, false, false, false, false, false, false, true);
    }
    
    private void doTestNode(final ITreeNode node, final SIEActionEnablementForSelectionManagerExposer manager,
    		final boolean isAddServiceActionEnabled, 
    		final boolean isAddOperationActionEnabled,
    		final boolean isAddInParameterActionEnabled,
    		final boolean isAddOutParameterActionEnabled,
    		final boolean isAddFaultActionEnabled,
    		final boolean isDeleteActionEnabled,
    		final boolean isOpenInNewEditorActionEnabled) {
    	
        final StructuredSelection selection = node == null ? new StructuredSelection(new Object[]{}) : new StructuredSelection(node);
        
        assertEquals(isAddServiceActionEnabled, manager.isAddServiceActionEnabled(selection));
        assertEquals(isAddOperationActionEnabled, manager.isAddOperationActionEnabled(selection));
        assertEquals(isAddInParameterActionEnabled, manager.isAddInParameterActionEnabled(selection));
        assertEquals(isAddOutParameterActionEnabled, manager.isAddOutParameterActionEnabled(selection));
        assertEquals(isAddFaultActionEnabled, manager.isAddFaultActionEnabled(selection));
        assertEquals(isDeleteActionEnabled, manager.isDeleteActionEnabled(selection));
        assertEquals(isOpenInNewEditorActionEnabled, manager.isOpenInNewEditorActionEnabled(selection));
    }
    
    private void doTestNode(final ITreeNode node, final DataTypesEditorActionEnablementForSelectionManagerExposer manager,
    		final boolean isAddNamespaceEnabled, 
    		final boolean isAddGlobalElementEnabled,
    		final boolean isAddSimpleTypeEnabled,
    		final boolean isAddStructureTypeEnabled,
    		final boolean isAddElementEnabled,
    		final boolean isAddAttributeEnabled,
    		final boolean isRemoveEnabled,
    		final boolean isCopyEnabled,
    		final boolean isPasteEnabled,
    		final boolean isOpenInNewEditorEnabled) {
    	
        final StructuredSelection selection = node == null ? new StructuredSelection(new Object[]{}) : new StructuredSelection(node);
        
        assertEquals(isAddNamespaceEnabled, manager.isAddNamespaceEnabled(selection));
        assertEquals(isAddGlobalElementEnabled, manager.isAddGlobalElementEnabled(selection));
        assertEquals(isAddSimpleTypeEnabled, manager.isAddSimpleTypeEnabled(selection));
        assertEquals(isAddStructureTypeEnabled, manager.isAddStructureTypeEnabled(selection));
        
        assertEquals(isAddElementEnabled, manager.isAddElementEnabled(selection));
        assertEquals(isAddAttributeEnabled, manager.isAddAttributeEnabled(selection));
        
        assertEquals(isRemoveEnabled, manager.isRemoveEnabled(selection));
        
        assertEquals(isCopyEnabled, manager.isCopyEnabled(selection));
        assertEquals(isPasteEnabled, manager.isPasteEnabled(selection));
        assertEquals(isOpenInNewEditorEnabled, manager.isOpenInNewEditorEnabled(selection));
    }
    
    private ITreeNode getTreeNodeWithName(final List<ITreeNode> allNodes, final String nodeName) {
    	for(final ITreeNode aNode : allNodes) {
    		if(aNode.getDisplayName().equals(nodeName)) {
    			return aNode;
    		}
    	}
    	fail("Tree node not found. Node name:" + nodeName);
    	return null;
    }
    
    private List<ITreeNode> getAllTreeNodesRecursive(final Object[] allServiceInterfaceNodes) {
    	final List<ITreeNode> allNodes = new ArrayList<ITreeNode>();
    	
    	if(allServiceInterfaceNodes != null) {
	    	for(final Object node : allServiceInterfaceNodes) {
	    		allNodes.add((ITreeNode)node);

	    		final Object[] childrenNodes = ((ITreeNode)node).getChildren();
				allNodes.addAll(getAllTreeNodesRecursive(childrenNodes));
	    	}
    	}
    	return allNodes;
    }

    private class SIEActionEnablementForSelectionManagerExposer extends SIEActionEnablementForSelectionManager {

		@Override
		public boolean isAddFaultActionEnabled(final IStructuredSelection selection) {
			return super.isAddFaultActionEnabled(selection);
		}

		@Override
		public boolean isAddInParameterActionEnabled(
				final IStructuredSelection selection) {
			return super.isAddInParameterActionEnabled(selection);
		}

		@Override
		public boolean isAddOperationActionEnabled(
				final IStructuredSelection selection) {
			return super.isAddOperationActionEnabled(selection);
		}

		@Override
		public boolean isAddOutParameterActionEnabled(
				final IStructuredSelection selection) {
			return super.isAddOutParameterActionEnabled(selection);
		}

		@Override
		public boolean isAddServiceActionEnabled(
				final IStructuredSelection selection) {
			return super.isAddServiceActionEnabled(selection);
		}

		@Override
		public boolean isDeleteActionEnabled(final IStructuredSelection selection) {
			return super.isDeleteActionEnabled(selection);
		}

		@Override
		public boolean isOpenInNewEditorActionEnabled(
				final IStructuredSelection selection) {
			return super.isOpenInNewEditorActionEnabled(selection);
		}

		public SIEActionEnablementForSelectionManagerExposer(
				final SIMasterDetailsBlock masterBlock) {
			super(masterBlock);
		}
    	
    }
    
    private class DataTypesEditorActionEnablementForSelectionManagerExposer extends DataTypesEditorActionEnablementForSelectionManager {

		@Override
		public boolean isAddAttributeEnabled(final IStructuredSelection selection) {
			return super.isAddAttributeEnabled(selection);
		}

		@Override
		public boolean isAddElementEnabled(final IStructuredSelection selection) {
			return super.isAddElementEnabled(selection);
		}

		@Override
		public boolean isAddGlobalElementEnabled(
				final IStructuredSelection selection) {
			return super.isAddGlobalElementEnabled(selection);
		}

		@Override
		public boolean isAddNamespaceEnabled(final IStructuredSelection selection) {
			return super.isAddNamespaceEnabled(selection);
		}

		@Override
		public boolean isAddSimpleTypeEnabled(final IStructuredSelection selection) {
			return super.isAddSimpleTypeEnabled(selection);
		}

		@Override
		public boolean isAddStructureTypeEnabled(
				final IStructuredSelection selection) {
			return super.isAddStructureTypeEnabled(selection);
		}

		@Override
		public boolean isCopyEnabled(final IStructuredSelection selection) {
			return super.isCopyEnabled(selection);
		}

		@Override
		public boolean isOpenInNewEditorEnabled(
				final IStructuredSelection selection) {
			return super.isOpenInNewEditorEnabled(selection);
		}

		@Override
		public boolean isPasteEnabled(final IStructuredSelection selection) {
			return super.isPasteEnabled(selection);
		}

		@Override
		public boolean isRemoveEnabled(final IStructuredSelection selection) {
			return super.isRemoveEnabled(selection);
		}

		public DataTypesEditorActionEnablementForSelectionManagerExposer(
				final DataTypesMasterDetailsBlock masterBlock) {
			super(masterBlock);
		}
    	
    }

}
