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
/**
 * 
 */
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class EnsureSchemaElementCommand extends AbstractNotificationOperation {
    private final boolean hasXmlTag;
    private final boolean hasSchemaElement;

    /**
     * @param schema
     *            The model root in whos enviroment the commands are executed is
     *            retrieved from the schema.
     * @see AbstractNotificationOperation
     */
    public EnsureSchemaElementCommand(final ISchema schema, final String operationLabel) {
        super(schema.getModelRoot(), schema, operationLabel);
        final XSDSchema xsdSchema = schema.getComponent();
        hasXmlTag = EmfXsdUtils.hasXmlTag(xsdSchema.getDocument());
        hasSchemaElement = xsdSchema.getElement() != null;
    }

    @Override
    public boolean canExecute() {
        final ISchema schema = (ISchema) getModelObjects()[0];
        if (schema == null) {
            return false;
        }
        final XSDSchema component = schema.getComponent();
        return (component != null) && (component.getDocument() != null) && !hasSchemaElement && super.canExecute();
    }

    protected void patchModelAfterchange(final Node documentNode) {
        final HashSet<Node> nodesList = new HashSet<Node>();
        if (documentNode instanceof Document) {
            nodesList.add(documentNode);
            EmfModelPatcher.instance().patchEMFModelAfterDomChange((IXSDModelRoot) getModelRoot(), nodesList);
        }
    }

    @SuppressWarnings( { "restriction" })
    private static final class BaseCommandExposer extends org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand {
        public static void exposeEnsureSchemaElement(final XSDSchema schema) {
            ensureSchemaElement(schema);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation
     * #run(org.eclipse.core.runtime.IProgressMonitor,
     * org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final XSDSchema xsdSchema = ((ISchema) getModelObjects()[0]).getComponent();

        if (!hasXmlTag) {
            ensureXmlElement(xsdSchema);
        }

        BaseCommandExposer.exposeEnsureSchemaElement(xsdSchema);
        xsdSchema.elementChanged(xsdSchema.getElement());
        patchModelAfterchange(xsdSchema.getDocument());

        if (!hasSchemaElement)
            xsdSchema.eNotify(new ENotificationImpl((InternalEObject) xsdSchema, Notification.ADD, null, null, xsdSchema));
        return Status.OK_STATUS;
    }

    // =========================================================
    // eclipse bug 321851 workarounds:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=321851
    // =========================================================

    /**
     * this is a workaround for the BaseCommand#ensureSchemaElement method. this
     * method is first adding the <schema> element and then - the <?xml>
     * element. The <?xml> node is the first child node of the document, so this
     * was the cause of a large number of DOM change events.
     * 
     * the following lines of code are copy from BaseCommand#ensureXMLDirective
     * method
     */
    private void ensureXmlElement(final XSDSchema xsdSchema) {
        final Node firstChild = xsdSchema.getDocument().getFirstChild();
        final ProcessingInstruction xmlDeclaration = getXMLDeclaration(xsdSchema.getDocument());
        xsdSchema.getDocument().insertBefore(xmlDeclaration, firstChild);
        final Text textNode = xsdSchema.getDocument().createTextNode(System.getProperty("line.separator")); //$NON-NLS-1$
        xsdSchema.getDocument().insertBefore(textNode, firstChild);

        // explicitly fire an ADD event for the added xml directive
        xsdSchema.eNotify(new ENotificationImpl((InternalEObject) xsdSchema, Notification.ADD, null, null, xmlDeclaration));
    }

    /**
     * this method is copy of {@link BaseCommand#getXMLDeclaration}
     * 
     * @param document
     * @return
     */
    private static ProcessingInstruction getXMLDeclaration(final Document document) {
        final Preferences preference = XMLCorePlugin.getDefault().getPluginPreferences();
        String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
        if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
            charSet = "UTF-8"; //$NON-NLS-1$
        }
        final ProcessingInstruction xmlDeclaration = document.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"" //$NON-NLS-1$ //$NON-NLS-2$
                + charSet + "\""); //$NON-NLS-1$
        return xmlDeclaration;
    }

}
