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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class SchemaFilenameGenerator {

    private static final SchemaFilenameGenerator INSTANCE = new SchemaFilenameGenerator();

    private SchemaFilenameGenerator() {

    }

    public static SchemaFilenameGenerator instance() {
        return INSTANCE;
    }

    public String getFilename(final IPath path) {
        String filename = null;
        IPath clone = null;
        int i = 1;

        do {
            clone = (IPath) path.clone();
            filename = MessageFormat.format(ExtractSchemaWizardConstants.DEFAULT_EXTRACTED_SCHEMA_FILENAME_TEMPLATE, i);
            i++;
        } while (ResourcesPlugin.getWorkspace().getRoot().getFile(clone.append(filename)).exists());

        return filename;
    }

    public String[] getFilenames(final IPath path, final String template, final int numberOfFiles) {
        if (numberOfFiles == 0) {
            return new String[] {};
        }
        final String[] filenames = new String[numberOfFiles];

        int createdFilenames = 0;
        int i = 0;
        do {
            final IPath clone = (IPath) path.clone();
            final String filename = MessageFormat.format(template, i + 1);
            if (!ResourcesPlugin.getWorkspace().getRoot().getFile(clone.append(filename)).exists()) {
                filenames[createdFilenames++] = filename;
            }

            i++;
        } while (createdFilenames < numberOfFiles);

        return filenames;
    }
}
