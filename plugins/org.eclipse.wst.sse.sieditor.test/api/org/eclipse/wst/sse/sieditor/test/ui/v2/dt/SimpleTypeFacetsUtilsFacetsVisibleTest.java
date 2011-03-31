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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.SimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

/**
 * 
 *
 * 
 */
public class SimpleTypeFacetsUtilsFacetsVisibleTest extends SIEditorBaseTest {

    private IWsdlModelRoot wsdlModelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (wsdlModelRoot == null) {
            wsdlModelRoot = getWSDLModelRoot("pub/simple/SimpleTypeFacetsTestWSDLFile.wsdl", "SimpleTypeFacetsTestWSDLFile.wsdl");
        }
    }

    @Test
    public void testSimpleTypeFromStringVisibleFacets() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/SimpleTypeFacetsTestWSDLFile/")[0];
        final ISimpleType simpleTypeFromString = (ISimpleType) schema.getType(false, "SimpleTypeFromString");

        assertTrue(SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areLengthFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areExclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromString.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleTypeFromString));
    }

    @Test
    public void testSimpleTypeFromIntVisibleFacets() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/SimpleTypeFacetsTestWSDLFile/")[0];
        final ISimpleType simpleTypeFromInt = (ISimpleType) schema.getType(false, "SimpleTypeFromInt");

        assertTrue(SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areLengthFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areExclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromInt.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleTypeFromInt));
    }

    @Test
    public void testSimpleTypeFromBooleanVisibleFacets() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/SimpleTypeFacetsTestWSDLFile/")[0];
        final ISimpleType simpleTypeFromBoolean = (ISimpleType) schema.getType(false, "SimpleTypeFromBoolean");

        assertTrue(SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areLengthFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromBoolean.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleTypeFromBoolean));
    }

    @Test
    public void testSimpleTypeFromDateVisibleFacets() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/SimpleTypeFacetsTestWSDLFile/")[0];
        final ISimpleType simpleTypeFromDate = (ISimpleType) schema.getType(false, "SimpleTypeFromDate");

        assertTrue(SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areLengthFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areExclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromDate.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleTypeFromDate));
    }

    @Test
    public void testSimpleTypeFromFloatVisibleFacets() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/SimpleTypeFacetsTestWSDLFile/")[0];
        final ISimpleType simpleTypeFromFloat = (ISimpleType) schema.getType(false, "SimpleTypeFromFloat");

        assertTrue(SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().areLengthFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().areExclusiveFacetsSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertFalse(SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported(
                (XSDSimpleTypeDefinition) simpleTypeFromFloat.getComponent()));
        assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleTypeFromFloat));
    }

}
