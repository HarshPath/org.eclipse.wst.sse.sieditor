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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

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
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ui.internal.adapters.commands.W11TopLevelElementCommand;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 * Command creating default &lt?xml.. &ltdefinitions&gt wsdl element in a
 * corrupted or empty document. <br>
 * <br>
 * note: if included in a composite command execution in separate transaction
 * should be preffered, due to custom implementation of undo/redo
 * 
 * 
 * 
 */
public class EnsureDefinitionCommand extends AbstractNotificationOperation {

    private final boolean hasXmlTag;
    private final boolean hasDefinitionsElement;

    public EnsureDefinitionCommand(final IModelRoot root, final IDescription modelObject, final String operationLabel) {
        super(root, modelObject, operationLabel);
        final Definition definitions = modelObject.getComponent();
        hasDefinitionsElement = definitions.getElement() != null;
        hasXmlTag = EmfXsdUtils.hasXmlTag(definitions.getDocument());
    }

    @Override
    public boolean canExecute() {
        final IDescription iDescription = (IDescription) getModelObjects()[0];
        if (iDescription == null) {
            return false;
        }
        final Definition component = iDescription.getComponent();
        return (component != null) && (component.getDocument() != null) && !hasDefinitionsElement && super.canExecute();
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final Definition definition = ((IDescription) getModelObjects()[0]).getComponent();
        if (!hasXmlTag) {
            ensureXmlDirective(definition);
        }
        W11TopLevelElementCommand.ensureDefinition(definition);

        if (!hasDefinitionsElement)
            definition.eNotify(new ENotificationImpl((InternalEObject) definition, Notification.ADD, null, null, definition));
        return Status.OK_STATUS;
    }

    // =========================================================
    // eclipse bug 321851 workarounds:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=321851
    // =========================================================

    private void ensureXmlDirective(final Definition definition) {
        Node firstChild = definition.getDocument().getFirstChild();
        ProcessingInstruction xmlDeclaration = getXMLDeclaration(definition.getDocument());
        definition.getDocument().insertBefore(xmlDeclaration, firstChild);

        // explicitly fire an ADD event for the added xml directive
        definition.eNotify(new ENotificationImpl((InternalEObject) definition, Notification.ADD, null, null, xmlDeclaration));
    }

    /**
     * this method is copy of {@link BaseCommand#getXMLDeclaration}
     * 
     * @param document
     * @return
     */
    private static ProcessingInstruction getXMLDeclaration(Document document) {
        Preferences preference = XMLCorePlugin.getDefault().getPluginPreferences();
        String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
        if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
            charSet = "UTF-8"; //$NON-NLS-1$
        }
        ProcessingInstruction xmlDeclaration = document.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"" //$NON-NLS-1$ //$NON-NLS-2$
                + charSet + "\""); //$NON-NLS-1$
        return xmlDeclaration;
    }

}
