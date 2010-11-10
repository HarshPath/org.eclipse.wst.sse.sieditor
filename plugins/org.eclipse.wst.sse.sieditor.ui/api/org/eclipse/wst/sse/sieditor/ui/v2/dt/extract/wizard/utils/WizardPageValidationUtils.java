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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class WizardPageValidationUtils {

    private static final WizardPageValidationUtils INSTANCE = new WizardPageValidationUtils();

    private WizardPageValidationUtils() {

    }

    public static WizardPageValidationUtils instance() {
        return INSTANCE;
    }

    public IStatus validateSchemaFilenames(final Collection<SchemaNode> nodes) {
        final Map<String, Boolean> filenamesMap = new TreeMap<String, Boolean>();

        for (final SchemaNode dependency : nodes) {
            IStatus validationStatus = validateSchemaDependencyFileExistence(dependency);
            if (!validationStatus.isOK()) {
                return validationStatus;
            }

            validationStatus = validateFilenameNotDuplicate(filenamesMap, dependency);
            if (!validationStatus.isOK()) {
                return validationStatus;
            }
            filenamesMap.put(dependency.getFilename(), Boolean.TRUE);
        }
        return Status.OK_STATUS;
    }

    private IStatus validateFilenameNotDuplicate(final Map<String, Boolean> filenamesMap, final SchemaNode dependency) {
        if (!dependency.getFilename().isEmpty() && filenamesMap.containsKey(dependency.getFilename())) {
            return new Status(Status.ERROR, Activator.PLUGIN_ID,
                    Messages.WizardPageValidationUtils_duplicate_target_filenames_error_msg);
        }
        return Status.OK_STATUS;
    }

    public IStatus validateSchemaDependencyFileExistence(final SchemaNode schemaNode) {
        if (schemaNode.getIFile().exists()) {
            return new Status(Status.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.WizardPageValidationUtils_file_X_already_exists_error_msg, schemaNode.getIFile().getFullPath()));
        }
        return Status.OK_STATUS;
    }

    public IStatus validateSchemaDependencyNodes(final Set<SchemaNode> dependenciesSet) {
        for (final SchemaNode dependency : dependenciesSet) {
            final IStatus status = validateSchemaDependencyNode(dependency, true);
            if (!status.isOK()) {
                return status;
            }
        }
        return Status.OK_STATUS;
    }

    public IStatus validateSchemaDependencyNode(final SchemaNode node, final boolean checkIfInCurrentFolder) {
        final String filename = node.getFilename();
        boolean fileNameValid = filename != null && !filename.isEmpty();
        
        if (fileNameValid && checkIfInCurrentFolder) {
            fileNameValid &= (filename.indexOf('\\') == -1 && filename.indexOf('/') == -1);
        }
        final String fullPathString = node.getFullPath().toOSString();
        final boolean pathEndsWithSeparator = fullPathString.endsWith("\\") || fullPathString.endsWith("/"); //$NON-NLS-1$//$NON-NLS-2$

        final boolean isPathValid = node.getFullPath().isValidPath(fullPathString);
        if (!fileNameValid || !isPathValid || pathEndsWithSeparator) {
            return new Status(Status.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.WizardPageValidationUtils_filename_X_invalid_error_msg, filename));
        }
        return Status.OK_STATUS;
    }
}
