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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.KeyAttribute;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestKeyAttribute extends BasicConstraintTest {

    private static Definition okDefinition;
    private static Definition errDefinition;

    @BeforeClass
    public static void beforeClass() {
        okDefinition = loadDefinition("/data/validation/DuplicateKey_OK.wsdl");
        errDefinition = loadDefinition("/data/validation/DuplicateKey_ERR.wsdl");
    }

    @Test
    public void testBatchErr() throws Exception {
        IValidationContext validationContext = createValidationContext(true, errDefinition);
        KeyAttribute constraint = new KeyAttribute();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(10, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
            Set<EObject> resultLocus = ((ConstraintStatus) children[ndx]).getResultLocus();
            Assert.assertTrue("result locus does not contain expected location of the error", containsAny(resultLocus,
                    WSDLPackage.Literals.MESSAGE__QNAME, WSDLPackage.Literals.PORT_TYPE__QNAME,
                    WSDLPackage.Literals.BINDING__QNAME, WSDLPackage.Literals.SERVICE__QNAME,
                    WSDLPackage.Literals.IMPORT__NAMESPACE_URI));
        }
    }

    @Test
    public void testBatchOK() throws Exception {
        IValidationContext validationContext = createValidationContext(true, okDefinition);
        KeyAttribute constraint = new KeyAttribute();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(10, children.length);

        for (IStatus child : children) {
            Assert.assertEquals(IStatus.OK, child.getSeverity());
        }
    }

    @Test
    public void testLiveErr() throws Exception {
        for (EObject element : getElements(false)) {
            IValidationContext validationContext = createValidationContext(false, element);
            KeyAttribute constraint = new KeyAttribute();

            IStatus status = constraint.validate(validationContext);

            Assert.assertEquals(true, status.isMultiStatus());
            IStatus[] children = status.getChildren();
            Assert.assertEquals(2, children.length);

            Assert.assertEquals(IStatus.ERROR, children[0].getSeverity());
            Assert.assertEquals(IStatus.ERROR, children[1].getSeverity());
            super.tearDown();
        }
    }

    @Test
    public void testLiveOK() throws Exception {
        for (EObject element : getElements(true)) {
            IValidationContext validationContext = createValidationContext(false, element);
            KeyAttribute constraint = new KeyAttribute();

            IStatus status = constraint.validate(validationContext);

            Assert.assertEquals(true, status.isMultiStatus());
            IStatus[] children = status.getChildren();
            Assert.assertEquals(2, children.length);

            Assert.assertEquals(IStatus.OK, children[0].getSeverity());
            Assert.assertEquals(IStatus.OK, children[1].getSeverity());
            super.tearDown();
        }
    }

    @Test
    public void testBatchWithNonDefinition() throws Exception {
        EObject nonDefinitionElement = (EObject) okDefinition.getEMessages().get(0);
        IValidationContext validationContext = createValidationContext(true, nonDefinitionElement);
        KeyAttribute constraint = new KeyAttribute();

        IStatus status = constraint.validate(validationContext);
        Assert.assertNull(status);
    }

    @Test
    public void testLiveWithDefinition() throws Exception {
        IValidationContext validationContext = createValidationContext(false, okDefinition);
        KeyAttribute constraint = new KeyAttribute();

        IStatus status = constraint.validate(validationContext);
        Assert.assertNull(status);
    }

    @SuppressWarnings("unchecked")
    private Collection<EObject> getElements(boolean ok) {
        Collection<EObject> elements = new ArrayList<EObject>();

        Definition definition = ok ? okDefinition : errDefinition;

        int[] featureIds = { WSDLPackage.DEFINITION__EMESSAGES, WSDLPackage.DEFINITION__EPORT_TYPES,
                WSDLPackage.DEFINITION__EBINDINGS, WSDLPackage.DEFINITION__ESERVICES, WSDLPackage.DEFINITION__EIMPORTS };

        for (int featureId : featureIds) {
            EStructuralFeature feature = WSDLPackage.Literals.DEFINITION.getEStructuralFeature(featureId);
            Object object = definition.eGet(feature);
            elements.addAll(((List<EObject>) object));
        }

        return elements;
    }

}
