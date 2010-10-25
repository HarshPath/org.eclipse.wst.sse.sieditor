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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialog;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialogConfiguration;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.common.ui.internal.search.dialogs.ScopedComponentSearchListDialog;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDComponentDescriptionProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDTypesSearchListProvider;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

/**
 * An implementation of the {@link ITypeSelectionDialogDelegate} which delegates
 * to the XSD type selection dialog.
 * <p>
 * All methods should be called from the UI thread!
 * 
 * 
 * 
 */
public class TypeSelectionDialogDelegate implements
		ITypeSelectionDialogDelegate {

	/*
	 * The wrapped dialog instance
	 */
	protected ComponentSearchListDialog dialog;

	/*
	 * The workspace file, which is a context for the types to be searched
	 */
	private IFile contextFile;

	/*
	 * The XSD schemas contained in the context
	 */
	private XSDSchema[] schemas;

	/**
	 * Constructs a new dialog delegate.
	 * 
	 * @param contextFile
	 *            the workspace file, which will be used as a context, could be
	 *            <code>null</code> if there is no context
	 * @param schemas
	 *            the XSD schemas contained in the context.
	 */
	public TypeSelectionDialogDelegate(IFile contextFile, XSDSchema[] schemas) {
		this.contextFile = contextFile;
		this.schemas = schemas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #getSelectedObject()
	 */
	public Object getSelectedObject() {
		return dialog.getSelectedComponent().getObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #getSelectedTypeFile()
	 */
	public IFile getSelectedTypeFile() {
		return dialog.getSelectedComponent().getFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #getSelectedTypeName()
	 */
	public String getSelectedTypeName() {
		return dialog.getSelectedComponent().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #getSelectedTypeNamespace()
	 */
	public String getSelectedTypeNamespace() {
		String nsQualifier = dialog.getSelectedComponent().getQualifier();
		return nsQualifier == null ? Messages.TypeSelectionDialogDelegate_NullNamespaceText
				: nsQualifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #open()
	 */
	public boolean open() {
		return dialog.open() == IDialogConstants.OK_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate
	 * #create(org.eclipse.swt.widgets.Shell, boolean, java.lang.String)
	 */
	@SuppressWarnings("restriction")
	public void create(Shell shell, String displayText,
			IComponentSearchListProvider iComponentSearchListProvider) {

		ComponentSearchListDialogConfiguration configuration = createDialogConfiguration(iComponentSearchListProvider);

		dialog = (contextFile == null) ? new ComponentSearchListDialog(shell,
				displayText, configuration)
				: new ScopedComponentSearchListDialog(shell, displayText,
						configuration);

		if (dialog instanceof ScopedComponentSearchListDialog) {
			((ScopedComponentSearchListDialog) dialog)
					.setCurrentResource(contextFile);
		}

		dialog.setBlockOnOpen(true);
		dialog.create();
	}

	/**
	 * Used to create the configuration for the dialog.
	 * 
	 * @param iComponentSearchListProvider
	 * @return a newly created configuration for {@link ComponentSearchListDialog}
	 */
	@SuppressWarnings("restriction")
	protected ComponentSearchListDialogConfiguration createDialogConfiguration(
			IComponentSearchListProvider iComponentSearchListProvider) {
		ComponentSearchListDialogConfiguration configuration = new ComponentSearchListDialogConfiguration();
		XSDComponentDescriptionProvider descriptionProvider = new XSDComponentDescriptionProvider() {
			@Override
			public String getQualifier(Object component) {
				String qualifier = super.getQualifier(component);
				return qualifier == null ? Messages.TypeSelectionDialogDelegate_NullNamespaceText
						: qualifier;
			}
		};
		configuration.setDescriptionProvider(descriptionProvider);
		configuration.setSearchListProvider(iComponentSearchListProvider);
		configuration
				.setFilterLabelText(Messages.TypeSelectionDialogDelegate_filter_label_text);
		configuration
				.setListLabelText(Messages.TypeSelectionDialogDelegate_types_list_label);

		return configuration;
	}
}
