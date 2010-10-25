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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * Updates documentation for WSDL Components
 * 
 * 
 * 
 */
public class SetDocumentationCommand extends AbstractNotificationOperation {

    private final Element _component;
    private final String _text;
    private Element documentationElement;

    public SetDocumentationCommand(final IModelRoot root, final IModelObject modelObject, final Element componentElement,
            final String text) {
        super(root, modelObject, Messages.SetDocumentationCommand_set_documentation_command_label);
        this._component = componentElement;
        this._text = text;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        WSDLElement wsdlElement = (WSDLElement) modelObject.getComponent();

        documentationElement = wsdlElement.getDocumentationElement();
        if (documentationElement != null && documentationElement.getParentNode() == _component) {
            _component.removeChild(documentationElement);
            documentationElement = null;
        }

        if (_text.length() > 0) {
            final Document document = _component.getOwnerDocument();
            final String prefix = _component.getPrefix();
            final String documentElementName = prefix != null && prefix.length() > 0 ? prefix
                    + ":" + WSDLConstants.DOCUMENTATION_ELEMENT_TAG : WSDLConstants.DOCUMENTATION_ELEMENT_TAG;//$NON-NLS-1$
            documentationElement = document.createElement(documentElementName);
            _component.insertBefore(documentationElement, _component.getFirstChild());
            Text textNode = document.createTextNode(_text);
            documentationElement.appendChild(textNode);
        }

        wsdlElement.setDocumentationElement(documentationElement);

        return Status.OK_STATUS;
    }

    @Override
    public boolean canExecute() {
        return !(null == _text || null == _component);
    }

    public Element getDocumentationElement() {
        return documentationElement;
    }

}
