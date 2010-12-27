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
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;

public class ElementRefToGlobalElementStrategy extends ElementOfGlobalTypeStrategy{

	public ElementRefToGlobalElementStrategy(IDataTypesFormPageController formPageController) {
		super(formPageController);
	}
	
	@Override
	public String getName() {
		IStructureType type = (IStructureType) input.getType();
		return type.getName();
	}
	
	@Override
	public void setName(String name) {
		formPageController.renameType(input.getType(), name);
	}
	
	@Override
	public String getNamespace() {
		IStructureType type = (IStructureType) input.getType();
		return type.getNamespace();
	}
	
	@Override
	public IType getType() {
		return ((IStructureType) input.getType()).getType();
	}
	
	@Override
	public void setType(IType type) {
		if (type != getType()) {
			formPageController.setStructureType((IStructureType) input.getType(), type);
		}
	}
	
	@Override
	public boolean getNillable() {
		return ((IStructureType) input.getType()).isNillable();
	}
	
	@Override
	public void setNillable(boolean nillable) {
		if (getNillable() != nillable) {
			formPageController.setGlobalElementNillable((IStructureType) input.getType(), nillable);
		}
	}
	
	@Override
	public boolean isBaseTypeApplicable() {
		return getType() instanceof ISimpleType && getType().isAnonymous();
	}
	
	@Override
	public boolean isBaseTypeEditable() {
		return true;
	}
	
	@Override
	public IType getBaseType() {
		return getType().getBaseType();
	}
	
	@Override
	public void setBaseType(IType baseType) {
		IType currentType = getType();
		if (currentType.getBaseType() != baseType ) {
			formPageController.setSimpleTypeBaseType((ISimpleType) getType(), (ISimpleType) baseType);
		}
	}

}
