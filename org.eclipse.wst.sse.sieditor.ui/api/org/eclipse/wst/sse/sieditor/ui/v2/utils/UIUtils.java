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
package org.eclipse.wst.sse.sieditor.ui.v2.utils;

import java.util.Collection;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;

public class UIUtils {

    private static final UIUtils INSTANCE = new UIUtils();

    private UIUtils() {

    }

    public static UIUtils instance() {
        return INSTANCE;
    }

    public String getDisplayName(final INamedObject namedObject) {
        return namedObject.getName() == null ? UIConstants.EMPTY_STRING : namedObject.getName();
    }

    /**
     * utility method
     * 
     * @param node
     *            - the node to get the parameter for
     * @return the {@link IParameter} of the given tree node
     */
    public IParameter getParameterFromUINode(final ITreeNode node) {
        final IModelObject modelObject = node.getModelObject();
        if (node instanceof ParameterNode) {
            return (IParameter) modelObject;
        }

        if (node instanceof FaultNode) {
            final Collection<IParameter> parameters = ((IFault) modelObject).getParameters();
            if (parameters.isEmpty()) {
                return null;
            }
            return parameters.iterator().next();
        }

        return null;
    }

    /**
     * Retrieves the type, standing behind the type names in the
     * {@link #getCommonTypesDropDownList()}
     * 
     * @param typeName
     *            the type name - must be one from the String[] return from the
     *            method mentioned above. If null or empty string- null is
     *            returned
     * @return the Type, the typeName is representing, or null if such is not
     *         found, or name is null
     */
    public IType getCommonTypeByName(final String typeName) {
        if (typeName != null && (typeName.trim().length() != 0)) {
            return BuiltinTypesHelper.getInstance().getCommonBuiltinType(typeName);
        }
        return null;
    }

}
