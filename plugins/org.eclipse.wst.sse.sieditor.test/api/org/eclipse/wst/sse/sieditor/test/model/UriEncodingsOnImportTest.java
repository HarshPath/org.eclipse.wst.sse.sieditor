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
package org.eclipse.wst.sse.sieditor.test.model;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class UriEncodingsOnImportTest extends SIEditorBaseTest {

    @Test
    public void test() throws ExecutionException {
        IWsdlModelRoot wsdlModelRoot = null;
        IXSDModelRoot xsdModelRoot = null;
        try {
            wsdlModelRoot = getWSDLModelRoot("pub/self/mix/TypesInternalImporting.wsdl", "TypesInternalImporting.wsdl");
            xsdModelRoot = getXSDModelRoot("pub/xsd/example2.xsd", "test with space.xsd");
        } catch (Exception e) {
            fail(e.toString());
        }

        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.com/")[0];

        IStructureType structure = (IStructureType) schema.getType(false, "Address");
        IElement element = structure.getElements("name").iterator().next();
        IType type = xsdModelRoot.getSchema().getType(false, "Address");

        SetElementTypeCommand setElementTypeCommand = new SetElementTypeCommand(wsdlModelRoot, element, type);
        wsdlModelRoot.getEnv().execute(setElementTypeCommand);
        Assert.assertNotSame(UnresolvedType.instance(), element.getType());
    }
}
