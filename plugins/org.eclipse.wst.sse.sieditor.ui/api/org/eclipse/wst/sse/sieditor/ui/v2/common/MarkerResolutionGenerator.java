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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.validation.impl.MarkerUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.quickFix.MissingSchemaElementQuickFix;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.quickFix.MissingSchemaForSchemaQuickFix;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator {

    public IMarkerResolution[] getResolutions(final IMarker marker) {

        Object modelObject = null;
        try {
            modelObject = MarkerUtils.getObject((String) marker.getAttribute(EValidator.URI_ATTRIBUTE));
        } catch (CoreException e) {
            Logger.logError("quick fix encountered a problem", e); //$NON-NLS-1$
            return new IMarkerResolution[0];
        }
        // this is supported for now
        if (modelObject instanceof ISchema) {
            final ISchema schema = (ISchema) modelObject;
            if (EmfXsdUtils.isSchemaElementMissing(schema)) {
                return new IMarkerResolution[] { new MissingSchemaElementQuickFix(schema) };
            }
            if (EmfXsdUtils.isSchemaForSchemaMissing(schema)) {

                return new IMarkerResolution[] { new MissingSchemaForSchemaQuickFix(
                        Messages.MarkerResolutionGenerator_QuickFixLbl0, schema) };
            }
        }
        return new IMarkerResolution[0];
    }

}
