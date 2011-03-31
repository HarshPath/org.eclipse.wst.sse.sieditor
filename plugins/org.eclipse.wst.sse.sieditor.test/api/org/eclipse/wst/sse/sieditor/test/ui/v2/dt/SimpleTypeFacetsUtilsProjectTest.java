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
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.SimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

public class SimpleTypeFacetsUtilsProjectTest extends SIEditorBaseTest {

    @Test(timeout = 3000)
    public void testCircularSimpleTypeDependencies() throws Exception {
        final IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/simple/NewWSDLFileCyclingTypes.wsdl", "NewWSDLFileCyclingTypes.wsdl");

        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://namespace2")[0];
        // SimpleType4 is using a type which has circular dependency to other
        // types
        final ISimpleType simpleType4 = (ISimpleType) schema.getType(false, "SimpleType4");

        // All methods below must pass without endless loop
        SimpleTypeFacetsUtils.instance().areLengthFacetsSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().areExclusiveFacetsSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().areInclusiveFacetsSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().isEnumerationFacetSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().isWhitespaceFacetSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().isTotalDigitsFacetSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().isFractionDigitsFacetSupported((XSDSimpleTypeDefinition) simpleType4.getComponent());
        SimpleTypeFacetsUtils.instance().isPatternFacetSupported(simpleType4);
    }

}
