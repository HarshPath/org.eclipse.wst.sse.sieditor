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
 *    Tsvetan Stoyanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;

/**
 * &lt;element name="something"&gt;
 * 	&lt;type_declaration&gt;
 * &lt;/element&gt;
 * 
 *
 */
public class ElementOfAnonymousTypeStrategy extends ElementOfGlobalTypeStrategy{

	public ElementOfAnonymousTypeStrategy(IDataTypesFormPageController formPageController) {
		super(formPageController);
	}

	@Override
	public boolean isConstraintsSectionApplicable() {
		return getType() instanceof ISimpleType;
	}
	
	@Override
	public IConstraintsController getConstraintsSectionController() {
		if (constraintsController == null) {
			constraintsController = new SimpleTypeConstraintsController(formPageController);
			constraintsController.setType((ISimpleType) getType());
		}
		return constraintsController;
	}
	
	@Override
	public boolean isBaseTypeApplicable() {
		return false;
	}
	
	@Override
	public IType getBaseType() {
		return getType().getBaseType();
	}
	
	@Override
	public void setBaseType(IType baseType) {
		if (isBaseTypeApplicable() && !baseType.equals(getBaseType())) {
			formPageController.setSimpleTypeBaseType((ISimpleType) getType(), (ISimpleType) baseType);
		}
	}

}
