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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints.xsd;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.service.ConstraintExistsException;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.ValidXSDEntity;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


@SuppressWarnings("nls")
public class ValidXSDEntityTest extends BasicConstraintTest {

    private static Definition definition;

    @BeforeClass
    public static void loadWSDL() {
        definition = loadDefinition("/data/validation/validaitionServiceTest.wsdl");
    }

    @Test
    public void testOK() throws Exception {
        XSDSchema schema = (XSDSchema) definition.getETypes().getSchemas("http://www.example.org/").get(0);

        IValidationContext validationContext = createValidationContext(true, schema);
        ValidXSDEntity constraint = new ValidXSDEntity();
        Assert.assertEquals(true, constraint.validate(validationContext).isOK());
    }

    @Test
    public void testError() throws ConstraintExistsException {
        XSDSchema schema = (XSDSchema) definition.getETypes().getSchemas("http://namespace1").get(0);
        IValidationContext validationContext = createValidationContext(true, schema);
        ValidXSDEntity constraint = new ValidXSDEntity();
        Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
    }

    @Test
    public void testOkAfterFixingError() throws ConstraintExistsException {
        XSDSchema schema = (XSDSchema) definition.getETypes().getSchemas("http://namespace1").get(0);

        XSDComplexTypeDefinition resolveComplexTypeDefinition = schema.resolveComplexTypeDefinition("StructureType25543s4433");
        Assert.assertNotNull(resolveComplexTypeDefinition.eContainer());
        XSDAttributeDeclaration attributeDeclaration = ((XSDAttributeUse) resolveComplexTypeDefinition.getAttributeContents()
                .get(0)).getAttributeDeclaration();
        attributeDeclaration.setName("changedToValid");

        IValidationContext validationContext = createValidationContext(true, schema);
        ValidXSDEntity constraint = new ValidXSDEntity();
        Assert.assertEquals(true, constraint.validate(validationContext).isOK());
    }
}
