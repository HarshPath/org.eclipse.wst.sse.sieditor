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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class ValidationResultLocusesTest extends SIEditorBaseTest {

    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = (IWsdlModelRoot) getModelRoot("validation/Locuses.wsdl", "Locuses.wsdl", ServiceInterfaceEditor.EDITOR_ID);

        modelDescription = modelRoot.getDescription();
        validationService = editor.getValidationService();
    }

    @Test
    public void testErrorsLocus() throws Throwable {

        // locations of the errors of "Element1"
        final ArrayList<EObject> element1Locus = new ArrayList<EObject>();
        element1Locus.add(XSDPackage.Literals.XSD_MIN_FACET__EXCLUSIVE);
        element1Locus.add(XSDPackage.Literals.XSD_MIN_FACET__INCLUSIVE);
        element1Locus.add(XSDPackage.Literals.XSD_MAX_FACET__EXCLUSIVE);
        element1Locus.add(XSDPackage.Literals.XSD_MAX_FACET__INCLUSIVE);
        element1Locus.add(XSDPackage.Literals.XSD_TOTAL_DIGITS_FACET__VALUE);
        element1Locus.add(XSDPackage.Literals.XSD_WHITE_SPACE_FACET__VALUE);
        element1Locus.add(XSDPackage.Literals.XSD_ENUMERATION_FACET__VALUE);

        // locations of the errors of "SimpleType"
        final ArrayList<EObject> simpleTypeLocuses = new ArrayList<EObject>();
        simpleTypeLocuses.add(XSDPackage.Literals.XSD_TYPE_DEFINITION__BASE_TYPE);

        // locations of the errors of "StructureType1"
        final ArrayList<EObject> structureLocus = new ArrayList<EObject>();
        structureLocus.add(XSDPackage.Literals.XSD_TYPE_DEFINITION__ANNOTATION);
        structureLocus.add(XSDPackage.Literals.XSD_LENGTH_FACET__VALUE);
        structureLocus.add(XSDPackage.Literals.XSD_MIN_LENGTH_FACET__VALUE);
        structureLocus.add(XSDPackage.Literals.XSD_MAX_LENGTH_FACET__VALUE);

        final ISchema modelsSchema = modelDescription.getSchema("http://www.example.org/NewWSDLFile/")[0];
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

        // test errors locations for element "element1".
        // Locus should include: min inclusive, min exclusive, max inclusive,
        // max exclusive, total digits, enumeration facets;
        final IType element = modelsSchema.getType(true, "Element1");
        final List<IValidationStatus> statuses = validationStatusProvider.getStatus(element);
        assertFalse(statuses.isEmpty());
        for (final IValidationStatus status : statuses) {
            element1Locus.removeAll(status.getSourceStatus().getResultLocus());
        }
        assertTrue(element1Locus.isEmpty());

        // test errors locations for the simple type
        // "SimpleType". The
        // locus should include the type's base type
        final IType simpleType = modelsSchema.getType(false, "SimpleType");
        final List<IValidationStatus> typeStatus = validationStatusProvider.getStatus(simpleType);
        assertFalse(typeStatus.isEmpty());
        for (final IValidationStatus status : typeStatus) {
            simpleTypeLocuses.removeAll(status.getSourceStatus().getResultLocus());
        }
        assertTrue(simpleTypeLocuses.isEmpty());

        // test errors locations for the complex type
        // "StructureType1";
        // The locus should include: the base type of the
        // attribute
        // "attribute1"; length, min length and max
        // length of the element "Element1"
        final IStructureType struct = (IStructureType) modelsSchema.getType(false, "StructureType1");
        final IElement elmt = struct.getElements("Element1").iterator().next();
        for (final IValidationStatus status : validationStatusProvider.getStatus(elmt)) {
            structureLocus.removeAll(status.getSourceStatus().getResultLocus());
        }
        final IElement attr = struct.getElements("Attribute1").iterator().next();
        for (final IValidationStatus status : validationStatusProvider.getStatus(attr)) {
            structureLocus.removeAll(status.getSourceStatus().getResultLocus());
        }
        assertTrue(structureLocus.isEmpty());

    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }

}