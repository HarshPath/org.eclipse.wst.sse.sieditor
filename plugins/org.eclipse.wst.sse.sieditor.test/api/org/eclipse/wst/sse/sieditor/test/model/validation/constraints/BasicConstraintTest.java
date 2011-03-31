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

import java.util.Collection;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintSeverity;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.ConstraintExistsException;
import org.eclipse.emf.validation.service.ConstraintRegistry;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.wst.sse.sieditor.test.SIEditorTestsPlugin;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.internal.util.WSDLResourceFactoryImpl;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.junit.Assert;
import org.junit.Before;


public class BasicConstraintTest {
	
	@Before
	public void tearDown() {
		for (IConstraintDescriptor descriptor : ConstraintRegistry.getInstance().getAllDescriptors()) {
			ConstraintRegistry.getInstance().unregister(descriptor);
		}
		EasyMockUtils.clean();
	}

	protected IValidationContext createValidationContext(boolean batch, final EObject target) throws ConstraintExistsException {
		IValidationContext ctx = EasyMockUtils.createNiceMock(IValidationContext.class);
		
		final IModelConstraint constraint = EasyMockUtils.createNiceMock(IModelConstraint.class);
		
		IConstraintDescriptor descriptor = EasyMockUtils.createNiceMock(IConstraintDescriptor.class);
		
		EasyMock.expect(ctx.getTarget()).andStubReturn(target);
		EasyMock.expect(ctx.getEventType()).andStubReturn(batch ? EMFEventType.NULL : EMFEventType.SET);
		EasyMock.expect(ctx.createSuccessStatus()).andStubAnswer(new StatusAnswer(constraint, target));
		EasyMock.expect(ctx.createFailureStatus()).andStubAnswer(new StatusAnswer(constraint, target));
		EasyMock.expect(constraint.getDescriptor()).andStubReturn(descriptor);
		EasyMock.expect(descriptor.getSeverity()).andStubReturn(ConstraintSeverity.ERROR);
		EasyMock.expect(descriptor.getPluginId()).andStubReturn("myPlugin");
		
		EasyMockUtils.replay();
		
		ConstraintRegistry.getInstance().register(descriptor);
		
		return ctx;
	}
	
	@SuppressWarnings("restriction")
	protected static Definition loadDefinition(String pathName) {
        URI emfUri = URI.createPlatformPluginURI(SIEditorTestsPlugin.PLUGIN_ID + pathName, true);
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("wsdl", new WSDLResourceFactoryImpl());
        WSDLResourceImpl wsdlr = (WSDLResourceImpl) rs.getResource(emfUri, true);
        Definition definition = wsdlr.getDefinition();
        
        Assert.assertNotNull(definition);
        
        return definition;
	}
	
	public static class StatusAnswer implements IAnswer<IStatus> {
		
		private final IModelConstraint constraint;
		private final EObject target;
		public StatusAnswer(IModelConstraint constraint, EObject target) {
			this.constraint = constraint;
			this.target = target;
		}
		public IStatus answer() throws Throwable {
			return new ConstraintStatus(constraint, target);
		}
		
	}
	
	public boolean containsAny(Collection<?> collection, Object...objects){
	    for(Object o: objects){
	        if(collection.contains(o)){
	            return true;
	        }
	    }
	    
	    return false;
	}
}
