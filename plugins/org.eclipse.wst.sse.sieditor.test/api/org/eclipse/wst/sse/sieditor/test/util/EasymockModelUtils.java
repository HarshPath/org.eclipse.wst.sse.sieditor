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
package org.eclipse.wst.sse.sieditor.test.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class EasymockModelUtils {


    public static IDescription createDefinitionMockFromSameModel() {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IDescription modelObject = createNiceMock(IDescription.class);
        final Definition eObject = createMock(Definition.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static ISchema createISchemaMockFromSameModel() {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final ISchema modelObject = createNiceMock(ISchema.class);
        final XSDSchema eObject = createMock(XSDSchema.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static ISimpleType createISimpleTypeMockFromSameModel() {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final ISimpleType modelObject = createNiceMock(ISimpleType.class);
        final XSDNamedComponent eObject = createMock(XSDNamedComponent.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static IParameter createIParameterTypeMockFromSameModel() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final IParameter modelObject = createNiceMock(IParameter.class);
        final Part eObject = createNiceMock(Part.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static IOperation createIOperationTypeMockFromSameModel() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final IOperation modelObject = createNiceMock(IOperation.class);
        final EObject eObject = createMock(EObject.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static IServiceInterface createIServiceInterfaceMockFromSameModel() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final IServiceInterface modelObject = createNiceMock(IServiceInterface.class);
        final PortType eObject = createMock(PortType.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static INamedObject createINamedObjectMockFromSameModel() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final INamedObject modelObject = createNiceMock(INamedObject.class);
        final EObject eObject = createMock(EObject.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static IElement createIElementMockFromSameModel() {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IElement modelObject = createNiceMock(IElement.class);
        final XSDConcreteComponent eObject = createMock(XSDConcreteComponent.class);
        expect(eObject.eAdapters()).andReturn(new SimpleEList<Adapter>()).anyTimes();
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        EasymockModelUtils.addComponentAndContainerCalls(modelObject, eObject);
                
        org.easymock.EasyMock.replay(modelRoot, eObject);
        return modelObject;
    }
    
    public static IElement createReferingIElementMockFromSameModel() {
        final IElement modelObject = createNiceMock(IElement.class);
        recordReferingIElementMockFromSameModel(modelObject);
        return modelObject;
    }
    
    public static void recordReferingIElementMockFromSameModel(IElement modelObject) {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final XSDParticle eObject = createMock(XSDParticle.class);
        XSDElementDeclaration elementDeclaration = createMock(XSDElementDeclaration.class);
        expect(elementDeclaration.isElementDeclarationReference()).andReturn(true).anyTimes();
        expect(eObject.eAdapters()).andReturn(new SimpleEList<Adapter>()).anyTimes();
        expect(eObject.getContent()).andReturn(elementDeclaration).anyTimes();
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        addComponentAndContainerCalls(modelObject, eObject);
        
        org.easymock.EasyMock.replay(modelRoot, eObject, elementDeclaration);
    }
    
    public static IStructureType createIStructureTypeMockFromSameModel() {
        final IStructureType modelObject = createNiceMock(IStructureType.class);
        recordIStructureTypeMockFromSameModel(modelObject, false);
        return modelObject;
    }
    
    public static IStructureType createIStructureTypeMockFromSameModel(boolean isReference) {
        final IStructureType modelObject = createNiceMock(IStructureType.class);
        recordIStructureTypeMockFromSameModel(modelObject, isReference);
        return modelObject;
    }
    
    public static void recordIStructureTypeMockFromSameModel(IStructureType modelObject, boolean isReference) {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        XSDAttributeDeclaration elementDeclaration = createMock(XSDAttributeDeclaration.class);
        expect(elementDeclaration.isAttributeDeclarationReference()).andReturn(isReference).anyTimes();
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(elementDeclaration).anyTimes();
        
        org.easymock.EasyMock.replay(modelRoot, elementDeclaration);
    }
    
    public static void addComponentAndContainerCalls(IModelObject modelObject, Class<? extends EObject> returnEObjectClass) {
        EObject container = createMock(EObject.class);
        replay(container);
        EObject component = createMock(returnEObjectClass);
		expect(component.eContainer()).andReturn(container).anyTimes();
		replay(component);

		expect(modelObject.getComponent()).andReturn(component).anyTimes();
    }
    
    public static void addComponentAndContainerCalls(IModelObject modelObject, EObject component) {
        EObject container = createMock(EObject.class);
        replay(container);
		expect(component.eContainer()).andReturn(container).anyTimes();

		expect(modelObject.getComponent()).andReturn(component).anyTimes();
    }
}
