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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.ViewDelegate;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.providers.SIFContentProvider;
import org.eclipse.wst.sse.sieditor.ui.providers.SIFLableProvider;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;

public class SIFWizardPage extends WizardPage implements IWizardPage,
		ISIFWizardPageView {

	private Text interfaceNameTxt;
	private Text saveLocationTxt;
	private Text wsdlNamespaceTxt;
	private Text schemaNamespaceTxt;
	private Button browse;
	private ViewDelegate viewDelegate;
	private String message;
	private ISIFWizardPageListener listener;
	private IStructuredSelection selection;

	public SIFWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName);
		this.selection = selection;
		setTitle(Messages.WIZARD_TITLE_XTIT);
		setDescription(Messages.WIZARD_PAGE_DESC_XMSG);
		viewDelegate = new ViewDelegate(this);
	}

	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		FormLayout formLayout = new FormLayout();
		container.setLayout(formLayout);
		FormData formData;

		Label interfaceNamelbl = new Label(container, SWT.NULL);
		interfaceNamelbl.setText(Messages.WIZARD_NAME_XFLD);
		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(20, 0);
		formData.top = new FormAttachment(0, 5);
		interfaceNamelbl.setLayoutData(formData);
		interfaceNameTxt = new Text(container, SWT.BORDER | SWT.SINGLE);
		formData = new FormData();
		formData.left = new FormAttachment(interfaceNamelbl, 5);
		formData.right = new FormAttachment(100, -25);
		formData.top = new FormAttachment(0, 5);
		interfaceNameTxt.setLayoutData(formData);
		interfaceNameTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				listener.validateInterfaceName();
			}
		});
		interfaceNameTxt.forceFocus();
		Label saveLocationLbl = new Label(container, SWT.NULL);
		saveLocationLbl.setText(Messages.WIZARD_SAVE_LOC_XFLD);
		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(20, 0);
		formData.top = new FormAttachment(interfaceNameTxt, 10);
		saveLocationLbl.setLayoutData(formData);

		saveLocationTxt = new Text(container, SWT.BORDER | SWT.SINGLE);
		formData = new FormData();
		formData.left = new FormAttachment(saveLocationLbl, 5);
		formData.right = new FormAttachment(100, -25);
		formData.top = new FormAttachment(interfaceNameTxt, 10);
		saveLocationTxt.setLayoutData(formData);
		
		//Set the project location to the saveLocationTxt field
		IResource resource = null;
		if (null != selection && !selection.isEmpty()) {
			Object element = selection.getFirstElement();
			
			if (element instanceof IResource) {
				resource = (IResource) element;
			} else if (element instanceof IAdaptable) {
				Object adapter = ((IAdaptable) element)
						.getAdapter(IResource.class);
				resource = (IResource) adapter;
			}
			
			String selectedPath = null;
			
			if(null != resource) {
				if(resource.getType() == IResource.FILE) {
					IPath filePath = resource.getFullPath().removeLastSegments(1);
					selectedPath = filePath.removeTrailingSeparator().toString();
				}
				else 
					selectedPath = resource.getFullPath().toString();
			}
			
			if (null != selectedPath)
				selectedPath = selectedPath.replaceFirst("/", ""); //$NON-NLS-1$ //$NON-NLS-2$
			saveLocationTxt.setText((null == selectedPath)? "" : selectedPath); ////$NON-NLS-1$

		}

		saveLocationTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				listener.validateSavedLocation();
			}
		});
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		browse = new Button(container, SWT.PUSH);
		browse.setText(" .... "); //$NON-NLS-1$
		//Browse button is visible only when there are soem projects avaialable
		if (allProjects.length <= 0)
			browse.setVisible(false);
		browse.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				listener.showDiloag();
			}

			public void widgetSelected(SelectionEvent e) {
				listener.showDiloag();
			}

		});
		formData = new FormData();
		formData.left = new FormAttachment(saveLocationTxt, 3);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(interfaceNameTxt, 5);
		browse.setLayoutData(formData);
		Label wsdlNamespaceLbl = new Label(container, SWT.NULL);
		wsdlNamespaceLbl.setText(Messages.WIZARD_WSDL_NAMESPACE_XFLD);
		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(20, 0);
		formData.top = new FormAttachment(saveLocationTxt, 5);
		wsdlNamespaceLbl.setLayoutData(formData);
		wsdlNamespaceTxt = new Text(container, SWT.BORDER | SWT.SINGLE);
		wsdlNamespaceTxt.setText("http://www.example.org/"); //$NON-NLS-1$
		formData = new FormData();
		formData.left = new FormAttachment(wsdlNamespaceLbl, 5);
		formData.right = new FormAttachment(100, -25);
		formData.top = new FormAttachment(saveLocationTxt, 5);
		wsdlNamespaceTxt.setLayoutData(formData);
		wsdlNamespaceTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				listener.validateWsdlNamespace();
			}
		});
		Label schemaNamespaceLbl = new Label(container, SWT.NULL);
		schemaNamespaceLbl.setText(Messages.WIZARD_SCHEMA_NS_XFLD);
		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(20, 0);
		formData.top = new FormAttachment(wsdlNamespaceTxt, 5);
		schemaNamespaceLbl.setLayoutData(formData);
		schemaNamespaceTxt = new Text(container, SWT.BORDER | SWT.SINGLE);
		schemaNamespaceTxt.setText("http://www.example.org/"); //$NON-NLS-1$
		formData = new FormData();
		formData.left = new FormAttachment(schemaNamespaceLbl, 5);
		formData.right = new FormAttachment(100, -25);
		formData.top = new FormAttachment(wsdlNamespaceTxt, 5);
		schemaNamespaceTxt.setLayoutData(formData);
		schemaNamespaceTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				listener.validateSchemaNamespace();
			}
		});
		setControl(container);
		viewDelegate.notifyViewCreated();

	}

	public boolean addViewListener(IViewListener viewListener) {
		if (viewListener instanceof ISIFWizardPageListener)
			this.listener = (ISIFWizardPageListener) viewListener;
		return viewDelegate.addViewListener(viewListener);
	}

	public Composite getUIHost() {
		return null;
	}

	public boolean removeViewListener(IViewListener viewListener) {
		return viewDelegate.removeViewListener(viewListener);
	}

	public String getInterfaceName() {
		return interfaceNameTxt.getText();
	}

	public String getSavedLocation() {
		return saveLocationTxt.getText();
	}

	public String getSchemaNamespace() {
		return schemaNamespaceTxt.getText();
	}

	public String getWsdlNamespace() {
		return wsdlNamespaceTxt.getText();
	}

	public void setInterfaceName(String interfaceName) {
		interfaceNameTxt.setText(interfaceName);
	}

	public void setSavedLocation(String saveLocation) {
		saveLocationTxt.setText(saveLocation);
	}

	public void setSchemaNamespace(String schemaNamespace) {
		schemaNamespaceTxt.setText(schemaNamespace);
	}

	public void setWsdlNamespace(String wsdlNamespace) {
		wsdlNamespaceTxt.setText(wsdlNamespace);
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		this.message = message;
	}

	public String getStatus() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView#showDiloag()
	 */
	public void showDiloag() {

		//Use the eclipse's ElementTreeSelectionDialog, to show the list of projects available
		//Simple Content providers and Label providers used to get the location
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new SIFLableProvider(), new SIFContentProvider());
		dialog.setTitle(Messages.WIZARD_SRC_SELECTION_XTIT); 
		dialog.setMessage(Messages.WIZARD_FOLDER_SELECTION_XMSG);
		dialog.setInput(ResourcesPlugin.getWorkspace());
		// dialog.addFilter(filter);
		String pathString = ""; //$NON-NLS-1$
		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();
			if (element instanceof IProject) {
				IProject jproject = (IProject) element;
				IPath path = jproject.getProject().getFullPath();
				pathString = path.toString();
			} else if (element instanceof IContainer) {
				pathString = ((IContainer) element).getFullPath().toString();
			} else if (element instanceof IFolder) {
				pathString = ((IFolder) element).getFullPath().toString();
			}
		}
		pathString = pathString.replaceFirst("/", ""); //$NON-NLS-1$//$NON-NLS-2$
		if (!"".equals(pathString)) //$NON-NLS-1$
			saveLocationTxt.setText(pathString);
	}

	public boolean isPageComplete() {
		return listener.canFinish();
	}

	public ISIFWizardPageListener getListener() {
		return this.listener;
	}

	public void viewCreated() {
		viewDelegate.notifyViewCreated();

	}

	public boolean isProjectAvailable() {
		return browse.isVisible();
	}

}
