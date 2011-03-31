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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidationServiceTest extends SIEditorBaseTest {

    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        modelRoot = (IWsdlModelRoot) getModelRoot("validation/validaitionServiceTest.wsdl", "validaitionServiceTest.wsdl",
                ServiceInterfaceEditor.EDITOR_ID);

        modelDescription = modelRoot.getDescription();
        validationService = editor.getValidationService();
    }

    @Test
    public void testValidationService_OkSchema() throws Throwable {
        final ISchema modelsSchema = modelDescription.getSchema("http://www.example.org/")[0];
        final XSDSchema schema = modelsSchema.getComponent();

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        /*
                         * this dummy command needs to be executed in order to
                         * start the validation.
                         * 
                         * our validation starts after a command transaction has
                         * ended.
                         */
                    }
                });
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        assertNotNull(validationStatusProvider.getStatus(modelsSchema));
    }

    @Test
    public void testValidationService_ErrorSchema() throws Exception {
        final ISchema modelsSchema = modelDescription.getSchema("http://namespace1")[0];
        final XSDSchema schema = modelsSchema.getComponent();
        // validationService.validate(schema);
        final IType type = modelsSchema.getType(false, "StructureType25543s4433");

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        /*
                         * this dummy command needs to be executed in order to
                         * start the validation.
                         * 
                         * our validation starts after a command transaction has
                         * ended.
                         */
                    }
                });
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = validationStatusProvider.getStatus(type);
        assertEquals(1, status.size());
        assertEquals(IStatus.ERROR, status.get(0).getSeverity());
    }

    @Test
    public void liveValidateDisconnectedEObject() {
        final List<Notification> notifications = new LinkedList<Notification>();
        final Notification notificationMock = EasyMock.createMock(Notification.class);
        final EObject notifier = EasyMock.createMock(EObject.class);

        EasyMock.expect(notificationMock.getNotifier()).andReturn(notifier);
        EasyMock.expect(notifier.eResource()).andReturn(null);

        EasyMock.replay(notificationMock, notifier);

        notifications.add(notificationMock);

        final boolean called[] = { false };
        final boolean validated[] = { true };
        new ValidationService(null, null) {
            @Override
            public void update(final ResourceSet resourceSet, final IModelRoot modelRoot) {
            }

            @Override
            protected boolean validateDescription(final EObject eObject) {
                called[0] = true;
                validated[0] = super.validateDescription(eObject);
                return validated[0];
            }

            @Override
            public java.util.Set<org.eclipse.wst.sse.sieditor.model.api.IModelObject> validate(final Object source) {
                return null;
            };
        }.liveValidate(notifications);

        assertTrue("validateDescription was not called", called[0]);
        assertFalse("description validated", validated[0]);
    }

    @Test
    public void liveValidateValidEObject() {
        final List<Notification> notifications = new LinkedList<Notification>();
        final Notification notificationMock1 = EasyMock.createMock(Notification.class);
        final Notification notificationMock2 = EasyMock.createMock(Notification.class);
        final EObject notifier1 = EasyMock.createMock(EObject.class);
        final EObject notifier2 = EasyMock.createMock(EObject.class);

        final Resource resourceMock = EasyMock.createNiceMock(Resource.class);

        EasyMock.expect(notificationMock1.getNotifier()).andReturn(notifier1).anyTimes();
        EasyMock.expect(notifier1.eResource()).andReturn(resourceMock).anyTimes();
        EasyMock.expect(notificationMock2.getNotifier()).andReturn(notifier2).anyTimes();
        EasyMock.expect(notifier2.eResource()).andReturn(resourceMock).anyTimes();

        EasyMock.replay(notificationMock1, notificationMock2, notifier1, notifier2);

        notifications.add(notificationMock1);

        final boolean called[] = { false };
        final boolean validated[] = { true };
        final boolean recalled[] = { false };

        final ValidationService validationService = new ValidationService(null, null) {
            @Override
            public void update(final ResourceSet resourceSet, final IModelRoot modelRoot) {
            }

            @Override
            protected boolean validateDescription(final EObject eObject) {
                if (called[0]) {
                    recalled[0] = true;
                    called[0] = false;
                } else {
                    called[0] = true;
                }
                validated[0] = super.validateDescription(eObject);
                return validated[0];
            }

            @Override
            public java.util.Set<org.eclipse.wst.sse.sieditor.model.api.IModelObject> validate(final Object source) {
                assertNull(source);
                return null;
            }

            @Override
            protected org.eclipse.wst.wsdl.Definition getDefinitionFromResource(final Resource resource) {
                assertSame(resourceMock, resource);
                return null;
            };

            @Override
            protected boolean isWSDLResource(final org.eclipse.emf.ecore.resource.Resource currentResource) {
                return resourceMock == currentResource;
            }

        };
        validationService.liveValidate(notifications);

        assertTrue("validateDescription was not called", called[0]);
        assertFalse("validateDescription was called more than once", recalled[0]);
        assertTrue("description not validated", validated[0]);

        called[0] = false;
        recalled[0] = false;
        // we add second time the notification mock
        notifications.add(notificationMock2);
        validationService.liveValidate(notifications);
        assertTrue("validateDescription was called a the first time", called[0]);
        assertFalse("validateDescription was called more than once", recalled[0]);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        disposeModel();
    }

}
