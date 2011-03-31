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

import org.easymock.EasyMock;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.utils.SimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

/**
 * 
 *
 * 
 */
public class SimpleTypeFacetsUtilsPatternsTest {

    /**
     * Tests whether the SimpleTypeFacets.isPatternFacetSupported(type) method
     * will return <code>false</code> for unresolved base type.
     */
    @Test
    public void testUnsupportedPatternFacet() {
        final ISimpleType type = EasyMock.createMock(ISimpleType.class);
        EasyMock.expect(type.getNamespace()).andReturn("some_namespace").anyTimes();
        EasyMock.expect(type.getBaseType()).andReturn(null).anyTimes();

        EasyMock.replay(type);

        Assert.assertFalse(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(type));

        EasyMock.verify(type);
    }

    /**
     * Tests whether the SimpleTypeFacets.isPatternFacetSupported(type) method
     * will return <code>true</code> for resolved base type.
     */
    @Test
    public void testSupportedPatternFacet() {
        final ISimpleType type = EasyMock.createMock(ISimpleType.class);
        EasyMock.expect(type.getNamespace()).andReturn("some_namespace").anyTimes();

        final ISimpleType baseType = EasyMock.createMock(ISimpleType.class);
        EasyMock.expect(baseType.getNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).anyTimes();
        EasyMock.expect(type.getBaseType()).andReturn(baseType).anyTimes();

        EasyMock.replay(type, baseType);

        Assert.assertTrue(SimpleTypeFacetsUtils.instance().isPatternFacetSupported(type));

        EasyMock.verify(type, baseType);
    }

}
