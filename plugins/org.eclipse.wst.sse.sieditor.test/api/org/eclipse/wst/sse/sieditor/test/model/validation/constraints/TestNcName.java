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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintSeverity;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.ConstraintRegistry;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.NcName;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestNcName extends BasicConstraintTest{
	
	@Test
	public void testGetRequiredFeatures() {
		Map<EClass, EStructuralFeature[]> map = new HashMap<EClass, EStructuralFeature[]>();
		
		registerRequiredFeatures(map, WSDLPackage.Literals.MESSAGE, WSDLPackage.MESSAGE__QNAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.PART, WSDLPackage.PART__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.PORT_TYPE, WSDLPackage.PORT_TYPE__QNAME);
                registerRequiredFeatures(map, WSDLPackage.Literals.OPERATION, WSDLPackage.OPERATION__NAME);		
		registerRequiredFeatures(map, WSDLPackage.Literals.INPUT, WSDLPackage.INPUT__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.OUTPUT, WSDLPackage.OUTPUT__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.FAULT, WSDLPackage.FAULT__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.BINDING, WSDLPackage.BINDING__QNAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.BINDING_FAULT, WSDLPackage.BINDING_FAULT__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.BINDING_OPERATION, WSDLPackage.BINDING_OPERATION__NAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.SERVICE, WSDLPackage.SERVICE__QNAME);
		registerRequiredFeatures(map, WSDLPackage.Literals.PORT, WSDLPackage.PORT__NAME);
		registerRequiredFeatures(map, SOAPPackage.Literals.SOAP_FAULT, SOAPPackage.SOAP_FAULT__NAME);
                registerRequiredFeatures(map, WSDLPackage.Literals.DEFINITION, WSDLPackage.DEFINITION__QNAME);
		
		NcName c = new NcName();
		
		for (Entry<EClass, EStructuralFeature[]> entry : map.entrySet()) {
			EasyMockUtils.clean();
			EObject target = createTarget(entry.getKey());
			EasyMockUtils.replay();
			EStructuralFeature[] requiredFeatures = c.getRequiredFeatures(target);
			assertRequiredFeatures(entry.getKey(), requiredFeatures, entry.getValue());
		}
	}
	
	@Test
	public void testValidationOK() throws Exception{
		IValidationContext ctx = EasyMockUtils.createNiceMock(IValidationContext.class);
		final WSDLElement target = EasyMockUtils.createNiceMock(WSDLElement.class);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		final IModelConstraint constraint = EasyMockUtils.createNiceMock(IModelConstraint.class);
		
		IConstraintDescriptor descriptor = EasyMockUtils.createNiceMock(IConstraintDescriptor.class);
		
		EasyMock.expect(domElement.getLocalName()).andStubReturn("message");
		EasyMock.expect(target.eClass()).andStubReturn(WSDLPackage.Literals.MESSAGE);
		EasyMock.expect(target.eGet(WSDLPackage.Literals.MESSAGE.getEStructuralFeature(WSDLPackage.MESSAGE__QNAME))).andStubReturn(new QName("msgLocalPart"));
		EasyMock.expect(target.eIsSet(WSDLPackage.Literals.MESSAGE.getEStructuralFeature(WSDLPackage.MESSAGE__QNAME))).andStubReturn(true);		
		
		EasyMock.expect(target.getElement()).andStubReturn(domElement);
		EasyMock.expect(ctx.getTarget()).andStubReturn(target);
		EasyMock.expect(ctx.createSuccessStatus()).andStubReturn(Status.OK_STATUS);
		EasyMock.expect(constraint.getDescriptor()).andStubReturn(descriptor);
		EasyMock.expect(descriptor.getSeverity()).andStubReturn(ConstraintSeverity.ERROR);
		EasyMock.expect(descriptor.getPluginId()).andStubReturn("myPlugin");

		EasyMock.expect(ctx.createFailureStatus()).andStubAnswer(new IAnswer<IStatus>() {
			public IStatus answer() throws Throwable {
				return new ConstraintStatus(constraint, target);
			}
		});
		
		EasyMockUtils.replay();
		
		ConstraintRegistry.getInstance().register(descriptor);
		try {
			IStatus result = new NcName().validate(ctx);
			Assert.assertEquals(Status.OK, result.getSeverity());
		} finally {
			ConstraintRegistry.getInstance().unregister(descriptor);
		}
	}
	
	@Test
	public void testValidationError() throws Exception{
		IValidationContext ctx = EasyMockUtils.createNiceMock(IValidationContext.class);
		final WSDLElement target = EasyMockUtils.createNiceMock(WSDLElement.class);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		final IModelConstraint constraint = EasyMockUtils.createNiceMock(IModelConstraint.class);
		
		IConstraintDescriptor descriptor = EasyMockUtils.createNiceMock(IConstraintDescriptor.class);
		
                EasyMock.expect(domElement.getLocalName()).andStubReturn("message");
                EasyMock.expect(target.eClass()).andStubReturn(WSDLPackage.Literals.MESSAGE);
                EasyMock.expect(target.eGet(WSDLPackage.Literals.MESSAGE.getEStructuralFeature(WSDLPackage.MESSAGE__QNAME))).andStubReturn(new QName("msgLocalPartInvalid?"));
                EasyMock.expect(target.eIsSet(WSDLPackage.Literals.MESSAGE.getEStructuralFeature(WSDLPackage.MESSAGE__QNAME))).andStubReturn(true);             

                
		EasyMock.expect(target.getElement()).andStubReturn(domElement);
		EasyMock.expect(ctx.getTarget()).andStubReturn(target);
		EasyMock.expect(ctx.createSuccessStatus()).andStubReturn(Status.OK_STATUS);
		EasyMock.expect(constraint.getDescriptor()).andStubReturn(descriptor);
		EasyMock.expect(descriptor.getSeverity()).andStubReturn(ConstraintSeverity.ERROR);
		EasyMock.expect(descriptor.getPluginId()).andStubReturn("myPlugin");

		EasyMock.expect(ctx.createFailureStatus()).andStubAnswer(new IAnswer<IStatus>() {
			public IStatus answer() throws Throwable {
				return new ConstraintStatus(constraint, target);
			}
		});
		
		EasyMockUtils.replay();
		
		ConstraintRegistry.getInstance().register(descriptor);
		
		try {
			IStatus result = new NcName().validate(ctx);
			Assert.assertEquals(Status.ERROR, result.getSeverity());
		} finally {
			ConstraintRegistry.getInstance().unregister(descriptor);
		}
	}
	
	private void assertRequiredFeatures(EClass eClass, EStructuralFeature[] requiredFeatures, EStructuralFeature[] expectedFeatures) {
		Assert.assertEquals(requiredFeatures.length, expectedFeatures.length);
		
		for (EStructuralFeature requiredFeature : expectedFeatures) {
			boolean found = false;
			for (EStructuralFeature feature: requiredFeatures) {
				if (feature.equals(requiredFeature)) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Feature " + requiredFeature.getName() + "was expected as required but not found for " + eClass.getName(), found);
		}
	}
	
	private static void registerRequiredFeatures(Map<EClass, EStructuralFeature[]> map, EClass eClass, int...ids) {
		EStructuralFeature[] features = new EStructuralFeature[ids.length];
		
		for (int ndx = 0; ndx < ids.length; ndx++) {
			features[ndx] = eClass.getEStructuralFeature(ids[ndx]);
		}
		map.put(eClass, features);
	}
	
	private EObject createTarget(EClass eClass) {
		WSDLElement target = EasyMockUtils.createNiceMock(WSDLElement.class);
		EasyMock.expect(target.eClass()).andStubReturn(eClass);
		return target;
	}

	
}
