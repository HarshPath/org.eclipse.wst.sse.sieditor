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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public interface IElementStrategyTest {
    void testSetInput();

    void testGetName();
    
    void testSetName();
    
    void testIsNameApplicable();
    
    void testIsNameEditable();
    
    void testGetNamespace();
    
    void testSetNamespace();
    
    void testIsNamespaceApplicable();
    
    void testIsNamespaceEditable();
    
    void testGetNillable();
    
    void testSetNillable();
    
    void testIsNillableApplicable();
    
    void testIsNillableEditable();
    
    void testGetCardinality();
    
    void testSetCardinality();
    
    void testIsCardinalityApplicable();
    
    void testIsCardinalityEditable();
    
    void testIsConstraintsSectionApplicable();
    
    void testGetConstraintsSectionController();
    
    void testGetType();
    
    void testSetType();
    
    void testIsTypeApplicable();

    void testIsTypeEditable();

    void testGetBaseType();

    void testSetBaseType();

    void testIsBaseTypeApplicable();

    void testIsBaseTypeEditable();

}
