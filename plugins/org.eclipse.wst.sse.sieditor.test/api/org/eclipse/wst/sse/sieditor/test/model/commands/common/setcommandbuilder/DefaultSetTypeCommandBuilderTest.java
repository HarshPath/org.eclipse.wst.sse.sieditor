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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.setcommandbuilder;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.DefaultSetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeBaseTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class DefaultSetTypeCommandBuilderTest {

    private DefaultSetTypeCommandBuilder commandBuilder;

    @Test
    public void createSetTypeCommand() {
        final IXSDModelRoot rootMock = createMock(IXSDModelRoot.class);
        final IEnvironment envMock = createMock(IEnvironment.class);
        expect(rootMock.getEnv()).andReturn(envMock).anyTimes();
        expect(envMock.getEditingDomain()).andReturn(null).anyTimes();
        
        final IWsdlModelRoot wsdlModelRoot = createMock(IWsdlModelRoot.class);
        expect(wsdlModelRoot.getEnv()).andReturn(envMock);
        
        replay(rootMock, envMock, wsdlModelRoot);
        
        final IElement element = createMock(IElement.class);
        expect(element.getModelRoot()).andReturn(rootMock);
        replay(element);
        commandBuilder = new DefaultSetTypeCommandBuilder(element);

        assertTrue(commandBuilder.createSetTypeCommand(null) instanceof SetElementTypeCommand);

        final IStructureType structureType1 = createMock(IStructureType.class);
        expect(structureType1.getModelRoot()).andReturn(rootMock);
        expect(structureType1.isElement()).andReturn(true);
        replay(structureType1);
        commandBuilder = new DefaultSetTypeCommandBuilder(structureType1);
        assertTrue(commandBuilder.createSetTypeCommand(null) instanceof SetStructureTypeCommand);
        
        final IStructureType structureType2 = createMock(IStructureType.class);
        expect(structureType2.getModelRoot()).andReturn(rootMock);
        expect(structureType2.isElement()).andReturn(false).anyTimes();
        replay(structureType2);
        commandBuilder = new DefaultSetTypeCommandBuilder(structureType2);
        assertTrue(commandBuilder.createSetTypeCommand(null) instanceof SetStructureTypeBaseTypeCompositeCommand);
        
        final ISimpleType simpleType = createMock(ISimpleType.class);
        expect(simpleType.getModelRoot()).andReturn(rootMock);
        replay(simpleType);
        commandBuilder = new DefaultSetTypeCommandBuilder(simpleType);
        assertTrue(commandBuilder.createSetTypeCommand(null) instanceof SetBaseTypeCommand);
        
        final IParameter parameter = createMock(IParameter.class);
        expect(parameter.getModelRoot()).andReturn(rootMock);
        replay(parameter);
        commandBuilder = new DefaultSetTypeCommandBuilder(parameter);
        assertTrue(commandBuilder.createSetTypeCommand(null) instanceof SetParameterTypeCommand);
        
    }

}
