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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class SchemaNode {

    private final String namespace;

    private final List<SchemaNode> imports;

    private IPath path;

    private IFile iFile;

    private String filename;

    private ISchema schema;

    public SchemaNode(final String namespace) {
        this.namespace = namespace;
        this.imports = new LinkedList<SchemaNode>();
    }

    public void addImport(final SchemaNode schemaImport) {
        if (!imports.contains(schemaImport)) {
            imports.add(schemaImport);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public List<SchemaNode> getImports() {
        return Collections.unmodifiableList(imports);
    }

    public IPath getPath() {
        return path;
    }

    public void setPath(final IPath newPath) {
        this.path = newPath;
        this.updateFile();
    }

    public void updateImportsPaths() {
        final Set<SchemaNode> schemaDependencies = SchemaDependenciesUtils.instance().getSchemaDependencies(this);
        for (final SchemaNode dependency : schemaDependencies) {
            dependency.setPath(getPath());
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
        this.updateFile();
    }

    private void updateFile() {
        if (getPath() != null && getFilename() != null) {
            iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(getFullPath());
        }
    }

    public IPath getFullPath() {
        return getPath().append(getFilename());
    }

    public ISchema getSchema() {
        return schema;
    }

    public void setSchema(final ISchema schema) {
        this.schema = schema;
    }

    public IFile getIFile() {
        return iFile;
    }

}
