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
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

public class GlobalElementStrategy implements IElementStrategy{
	
	private IStructureType structureType;
	private final IDataTypesFormPageController formPageController;
	
	private IConstraintsController constraintsController;

	public GlobalElementStrategy(IDataTypesFormPageController formPageController) {
		this.formPageController = formPageController;
	}
	
	public void setInput(ITreeNode treeNode) {
	    IModelObject input = treeNode == null ? null : treeNode.getModelObject();
		structureType = (IStructureType) input;
		constraintsController = null;
	}

	public CardinalityType getCardinality() {
		return null;
	}

	public String getName() {
		return structureType.getName();
	}

	public String getNamespace() {
		return NamespaceNode.getNamespaceDisplayText(structureType.getNamespace());
	}

	public boolean getNillable() {
		return structureType.isNillable();
	}

	public IType getType() {
		return structureType.getType();
	}

	public boolean isCardinalityApplicable() {
		return false;
	}
	
	public boolean isCardinalityEditable() {
		return false;
	}

	public boolean isConstraintsSectionApplicable() {
		IType elementType = structureType.getType();
		return elementType instanceof ISimpleType;
	}

	public boolean isNameApplicable() {
		return true;
	}

	public boolean isNameEditable() {
		return true;
	}

	public boolean isNamespaceApplicable() {
		return true;
	}

	public boolean isNamespaceEditable() {
		return false;
	}

	public boolean isNillableApplicable() {
		return true;
	}

	public boolean isNillableEditable() {
		return true;
	}

	public void setCardinality(CardinalityType cardinality) {
		throw new IllegalStateException();
		
	}

	public void setName(String name) {
		if (!name.equals(getName())) {
			formPageController.renameType(structureType, name);
		}
	}

	public void setNamespace(String namespace) {
		throw new IllegalStateException();
	}

	public void setNillable(boolean nillable) {
		if (getNillable() != nillable) {
			formPageController.setGlobalElementNillable(structureType, nillable);
		}
	}
	
	public void setType(IType type) {
		if (getType() != type) {
			formPageController.setStructureType(structureType, type);
		}
	}
	
	public boolean isTypeApplicable() {
		return true;
	}
	
	public boolean isTypeEditable() {
		return true;
	}

	public IConstraintsController getConstraintsSectionController() {
		ISimpleType type = (ISimpleType) getType();
		if (constraintsController == null) {
			if (type.isAnonymous()) {
				constraintsController = new SimpleTypeConstraintsController(formPageController);
			} else {
				constraintsController = new ElementConstraintsController(formPageController, structureType);
			}
			constraintsController.setType((ISimpleType) structureType.getType());
		}
		 return constraintsController;
	}

	public IType getBaseType() {
		return getType().getBaseType();
	}

	public boolean isBaseTypeApplicable() {
		return false;
	}

	public boolean isBaseTypeEditable() {
		return isBaseTypeApplicable();
	}

	public void setBaseType(IType baseType) {
		if (getType().getBaseType() != baseType) {
			formPageController.setSimpleTypeBaseType((ISimpleType) getType(), (ISimpleType) baseType);
		}
	}

}
