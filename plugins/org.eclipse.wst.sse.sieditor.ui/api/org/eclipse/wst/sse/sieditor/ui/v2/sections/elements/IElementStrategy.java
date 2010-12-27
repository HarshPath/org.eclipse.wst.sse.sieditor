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

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

public interface IElementStrategy {
	
	void setInput(ITreeNode input);

	String getName();
	
	void setName(String name);
	
	boolean isNameApplicable();
	
	boolean isNameEditable();
	
	String getNamespace();
	
	void setNamespace(String namespace);
	
	boolean isNamespaceApplicable();
	
	boolean isNamespaceEditable();
	
	boolean getNillable();
	
	void setNillable(boolean nillable);
	
	boolean isNillableApplicable();
	
	boolean isNillableEditable();
	
	CardinalityType getCardinality();
	
	void setCardinality(CardinalityType cardinality);
	
	boolean isCardinalityApplicable();
	
	boolean isCardinalityEditable();
	
	boolean isConstraintsSectionApplicable();
	
	IConstraintsController getConstraintsSectionController();
	
	IType getType();
	
	void setType(IType type);
	
	boolean isTypeApplicable();

	boolean isTypeEditable();

	IType getBaseType();

	void setBaseType(IType type);

	boolean isBaseTypeApplicable();

	boolean isBaseTypeEditable();
}
