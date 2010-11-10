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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ExtractXmlSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaDependenciesUtils;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.IXmlSchemaExtractor;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.XmlSchemaExtractor;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ExtractNamespaceRunnable implements IRunnableWithProgress {

    private IStatus status;

    private final SchemaNode schemaNode;

    private final Set<SchemaNode> dependenciesSet;

    private final Map<String, String> filenamesMap;

    protected final boolean importExtractedSchemas;

    private final IPath wsdlLocationPath;

    private final String wsdlEncoding;

    public ExtractNamespaceRunnable(final SchemaNode schemaNode, final Set<SchemaNode> dependenciesSet,
            final boolean importExtractedSchemas, final IPath wsdlLocationPath) {
        this.schemaNode = schemaNode;
        this.dependenciesSet = dependenciesSet;
        this.filenamesMap = SchemaDependenciesUtils.instance().createFilenamesMap(schemaNode, dependenciesSet);
        this.importExtractedSchemas = importExtractedSchemas;

        String charset = null;
        try {
            charset = ResourcesPlugin.getWorkspace().getRoot().getFile(wsdlLocationPath).getCharset();
        } catch (final CoreException e) {
            charset = "UTF-8"; //$NON-NLS-1$
        }
        this.wsdlEncoding = charset;
        this.wsdlLocationPath = wsdlLocationPath;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        final Set<SchemaNode> schemasToExtract = new HashSet<SchemaNode>();
        schemasToExtract.add(schemaNode);
        schemasToExtract.addAll(dependenciesSet);

        try {
            status = extractSchemas(monitor, schemasToExtract, wsdlLocationPath, wsdlEncoding);
            if (status.getSeverity() == IStatus.CANCEL) {
                return;
            }
            status = importExtractedSchemas(monitor, schemasToExtract);

        } catch (final Exception e) {
            status = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, MessageFormat.format(
                    Messages.ExtractNamespaceRunnable_extract_xml_schema_error_status_msg, schemaNode.getNamespace(), e
                            .getLocalizedMessage()), e);
            rollbackExtraction(schemasToExtract, monitor);
            throw new InvocationTargetException(e);
        }

        monitor.done();
    }

    private IStatus extractSchemas(final IProgressMonitor monitor, final Set<SchemaNode> schemasToExtract,
            final IPath wsdlLocationPath, final String wsdlEncoding) throws IOException, CoreException {
        monitor.beginTask(Messages.ExtractNamespaceRunnable_extracting_xml_schema_subtask, dependenciesSet.size() + 1);

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        for (final SchemaNode node : schemasToExtract) {
            schemaExtractor().extractSchema(node.getIFile(), node.getSchema(), filenamesMap, wsdlLocationPath, wsdlEncoding);
            monitor.worked(1);

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
        }

        return Status.OK_STATUS;
    }

    protected IStatus importExtractedSchemas(final IProgressMonitor monitor, final Set<SchemaNode> schemasToExtract)
            throws ExecutionException {
        if (!importExtractedSchemas) {
            return Status.OK_STATUS;
        }

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        monitor.beginTask(Messages.ExtractNamespaceRunnable_importing_extracted_schemas_subtask, dependenciesSet.size() + 2);

        final ISchema schema = schemaNode.getSchema();
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) schema.getModelRoot().getRoot();

        return wsdlModelRoot.getEnv().execute(
                new ExtractXmlSchemaCompositeCommand(wsdlModelRoot, schema, schemasToExtract, monitor));
    }

    private void rollbackExtraction(final Set<SchemaNode> schemasToExtract, final IProgressMonitor monitor) {
        monitor.beginTask(Messages.ExtractNamespaceRunnable_rolling_back_extraction_subtask, schemasToExtract.size());

        for (final SchemaNode node : schemasToExtract) {
            try {
                node.getIFile().delete(true, new NullProgressMonitor());
            } catch (final CoreException ce) {
                Logger.logError("Exception during rollback of extract schema: " + ce.getMessage(), ce); //$NON-NLS-1$
            }
            monitor.worked(1);
        }

        monitor.done();
    }

    public IStatus getStatus() {
        return status;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected IXmlSchemaExtractor schemaExtractor() {
        return XmlSchemaExtractor.instance();
    }

}
