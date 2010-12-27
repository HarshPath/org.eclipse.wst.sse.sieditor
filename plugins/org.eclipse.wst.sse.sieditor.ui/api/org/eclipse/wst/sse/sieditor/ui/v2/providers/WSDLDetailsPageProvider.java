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
package org.eclipse.wst.sse.sieditor.ui.v2.providers;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.FaultDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.OperationDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ParameterDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ServiceInterfaceDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

public class WSDLDetailsPageProvider implements IDetailsPageProvider {

    protected final SIFormPageController controller;
    private final ITypeDisplayer typeDisplayer;

    public WSDLDetailsPageProvider(SIFormPageController controller, ITypeDisplayer typeDisplayer) {
        this.controller = controller;
        this.typeDisplayer = typeDisplayer;

    }

    // the pages are not cached here because a limited count of these is cashed
    // in the DetailsPart showing them - no sense of L2 cache, right?
    // @Override
    public IDetailsPage getPage(Object key) {
        if (ServiceInterfaceNode.class.equals(key)) {
            return new ServiceInterfaceDetailsPage(controller);
        } else if (OperationNode.class.equals(key)) {
            return new OperationDetailsPage(controller);
        } else if (ParameterNode.class.equals(key)) {
            return new ParameterDetailsPage(controller, typeDisplayer);
        } else if (FaultNode.class.equals(key)) {
            return new FaultDetailsPage(controller, typeDisplayer);
        } else {
            return null;
        }
    }

    // @Override
    public Object getPageKey(Object object) {
        if (object instanceof ITreeNode) {
            return object.getClass();
        }
        return null;
    }

}
