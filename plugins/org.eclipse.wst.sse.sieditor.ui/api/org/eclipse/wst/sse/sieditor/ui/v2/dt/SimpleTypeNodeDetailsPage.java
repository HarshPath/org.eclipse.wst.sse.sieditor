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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.wst.sse.sieditor.ui.v2.sections.ElementDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.SimpleTypeDetailsSection;

/**
 * This is the simple type node details page. It extends the
 * {@link ElementNodeDetailsPage}
 * 
 * 
 * 
 */
public class SimpleTypeNodeDetailsPage extends ElementNodeDetailsPage {

    public SimpleTypeNodeDetailsPage(final IDataTypesFormPageController controller, final ITypeDisplayer typeDisplayer) {
        super(controller, typeDisplayer);
    }

    @Override
    protected ElementDetailsSection createDetailsSection() {
        return new SimpleTypeDetailsSection(getDetailsController(), getToolkit(), getManagedForm(), getTypeDisplayer());
    }

}
