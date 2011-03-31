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
package org.eclipse.wst.sse.sieditor.test.wizard;

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.wizard.mocks.ViewFactoryMock;
import org.eclipse.wst.sse.sieditor.test.wizard.mocks.WizardCreatorMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;
import org.eclipse.wst.sse.sieditor.ui.providers.SIFContentProvider;
import org.eclipse.wst.sse.sieditor.ui.providers.SIFLableProvider;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

@SuppressWarnings("nls")
public class WizardTest extends SIEditorBaseTest {

    private ISIFWizardPageView wizardPageMock;
    private ISIFWizardPageListener pageListener;
    private WizardCreatorMock wizardCreatormock;
    private ISIFWizardListener wizardListener;
    private ISIFWizardView sifwizardMock;
    private IProject project;
    protected static final String DATA_PUBLIC_SELF_SATYA_REL_PATH = "pub/self/mix2/";

    @Before
    public void setUp() throws Exception {
    	// delete project if exists
    	final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(getProjectName());
		if(project != null && project.exists()){
			project.close(null);
			project.delete(true, true, null);
		}
    	
        super.setUp();
        
        project = getProject();
        wizardCreatormock = new WizardCreatorMock();
        sifwizardMock = (ISIFWizardView) wizardCreatormock.create();
        wizardPageMock = ViewFactoryMock.getInstance().createSIFWizardPage();
        wizardPageMock.setInterfaceName("WSDLInterface");//$NON-NLS-1$
        wizardPageMock.setSavedLocation("TestProjectForWizard");//$NON-NLS-1$
        wizardPageMock.setSchemaNamespace("http://test/schemaNamespace");//$NON-NLS-1$
        wizardPageMock.setWsdlNamespace("http://test/WSDLNamespace");//$NON-NLS-1$
        pageListener = wizardPageMock.getListener();
        wizardListener = sifwizardMock.getListener();
        
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        wizardPageMock = null;
        sifwizardMock = null;
        wizardCreatormock = null;
        pageListener = null;
        wizardListener = null;
    }

    // wizard pages test
    @Test
    public void testValidateInterfaceName() {
        pageListener.validateInterfaceName();
        Assert.assertEquals(null, wizardPageMock.getStatus());
        wizardPageMock.setInterfaceName("");//$NON-NLS-1$
        pageListener.validateInterfaceName();
        Assert.assertEquals(Messages.WIZARD_INTERFACE_NOT_DEFINED_XMSG, wizardPageMock.getStatus());
        wizardPageMock.setInterfaceName("*name");//$NON-NLS-1$
        pageListener.validateInterfaceName();
        Assert.assertEquals("* is an invalid character in resource name '*name.wsdl'.", wizardPageMock.getStatus());//$NON-NLS-1$
    }

    @Test
    public void testValidateSavedLocation() {
        pageListener.validateSavedLocation();
        Assert.assertEquals(null, wizardPageMock.getStatus());
        wizardPageMock.setSavedLocation("");//$NON-NLS-1$
        pageListener.validateSavedLocation();
        Assert.assertEquals(Messages.WIZARD_WSDL_NOT_DEFINED_XMSG, wizardPageMock.getStatus());
        wizardPageMock.setSavedLocation("xyz1111");//$NON-NLS-1$
        pageListener.validateSavedLocation();
        Assert.assertEquals(Messages.WIZARD_PROJECT_NOT_EXISTS_XMSG, wizardPageMock.getStatus());
    }

    @Test
    public void testValidateWSDLNamespace() {
        pageListener.validateWsdlNamespace();
        Assert.assertEquals(null, wizardPageMock.getStatus());
        wizardPageMock.setWsdlNamespace("");//$NON-NLS-1$
        pageListener.validateWsdlNamespace();
        Assert.assertEquals(Messages.WIZARD_NO_NAMESPACE_XMSG, wizardPageMock.getStatus());

        wizardPageMock.setWsdlNamespace("xxx%");//$NON-NLS-1$
        pageListener.validateWsdlNamespace();
        Assert.assertEquals(Messages.WIZARD_WSDL_INVALID_XMSG, wizardPageMock.getStatus());

    }

    @Test
    public void testValidateSchemaNamespace() {
        pageListener.validateSchemaNamespace();
        Assert.assertEquals(null, wizardPageMock.getStatus());
        wizardPageMock.setSchemaNamespace("");//$NON-NLS-1$
        pageListener.validateSchemaNamespace();
        Assert.assertEquals(Messages.WIZARD_NO_SCHEMA_XMSG, wizardPageMock.getStatus());

        wizardPageMock.setWsdlNamespace("xxx%");//$NON-NLS-1$
        pageListener.validateWsdlNamespace();
        Assert.assertEquals(Messages.WIZARD_WSDL_INVALID_XMSG, wizardPageMock.getStatus());

    }

    @Test
    public void testWizardPageCompletenessTest() {
        pageListener.validatePage();
        Assert.assertTrue(pageListener.canFinish());
        wizardPageMock.setInterfaceName("");//$NON-NLS-1$
        wizardPageMock.setSavedLocation("");//$NON-NLS-1$
        wizardPageMock.setWsdlNamespace("");//$NON-NLS-1$
        wizardPageMock.setSchemaNamespace("");//$NON-NLS-1$
        Assert.assertTrue(!pageListener.canFinish());

    }

    // wizard test
    @Test
    public void testPerformFinish() {
        Assert.assertEquals(true, wizardListener.performFinish());

        wizardPageMock.setInterfaceName("WSDLInterface");//$NON-NLS-1$
        wizardPageMock.setSavedLocation("NoProject");//$NON-NLS-1$
        wizardPageMock.setSchemaNamespace("http://test/schemaNamespace");//$NON-NLS-1$
        wizardPageMock.setWsdlNamespace("http://test/WSDLNamespace");//$NON-NLS-1$

        Assert.assertEquals(false, wizardListener.performFinish());
    }

    // Wizard LableProvider Test.
    @Test
    public void testSIFLableProvider() {
        SIFLableProvider sifLableProvider = new SIFLableProvider();
        String imageKey = ISharedImages.IMG_OBJ_FOLDER;
        Assert.assertEquals(PlatformUI.getWorkbench().getSharedImages().getImage(imageKey), sifLableProvider.getImage(project));
        Assert.assertEquals("TestProjectForWizard", sifLableProvider.getText(project));
    }

    // Wizard ContentProvider Test.
    @Test
    public void testSIFContentProvider() throws CoreException, IOException {
        // This is just to create some folder structure.
        // Can be removed with a new project.
        getWSDLModel(DATA_PUBLIC_SELF_SATYA_REL_PATH + "PurchaseOrderConfirmation.wsdl", "PurchaseOrderConfirmation.wsdl");
        SIFContentProvider sifContentProvider = new SIFContentProvider();
        Object[] children = sifContentProvider.getChildren(ResourcesPlugin.getWorkspace());
        assertNotNull(children);
        Assert.assertTrue(children[0] instanceof IProject);

        Object[] eleObjects = sifContentProvider.getElements(ResourcesPlugin.getWorkspace());
        assertNotNull(eleObjects);
        Assert.assertTrue(eleObjects[0] instanceof IProject);

        Object[] member = sifContentProvider.getChildren(project);
        assertNotNull(member);
        Assert.assertTrue(member[0] instanceof IContainer);

        Object[] eleMember = sifContentProvider.getElements(project);
        assertNotNull(eleMember);
        Assert.assertTrue(eleMember[0] instanceof IContainer);

        Assert.assertTrue(sifContentProvider.hasChildren(project));
        Assert.assertFalse(sifContentProvider.hasChildren(new Object()));

        Assert.assertTrue(sifContentProvider.getParent(project) instanceof IResource);
        Assert.assertEquals(null, sifContentProvider.getParent(new Object()));
    }

    // Create Project with the name.
    protected String getProjectName() {
        return "TestProjectForWizard";
    }
}
