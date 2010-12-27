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
package org.eclipse.wst.sse.sieditor.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.ViewDelegate;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

public class SIFWizard extends Wizard implements INewWizard, ISIFWizardView {

	private ViewDelegate viewDelegate;
	private ISIFWizardPageView view;
	private IStructuredSelection selection;
	private ISIFWizardListener listener;

	public SIFWizard() {
		setWindowTitle(Messages.WIZARD_TITLE_XTIT);
		viewDelegate = new ViewDelegate(this);

	}

	@Override
	public boolean performFinish() {
		return listener.performFinish();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		return getContainer().getCurrentPage().isPageComplete();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		viewDelegate.notifyViewCreated();

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView#addViewListener(org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener)
	 */
	public boolean addViewListener(IViewListener viewListener) {
		if (viewListener instanceof ISIFWizardListener)
			this.listener = (ISIFWizardListener) viewListener;
		return viewDelegate.addViewListener(viewListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView#getUIHost()
	 */
	public Composite getUIHost() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView#removeViewListener(org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener)
	 */
	public boolean removeViewListener(IViewListener viewListener) {
		return viewDelegate.removeViewListener(viewListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#doAddPage(org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView)
	 */
	public void doAddPage(ISIFWizardPageView page) {
		view = page;
		addPage((IWizardPage)page);
	}

	/**
	 * Initializes the wizard by setting the selection
	 * @param workbench
	 * @param selection
	 */
	public void doInit(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	public Shell getWorkbenchShell() {
		return getShell();
	}

	public void viewCreated() {

	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	// Can be uncommented if in future there will a need of velocity.

	// private Template initAndGetVelocityTemplate() throws Exception {
	// Template template = null;
	// try {
	// URL url = Platform.resolve(Activator.getDefault().getBundle()
	//					.getEntry("/")); //$NON-NLS-1$
	// String pluginPath = url.getPath();
	//			String templatePath = pluginPath + "template"; //$NON-NLS-1$
	// Velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
	// templatePath);
	// Velocity.init();
	//			template = Velocity.getTemplate("wsdl.vm"); //$NON-NLS-1$
	// return template;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception(WizardMessages
	//					.getString("UI_ERROR_VELOCITY_NOT_EXISTS")); //$NON-NLS-1$
	// }
	// }

	// Can be uncommented if in future there will a need of velocity.

	// private VelocityContext getVelocityContext() {
	// VelocityContext context = new VelocityContext();
	//
	// String name = view.getInterfaceName();
	// String schemaNamespace = view.getSchemaNamespace();
	// String wsdlNamespace = view.getWsdlNamespace();
	//		String operationResponseName = "operationResponseName"; //$NON-NLS-1$
	//		String operationRequestName = "operationRequestName"; //$NON-NLS-1$
	//		String operationName = "operationName"; //$NON-NLS-1$
	//
	//		context.put("name", name); //$NON-NLS-1$
	//		context.put("schemaNamespace", schemaNamespace); //$NON-NLS-1$
	//		context.put("wsdlNamespace", wsdlNamespace); //$NON-NLS-1$
	//		context.put("operationResponseName", operationResponseName); //$NON-NLS-1$
	//		context.put("operationRequestName", operationRequestName); //$NON-NLS-1$
	//		context.put("operationName", operationName); //$NON-NLS-1$
	//
	// return context;
	//
	// }

	// Can be uncommented if in future there will a need of velocity.

	// private String getFileContents() throws Exception {
	// Template template = null;
	// try {
	// template = initAndGetVelocityTemplate();
	// } catch (Exception e) {
	// throw new Exception(e);
	// }
	// VelocityContext context = getVelocityContext();
	// StringWriter writer = new StringWriter();
	// try {
	// template.merge(context, writer);
	// return writer.getBuffer().toString();
	// } catch (Exception e) {
	// throw new Exception(e);
	// }
	// }

	// Can be uncommented if in future there will a need of velocity.
	// private IFile getFileFromWizard() {
	// String location = view.getSavedLocation();
	// String projName;
	// String filePath = new String();
	//		if (location.contains("/")) { //$NON-NLS-1$
	// projName = location.substring(0, location.indexOf('/'));
	// filePath = location.substring(location.indexOf('/'));
	// } else
	// projName = location;
	// IContainer parentFolder = ResourcesPlugin.getWorkspace().getRoot()
	// .getProject(projName);
	//
	// IPath folderStructure = new Path(filePath
	//				+ "/" + view.getInterfaceName() + ".wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
	//
	// IFile file = parentFolder.getFile(folderStructure);
	// return file;
	// }

	// private IFile createFileForWizard() throws Exception {
	//		String contents = WizardMessages.getString("34"); //$NON-NLS-1$
	// try {
	// contents = getFileContents();
	// } catch (Exception e) {
	// throw new Exception(e);
	// }
	// byte[] bytes = contents.getBytes();
	// ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
	// InputStream istream = (InputStream) stream;
	// IFile file = getFileFromWizard();
	// try {
	// if (!file.exists()) {
	// file.createLink(file.getLocation(),
	// IResource.ALLOW_MISSING_LOCAL, null);
	// // file.create(istream,IResource.NONE , null);
	// }
	//
	// file.setContents(istream, true, true, null); // keephistory = true
	// // for possible undo
	// // feature,
	// // force=true to
	// // over-write the
	// // contents
	//
	// return file;
	// } catch (CoreException e) {
	// System.out.println(e.getMessage());
	// ErrorDialog.openError(getShell(), WizardMessages
	//					.getString("UI_ERROR_LOCATION_NOT_EXISTS"), //$NON-NLS-1$
	// e.getMessage(), e.getStatus());
	// throw new Exception(e.getCause());
	// }
	// }

	public IStructuredSelection getSelection() {
		return this.selection;
	}

	public void showErrorMessage() {
		MessageDialog.openError(getShell(), Messages.WIZARD_LOC_NOT_EXISTS_XTIT, Messages.WIZARD_LOC_NOT_EXISTS_XMSG);
	}

	public void showEditorUnavailable(PartInitException e) {
		ErrorDialog.openError(getShell(), Messages.WIZARD_EDITOR_NOT_AVAILABLE_XMSG,
				e.getMessage(), e.getStatus());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#getInterfaceName()
	 */
	public String getInterfaceName() {
		return view.getInterfaceName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#getSavedLocation()
	 */
	public String getSavedLocation() {
		return view.getSavedLocation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#getWsdlNamespace()
	 */
	public String getWsdlNamespace() {
		String wsdlNamespace = view.getWsdlNamespace();
		if(!wsdlNamespace.endsWith("/")) //$NON-NLS-1$
			wsdlNamespace = wsdlNamespace.concat("/"); //$NON-NLS-1$
		return wsdlNamespace;
	}

	public ISIFWizardListener getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#getSchemaNamespace()
	 */
	public String getSchemaNamespace() {
		String schemanNamespace = view.getSchemaNamespace();
		if(!schemanNamespace.endsWith("/")) //$NON-NLS-1$
			schemanNamespace = schemanNamespace.concat("/"); //$NON-NLS-1$
		return schemanNamespace;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView#openSIEditor(org.eclipse.core.resources.IFile)
	 */
	public void openSIEditor(IFile file) {

		if (null != file) {
			IEditorInput editorInput = new FileEditorInput(file);
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			try {
				//The SIEditor editor ID is provided here 
				page.openEditor(editorInput,
						ServiceInterfaceEditor.EDITOR_ID);
			} catch (PartInitException e) {
				Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
	        			"Failed to open SIEditor.", e); //$NON-NLS-1$
				showEditorUnavailable(e);
			}
		}
			
	}
}
