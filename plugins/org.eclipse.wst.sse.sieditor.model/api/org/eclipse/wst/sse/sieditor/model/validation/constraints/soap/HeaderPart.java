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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class HeaderPart extends AbstractConstraint {

    @Override
    protected IStatus doValidate(final IValidationContext ctx) {
        final SOAPHeaderBase header = (SOAPHeaderBase) ctx.getTarget();

        final Message eMessage = header.getEMessage();

        if (eMessage != null) {
            if (header.getEPart() == null) {
                if (ElementAttributeUtils.hasAttributeValue(header.getElement(), "part")) { //$NON-NLS-1$
                    final String partAttribute = header.getElement().getAttribute("part"); //$NON-NLS-1$
                    if (header instanceof SOAPHeaderFault) {
                        return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderPart_1, partAttribute,
                                eMessage.getQName().getLocalPart());
                    }
                    return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderPart_2, partAttribute, eMessage
                            .getQName().getLocalPart());
                }
            }
        }
        return ConstraintStatus.createSuccessStatus(ctx, header, null);
    }

    @Override
    protected boolean shouldExecute(final IValidationContext ctx) {
        return true;
    }

}
