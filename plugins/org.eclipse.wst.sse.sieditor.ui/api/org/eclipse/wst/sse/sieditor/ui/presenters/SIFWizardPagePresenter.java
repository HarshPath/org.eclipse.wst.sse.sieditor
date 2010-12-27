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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.DefaultPresenter;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.ui.SIEditorConstants;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;

public class SIFWizardPagePresenter extends DefaultPresenter<ISIFWizardPageView> {

    private ISIFWizardPageView view;
    private String mStr_msg;
    private String interfaceName;
    private String location;
    private String wsdlNamespace;
    private String schemaNamespace;
    private boolean isValidPage = false;

    public SIFWizardPagePresenter(ISIFWizardPageView view) {
        super(view);
        this.view = view;
    }

    /**
     * Validates the Service Interface name given in the UI
     */
    public void validateInterfaceName() {
        interfaceName = view.getInterfaceName();
        mStr_msg = null;
        if (interfaceName.length() == 0) {
            mStr_msg = Messages.WIZARD_INTERFACE_NOT_DEFINED_XMSG;
            view.updateStatus(mStr_msg);
        } else
            validatePage();

    }

    /**
     * Validates the saved loaction given in the UI
     */
    public void validateSavedLocation() {
        location = view.getSavedLocation();
        mStr_msg = null;

        if (location.length() == 0) {
            mStr_msg = Messages.WIZARD_WSDL_NOT_DEFINED_XMSG;
            view.updateStatus(mStr_msg);
        } else if (!view.isProjectAvailable()) {
            mStr_msg = Messages.WIZARD_NO_PROJECT_XMSG;
            view.updateStatus(mStr_msg);
        } else
            validatePage();

    }

    /**
     * VAlidates the namespace of the WSDL given in the UI
     */
    public void validateWsdlNamespace() {
        wsdlNamespace = view.getWsdlNamespace();
        mStr_msg = null;
        if (wsdlNamespace.length() == 0) {
            mStr_msg = Messages.WIZARD_NO_NAMESPACE_XMSG;
            view.updateStatus(mStr_msg);
        } else
            validatePage();
    }

    /**
     * Validates the namespace for the Schema which is created in the WSDL
     */
    public void validateSchemaNamespace() {
        schemaNamespace = view.getSchemaNamespace();
        mStr_msg = null;
        if (schemaNamespace.length() == 0) {
            mStr_msg = Messages.WIZARD_NO_SCHEMA_XMSG;
            view.updateStatus(mStr_msg);
        } else
            validatePage();
    }

    public void showDailog() {
        view.showDiloag();
    }

    /**
     * Checks if all fields are updated and have valid entries Only then allows
     * the user to press Finish
     * 
     * @return
     */
    public boolean canFinish() {
        interfaceName = view.getInterfaceName();
        location = view.getSavedLocation();
        wsdlNamespace = view.getWsdlNamespace();
        schemaNamespace = view.getSchemaNamespace();
        if (!("".equals(interfaceName)) && (null != interfaceName) && //$NON-NLS-1$
                EmfXsdUtils.isValidNCName(interfaceName) && !("".equals(location)) && (null != location) && //$NON-NLS-1$
                !("".equals(wsdlNamespace)) && (null != wsdlNamespace) && //$NON-NLS-1$
                !("".equals(schemaNamespace)) && (null != schemaNamespace) && isValidPage) //$NON-NLS-1$
            return true;
        return false;
    }

    /*
     * Checks whether the Given location exists or not
     */
    private boolean validateContainer() {
        IPath path = new Path(view.getSavedLocation());
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        String projectName = path.segment(0);
        if (projectName == null || !workspace.getRoot().getProject(projectName).exists()) {
            return false;
        }
        // path is invalid if any prefix is occupied by a file
        IWorkspaceRoot root = workspace.getRoot();
        while (path.segmentCount() > 1) {
            if (!root.getFolder(path).exists()) {
                return false;
            }

            path = path.removeLastSegments(1);
        }
        return true;
    }

    private boolean validateTargetNamespace(String ns) {
        boolean test = true;
        try {
            new URI(ns);
        } catch (URISyntaxException e) {
            test = false;
        }
        return test;
    }

    /*
     * Checks if the file name given is Valid
     */
    private boolean validateFileName() {
        mStr_msg = null;
        String resourceName = view.getInterfaceName() + SIEditorConstants.PERIOD + SIEditorConstants.WSDL_EXTENSION;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IStatus result = workspace.validateName(resourceName, IResource.FILE);
        if (!result.isOK()) {
            mStr_msg = result.getMessage();
            view.updateStatus(mStr_msg);
            return false;
        }
        return true;
    }

    // returns true if file of specified name exists in any case for selected
    // container
    private String existsFileAnyCase() {
        if ((view.getSavedLocation() != null) && (view.getSavedLocation().length() > 0)
                && (view.getInterfaceName().compareTo("") != 0)) //$NON-NLS-1$
        {
            // look through all resources at the specified container - compare
            // in upper case
            IResource parent = ResourcesPlugin.getWorkspace().getRoot().findMember(view.getSavedLocation());
            if (parent instanceof IContainer) {
                IContainer container = (IContainer) parent;
                try {
                    IResource[] members = container.members();
                    String enteredFileUpper = (view.getInterfaceName() + SIEditorConstants.PERIOD + SIEditorConstants.WSDL_EXTENSION)
                            .toUpperCase(Locale.ENGLISH);
                    for (int i = 0; i < members.length; i++) {
                        String resourceUpperName = members[i].getName().toUpperCase(Locale.ENGLISH);
                        if (resourceUpperName.equals(enteredFileUpper)) {
                            return members[i].getName();
                        }
                    }
                } catch (CoreException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Validates each entry in the page and sets the status Status contains an
     * error message if the page is not valid Or is set to null if the page is
     * valid
     */
    public void validatePage() {
        isValidPage = true;
        interfaceName = view.getInterfaceName();
        if (!validateTargetNamespace(view.getSchemaNamespace())) {
            isValidPage = false;
            view.updateStatus(Messages.WIZARD_SCHEMA_INVALID_XMSG);

        } else if (!validateFileName())
            isValidPage = false;
        else if (interfaceName.length() == 0) {
            isValidPage = false;
            mStr_msg = Messages.WIZARD_INTERFACE_NOT_DEFINED_XMSG;
            view.updateStatus(mStr_msg);
        } else if (!EmfXsdUtils.isValidNCName(interfaceName)) {
            isValidPage = false;
            view.updateStatus(Messages.WIZARD_INVALID_INTERFACE_NAME_XMSG);

        } else if (!validateContainer()) {
            isValidPage = false;
            view.updateStatus(Messages.WIZARD_PROJECT_NOT_EXISTS_XMSG);

        } else if (!validateTargetNamespace(view.getWsdlNamespace())) {
            isValidPage = false;
            view.updateStatus(Messages.WIZARD_WSDL_INVALID_XMSG);
        } else if (!validateTargetNamespace(view.getSchemaNamespace())) {
            isValidPage = false;
            view.updateStatus(Messages.WIZARD_SCHEMA_INVALID_XMSG);
        } else if (null != existsFileAnyCase()) {
            isValidPage = false;
            view.updateStatus(MessageFormat.format(Messages.WIZARD_FILE_EXIST_XMSG, new Object[] { existsFileAnyCase().toLowerCase(
                    Locale.ENGLISH) }));
        } else {
            view.updateStatus(null);
        }
    }

}
