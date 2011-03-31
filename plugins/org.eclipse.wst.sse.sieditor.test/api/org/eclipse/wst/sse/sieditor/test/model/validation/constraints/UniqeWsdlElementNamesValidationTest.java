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

import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.service.ConstraintExistsException;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.UniqueFaultName;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.UniqueMessagePartName;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.UniqueOperationName;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.UniquePortName;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UniqeWsdlElementNamesValidationTest extends BasicConstraintTest {

    private static Definition okDefinition;
    private static Definition errDefinition;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        okDefinition = loadDefinition("/data/validation/DuplicateWSDLElementsOK.wsdl");
        errDefinition = loadDefinition("/data/validation/DuplicateWSDLElementsERR.wsdl");
    }

    @Test
    public void testDuplicateWsdlMessagePartNameError() throws ConstraintExistsException {
        EObject object = (EObject) errDefinition.getEMessages().get(0);
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueMessagePartName constraint = new UniqueMessagePartName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
            Set<EObject> resultLocus = ((ConstraintStatus) children[ndx]).getResultLocus();
            Assert.assertTrue("result locus does not contain expected location of the error", containsAny(resultLocus,
                    WSDLPackage.Literals.PART__NAME));
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlMessagePartNameOK() throws ConstraintExistsException {
        EObject object = (EObject) okDefinition.getEMessages().get(0);
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueMessagePartName constraint = new UniqueMessagePartName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.OK, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlFaultNameError() throws ConstraintExistsException {
        EObject object = (Operation) ((PortType) errDefinition.getPortType(new QName("http://sap.com/xi/APPL/SE/Global",
                "ServiceInterface2"))).getEOperations().get(0);
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueFaultName constraint = new UniqueFaultName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
            Set<EObject> resultLocus = ((ConstraintStatus) children[ndx]).getResultLocus();
            Assert.assertTrue("result locus does not contain expected location of the error", containsAny(resultLocus,
                    WSDLPackage.Literals.FAULT.getEStructuralFeature(WSDLPackage.FAULT__NAME)));

        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlFaultNameOK() throws ConstraintExistsException {
        EObject object = (Operation) ((PortType) errDefinition.getPortType(new QName("http://sap.com/xi/APPL/SE/Global",
                "ServiceInterface2"))).getEOperations().get(0);
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueFaultName constraint = new UniqueFaultName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlOperationNameError() throws ConstraintExistsException {
        EObject object = (PortType) errDefinition.getPortType(new QName("http://sap.com/xi/APPL/SE/Global", "ServiceInterface3"));
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueOperationName constraint = new UniqueOperationName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
            Set<EObject> resultLocus = ((ConstraintStatus) children[ndx]).getResultLocus();
            Assert.assertTrue("result locus does not contain expected location of the error", 
                    containsAny(resultLocus, WSDLPackage.Literals.OPERATION__NAME));
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlOperationNameOK() throws ConstraintExistsException {
        EObject object = (PortType) okDefinition.getPortType(new QName("http://sap.com/xi/APPL/SE/Global", "ServiceInterface3"));
        IValidationContext validationContext = createValidationContext(true, object);
        UniqueOperationName constraint = new UniqueOperationName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.OK, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlPortTypeError() throws ConstraintExistsException {
        IValidationContext validationContext = createValidationContext(true, errDefinition);
        UniquePortName constraint = new UniquePortName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.ERROR, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
            Set<EObject> resultLocus = ((ConstraintStatus) children[ndx]).getResultLocus();
            Assert.assertTrue("result locus does not contain expected location of the error", containsAny(resultLocus,
                    WSDLPackage.Literals.PORT__NAME));
        }
        super.tearDown();
    }

    @Test
    public void testDuplicateWsdlPortTypeNameOK() throws ConstraintExistsException {
        IValidationContext validationContext = createValidationContext(true, okDefinition);
        UniquePortName constraint = new UniquePortName();

        IStatus status = constraint.validate(validationContext);

        Assert.assertEquals(true, status.isMultiStatus());
        IStatus[] children = status.getChildren();
        Assert.assertEquals(2, children.length);

        for (int ndx = 0; ndx < children.length; ndx++) {
            Assert.assertEquals(IStatus.OK, children[ndx].getSeverity());
            Assert.assertTrue(children[ndx] instanceof ConstraintStatus);
        }
        super.tearDown();
    }

}
