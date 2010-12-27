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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class SchemaLocationUtils {

    private static final String ECLIPSE_PLATFORM_DEVICE = "platform:"; //$NON-NLS-1$

    private static final SchemaLocationUtils INSTANCE = new SchemaLocationUtils();

    private SchemaLocationUtils() {

    }

    public static SchemaLocationUtils instance() {
        return INSTANCE;
    }

    public IPath getLocationRelativeToWorkspace(final IPath schemaPath) {
        IPath schemaPathSimple = schemaPath;
        if (schemaPathSimple.getDevice() != null && schemaPathSimple.getDevice().equals(ECLIPSE_PLATFORM_DEVICE)) {
            // remove device + first segment: "platform:/resource"
            schemaPathSimple = schemaPathSimple.removeFirstSegments(1).setDevice(null);
        } else if (schemaPathSimple.getDevice() != null) {
            // we have full system path. make relative to workspace
            schemaPathSimple = schemaPathSimple.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation());
        }
        return schemaPathSimple;
    }

    public String getSchemaToWsdlRelativeLocation(final IPath schemaLocation, final IPath wsdlLocationPath) {
        return schemaLocation.removeLastSegments(1).makeRelativeTo(wsdlLocationPath.removeLastSegments(1)).toString();
    }
}
