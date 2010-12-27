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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.SchemaLocationUtils;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

/**
 * Eclipse {@link IResource} utilitie's.
 * 
 */
public class ResourceUtils {

    // Eclipse resource ID for WSDL File
    public static final String WSDL_CONTENT_ID = "org.eclipse.wst.wsdl.wsdlsource"; //$NON-NLS-1$

    // Eclipse resource ID for XSD File
    public static final String XSD_CONTENT_ID = "org.eclipse.wst.xsd.core.xsdsource"; //$NON-NLS-1$

    public final static String PLATFORM_RESOURCE_PREFIX = "platform:/resource"; //$NON-NLS-1$

    protected static ResourceUtils instance = new ResourceUtils();

    protected IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    protected IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    public static void resetInstance() {
        instance = new ResourceUtils();
    }

    /**
     * Returns a {@link IFile} for given {@link IPath}
     * 
     * @param path
     * @return found file or null
     */
    public static final IFile getWorkSpaceFile(final IPath path) {
        Nil.checkNil(path, "path"); //$NON-NLS-1$
        IFile file = null;
        try {
            final IWorkspaceRoot workspaceRoot = instance.getWorkspaceRoot();
            file = getFileFromProject(path);
            if (file == null) {
                final URI encodedURI = URIHelper.createEncodedURI(path.toString());
                final IFile[] resources = workspaceRoot.findFilesForLocationURI(encodedURI);
                if (null != resources && resources.length > 0) {
                    file = resources[0];
                }
            }
        } catch (final URISyntaxException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not find file for location " + path, e); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not encode string" + path.toString(), e); //$NON-NLS-1$
        }
        return file;
    }

    /**
     * Checks whether the given {@link IFile} is of given ContentType
     * 
     * @param file
     *            - {@link IFile} object
     * @param contentId
     *            - eclipse resource type ID
     * @return
     */
    public static final boolean checkContentType(final IFile file, final String contentId) {
        if (file == null) {
            return false;
        }
        try {
            final IContentDescription description = file.getContentDescription();
            if (description != null) {
                final IContentType contentType = description.getContentType();
                if (contentType != null) {
                    contentType.isKindOf(null);
                    final IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
                    final IContentType paltformContentType = contentTypeManager.getContentType(contentId);
                    if (paltformContentType != null) {
                        if (contentType.isKindOf(paltformContentType)) {
                            return true;
                        }
                    }
                }
            }
        } catch (final CoreException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error resolving file Type for location ::" + //$NON-NLS-1$
                    file.getLocationURI().toString(), e);
            // Skip this
        }
        return false;
    }

    /**
     * Calculates a relative path for given {@link IFile} objects in the
     * workspace
     * 
     * @param wsdlLocation
     * @param handleFile
     * @return
     */
    public static String makeRelativeLocation(final String wsdlLocation, final IFile handleFile) {
        final String fileLocation = handleFile.getLocation().toString();
        final String relativeURI = constructURI(wsdlLocation, fileLocation).toString();
        return relativeURI;
    }

    /**
     * Calculates a relative path for given {@link IFile} objects in the
     * workspace
     * 
     * @param wsdlLocation
     * @param handleFile
     * @return GFB-POC modified to remove references to files TODO: this
     *         probably won't handle the general case;
     */
    public static String makeRelativeLocation(final String wsdlLocation, final URI uri) {
        String relativeURI = null;
        try {
            relativeURI = URIHelper.decodeURI(constructURI(wsdlLocation, URIHelper.decodeURI(uri)));
        } catch (final UnsupportedEncodingException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error constructing relative location for base:" + wsdlLocation + //$NON-NLS-1$
                    "; used:" + uri, e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return relativeURI;
    }

    /**
     * Construct relative path to WSDL, or return usedDefinition.getLocation()
     * 
     * @param parent
     *            can be null, then usedDefinition.getLocation() will be
     *            returned
     */
    public static URI constructURI(final Description parent, final Definition usedDefinition) {
        URI uri = null;

        try {
            final String wsdlLocation = parent == null ? "" : URIHelper.decodeURI(parent.getLocation()); //$NON-NLS-1$
            uri = constructURI(wsdlLocation, usedDefinition.getLocation());
        } catch (final Exception e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error constructing URI for parent wsdl:" + parent + //$NON-NLS-1$
                    "; used wsdl:" + usedDefinition.getLocation(), e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return uri;
    }

    /**
     * Construct relative path to WSDL, or return
     * usedXsdSchema.getSchemaLocation()
     * 
     * @param parent
     *            can be null, then usedXsdSchema.getSchemaLocation() will be
     *            returned
     */
    public static URI constructURI(final Description parent, final XSDSchema usedXsdSchema) {
        URI uri = null;

        try {
            final String wsdlLocation = parent == null ? "" : URIHelper.decodeURI(parent.getComponent().getLocation()); //$NON-NLS-1$
            uri = constructURI(wsdlLocation, usedXsdSchema.getSchemaLocation());
        } catch (final Exception e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error constructing URI for parent:" + parent + //$NON-NLS-1$
                    "; schema:" + usedXsdSchema.getSchemaLocation(), e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return uri;
    }

    public static URI constructURI(final XSDSchema baseSchema, final XSDSchema usedSchema) {
        if (usedSchema.eResource() == null || baseSchema.eResource() == null) {
            if (usedSchema != null && usedSchema.getSchemaLocation() != null) {
                final String relativeLocation = SchemaLocationUtils.instance().getSchemaToWsdlRelativeLocation(
                        new Path(usedSchema.getSchemaLocation()), new Path(baseSchema.getSchemaLocation()));
                try {
                    return new URI(new Path(relativeLocation, new Path(usedSchema.getSchemaLocation()).lastSegment()).toString());
                } catch (final URISyntaxException e) {
                    return null;
                }
            }
            return null;
        }

        final String baseURI = baseSchema.eResource().getURI().toString();
        final String usedURI = usedSchema.eResource().getURI().toString();

        return constructURI(baseURI, usedURI);
    }

    public static URI constructURI(final String baseURI, final String usedURI) {
        URI uri = null;

        try {
            String baseLocation = baseURI == null ? "" : URIHelper.decodeURI(baseURI); //$NON-NLS-1$
            String usedLocation = URIHelper.decodeURI(usedURI);
            if (usedLocation.equals(baseLocation)) {
                return URIUtil.fromString(""); //$NON-NLS-1$
            }

            final IFile baseFile = getWorkSpaceFile(new Path(baseURI));
            final IFile usedFile = getWorkSpaceFile(new Path(usedURI));

            if (baseFile != null && usedFile != null) {
                baseLocation = URIUtil.toUnencodedString(baseFile.getLocationURI());
                usedLocation = URIUtil.toUnencodedString(usedFile.getLocationURI());
            }

            final int baseFileIndex = baseLocation.lastIndexOf("/"); //$NON-NLS-1$
            if (baseFileIndex != -1) {
                baseLocation = baseLocation.substring(0, baseFileIndex);
            }

            final URI usedFileURI = URIUtil.fromString(usedLocation);
            final URI baseFileURI = URIUtil.fromString(baseLocation);

            uri = URIUtil.makeRelative(usedFileURI, baseFileURI);
        } catch (final Exception e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Error constructing URI for base:" + baseURI + //$NON-NLS-1$
                    "; used:" + usedURI, e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return uri;
    }

    private static IFile getFileFromProject(final IPath path) {
        final IWorkspaceRoot workspaceRoot = instance.getWorkspaceRoot();
        final String pathAsString = path.toString();

        if (pathAsString.indexOf(PLATFORM_RESOURCE_PREFIX) == -1) {
            return null;
        }
        final int platformIndex = pathAsString.indexOf(PLATFORM_RESOURCE_PREFIX);
        String projectName = pathAsString.substring(platformIndex + PLATFORM_RESOURCE_PREFIX.length() + 1);
        projectName = projectName.substring(0, projectName.indexOf("/")); //$NON-NLS-1$
        final IProject project = workspaceRoot.getProject(projectName);
        if (project == null) {
            return null;
        }
        final int filePathIndex = pathAsString.indexOf(projectName) + projectName.length();
        final String filePath = pathAsString.substring(filePathIndex);
        final IFile file = project.getFile(filePath);
        return file.exists() ? file : null;
    }
}
