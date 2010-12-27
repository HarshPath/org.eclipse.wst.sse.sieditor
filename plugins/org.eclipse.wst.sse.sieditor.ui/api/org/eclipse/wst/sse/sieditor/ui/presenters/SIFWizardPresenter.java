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
package org.eclipse.wst.sse.sieditor.ui.presenters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.DefaultPresenter;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.SIEditorConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizard;

public class SIFWizardPresenter extends DefaultPresenter<ISIFWizardView> {

	private ISIFWizardView view;

	public SIFWizardPresenter(ISIFWizardView view) {
		super(view);
		this.view = view;
	}

	/**
	 * Creates a new file, and opens it with SI Editor
	 * @return
	 */
	public boolean performFinish() {

		try {
			IFile file = createNewFile();
			// IFile file = createFileForWizard();
			if (null != file)
				view.openSIEditor(file);
			else {
				view.showErrorMessage();
				return false;
			}

		} catch (Exception e) {
			Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error while opening SI editor.", e); //$NON-NLS-1$
			view.showErrorMessage();
			return false;

		}
		return true;

	}

	/*
	 * Creates anew file from the name and location given in the view
	 */
	private IFile createNewFile() {
		String location = view.getSavedLocation();
		String projName;
		String filePath = UIConstants.EMPTY_STRING;
		if (location.contains("/")) { //$NON-NLS-1$
			projName = location.substring(0, location.indexOf('/'));
			filePath = location.substring(location.indexOf('/'));
		} else
			projName = location;
		IContainer parentFolder = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projName);

		IPath folderStructure = new Path(filePath
				+ "/" + view.getInterfaceName() + SIEditorConstants.PERIOD + SIEditorConstants.WSDL_EXTENSION); //$NON-NLS-1$ 

		IFile file = parentFolder.getFile(folderStructure);
		try {
			
			//Use a wsdl.template file which contains the template of a new file
			StringBuffer buffer = createTemplate(SIFWizard.class,
					"wsdl.template"); //$NON-NLS-1$
			String fileData = buffer.toString();
			//Following variables replaced in the new content of the New File
			fileData = fileData.replace("$NAME$", view.getInterfaceName()); //$NON-NLS-1$
			fileData = fileData.replace("$SECHEMA$", view.getSchemaNamespace()); //$NON-NLS-1$
			fileData = fileData.replace(
					"$WSDLNAMESPACE$", view.getWsdlNamespace()); //$NON-NLS-1$
			FileWriter fileWriter = null;
			try {
				if (null != file.getLocationURI())
					fileWriter = new FileWriter(file.getLocationURI().getPath());
				else
					return null;
			} catch (Exception e) {
				Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
						"Can not create file writer for file at location " + file.getLocationURI(), e); //$NON-NLS-1$
				return null;
			}
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write(fileData);
			out.close();

			if (!file.getLocation().toFile().exists()) {
				if (file.exists()) {
					file.refreshLocal(IResource.DEPTH_INFINITE, null);
				} else {

					try {
						file.create(new FileInputStream(buffer.toString()),
								true, null);
					} catch (Exception e) {
						Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
								"Can not create file for location " + file.getLocationURI(), e); //$NON-NLS-1$
					}

					file.setCharset(buffer.toString(), null);
				}
			} else if (!file.exists()) {
				// --> refresh Eclipse
				parentFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StringBuffer createTemplate(Class<SIFWizard> clazz,
			String resourceName) {
		StringBuffer buffer = null;
		try {
			InputStream templateStream = clazz
					.getResourceAsStream(resourceName);
			if (templateStream != null) {
				buffer = createTemplate(templateStream);
			}
		} catch (IOException ex) {
			Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
					"Can not create template for resource " + resourceName, ex); //$NON-NLS-1$
		}
		return buffer;
	}

	/**
	 * Creates a new template by parsing the given input stream's content.
	 */
	private StringBuffer createTemplate(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8")); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer();
		int c = reader.read();
		while (c != -1) {
			buffer.append((char) c);
			c = reader.read();
		}
		reader.close();

		return buffer;
	}

}
