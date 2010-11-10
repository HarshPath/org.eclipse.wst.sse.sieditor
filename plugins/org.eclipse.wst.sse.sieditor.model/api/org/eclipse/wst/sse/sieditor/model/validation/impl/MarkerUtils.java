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
package org.eclipse.wst.sse.sieditor.model.validation.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.validation.marker.MarkerUtil;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.xsd.XSDDiagnostic;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;

public final class MarkerUtils {
    public static final String VALIDATION_MARKER_TYPE = "org.eclipse.wst.sse.sieditor.model.validation.problem"; //$NON-NLS-1$

    private static final String PLATFORM_SCHEME = "platform"; //$NON-NLS-1$
    private static final String FILE_SCHEME = "file"; //$NON-NLS-1$
    private static final String RESOURCE_SEGMENT = "resource"; //$NON-NLS-1$
    private static Map<String, WeakReference<Object>> modelObjectsToURI = new HashMap<String, WeakReference<Object>>();

    public static void updateMarkers(List<IValidationStatus> validationStatusList, Collection<String> locationUris) {
        for (String uri : locationUris) {
            modelObjectsToURI.remove(uri);
        }
        for (Iterator<Entry<String, WeakReference<Object>>> iter = modelObjectsToURI.entrySet().iterator(); iter.hasNext();) {
            if (iter.next().getValue().get() == null) {
                iter.remove();
            }
        }
        try {

            for (String locationUri : locationUris) {
                final IFile f = getFile(locationUri);
                if (f != null && f.exists()) {
                    f.deleteMarkers(VALIDATION_MARKER_TYPE, false, IResource.DEPTH_ZERO);
                }
            }
            final IStatus markerStatus = convertToMarkerStatus(validationStatusList);
            if (!markerStatus.isOK()) {
                MarkerUtil.updateMarkers(markerStatus, VALIDATION_MARKER_TYPE, null);
                return;
            }
        } catch (CoreException e) {
            Logger.log(e.getStatus());
        }
    }

    public static IFile getFile(String location) {
        final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(location);
        return getFile(uri);
    }

    public static IFile getFile(URI uri) {

        if (PLATFORM_SCHEME.equals(uri.scheme()) && uri.segmentCount() > 1 && RESOURCE_SEGMENT.equals(uri.segment(0))) {
            StringBuffer platformResourcePath = new StringBuffer();
            for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
                platformResourcePath.append('/');
                platformResourcePath.append(org.eclipse.emf.common.util.URI.decode(uri.segment(j)));
            }

            try {
                return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
            } catch (Exception e) {
                Logger.logWarning(e.getMessage(), e);
            }
        } else if (FILE_SCHEME.equals(uri.scheme())) {
            StringBuffer fileResourcePath = new StringBuffer();
            for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
                fileResourcePath.append('/');
                fileResourcePath.append(org.eclipse.emf.common.util.URI.decode(uri.segment(j)));
            }

            try {
                return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileResourcePath.toString()));
            } catch (Exception e) {
                Logger.logWarning(e.getMessage(), e);
            }
        }

        return null;
    }

    private static IStatus convertToMarkerStatus(List<IValidationStatus> validationStatusList) {
        if (validationStatusList.isEmpty()) {
            return Status.OK_STATUS;
        }

        int code = 0;

        final List<IStatus> children = new ArrayList<IStatus>(validationStatusList.size());
        for (IValidationStatus current : validationStatusList) {
            final IConstraintStatus sourceStatus = current.getSourceStatus();
            code = sourceStatus.getCode();
            final EObject statusTarget = sourceStatus.getTarget();

            modelObjectsToURI.put(EcoreUtil.getURI(statusTarget).toString(), new WeakReference<Object>(current.getTarget()));
            // if (statusTarget.eResource() == null) {
            // // Avoid NPE with EMF marker utility
            // continue;
            // }

            children.add(new ConstraintStatus(sourceStatus.getConstraint(),
                    statusTarget instanceof XSDDiagnostic ? ((XSDDiagnostic) statusTarget).getPrimaryComponent() : statusTarget,
                    sourceStatus.getSeverity(), code, sourceStatus.getMessage(), sourceStatus.getResultLocus()));
        }

        return new MultiStatus(Activator.PLUGIN_ID, code, children.toArray(new IStatus[children.size()]), null, null);
    }

    /**
     * 
     * @param uri
     *            eObject's uri
     * @return the IModelObject corresponding to the eObject with this uri
     */
    public static Object getObject(String uri) {
        WeakReference<Object> reference = modelObjectsToURI.get(uri);
        return reference == null ? null : reference.get();
    }
}
