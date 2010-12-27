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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability;

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.DOCUMENT_STYLE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.ELEMENT_ATTRIBUTE_HAS_INVALID_VALUE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INAPPROPRIATE_SOAP_BINDING;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INAPPROPRIATE_SOAP_BINDING_STYLE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.LITERAL;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.PARTS_ATTRIBUTE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.RPC_STYLE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_OPERATION_HAS_MORE_THAN_ONE_INPUT_PARAMETER;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_OPERATION_HAS_MORE_THAN_ONE_OUTPUT_PARAMETER;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_INPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_INPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIUtils.getSOAPBinding;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.BindingOutput;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.ExtensibleElement;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class WSIOperationCompliant extends AbstractConstraint {

    private Operation operation;
    private List<Part> partsInInputMessage;
    private List<Part> partsInOutputMessage;
    private boolean hasPartsAttributeInBindingInputParameters;
    private boolean hasPartsAttributeInBindingOutputParameters;

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(final IValidationContext validationContext) {
        this.operation = (Operation) validationContext.getTarget();

        final Definition definition = this.operation.getEnclosingDefinition();
        for (final PortType port : (List<PortType>) definition.getEPortTypes()) {
            // check whether current operation is in this service(port)
            if (!port.getEOperations().contains(this.operation))
                continue;

            final List<Binding> soapBindingsForTheCurrentPort = WSIManager.getSoapBindings(port);
            if (soapBindingsForTheCurrentPort == null) {
                break;
            }
            for (final Binding currentBinding : soapBindingsForTheCurrentPort) {
                // check whether currentBinding contains target operation
                boolean isBindingContainOperation = false;
                for (final BindingOperation currentBindingOperation : (List<BindingOperation>) currentBinding
                        .getEBindingOperations()) {
                    if (currentBindingOperation == null)
                        continue;

                    final Operation currentEOperation = currentBindingOperation.getEOperation();
                    if (currentEOperation != null && currentEOperation.equals(this.operation)) {
                        isBindingContainOperation = true;
                        break;
                    }
                }
                if (!isBindingContainOperation)
                    continue;

                final SOAPBinding currentSoapBinding = getSOAPBinding(currentBinding);
                if (currentSoapBinding == null) {
                    return ConstraintStatus.createStatus(validationContext, port, null, INAPPROPRIATE_SOAP_BINDING,
                            INAPPROPRIATE_SOAP_BINDING);
                }

                final String theBindingStyle = currentSoapBinding.getStyle();
                return getStatusAccordingToSoapBindingStyle(validationContext, port, currentBinding, theBindingStyle);
            }
        }

        return statusThatOperationDoesNotHaveSimilarBindingOperation(validationContext);

    }

    @Override
    protected boolean shouldExecute(final IValidationContext ctx) {
        // if bindings are not specified or bindings are not SOAP bindings, then
        // validation over wsdl:operations shouldn't be executed
        return WSIManager.shouldExecuteContraintsOnOperation();
    }

    /**
     * R2718 A wsdl:binding in a DESCRIPTION MUST have the same set of
     * wsdl:operations as the wsdl:portType to which it refers.
     * 
     * @param validationContext
     *            is the IValidationContext from the doValidate(..) method
     * 
     * @return status for "this.operation" according to WS-I specification
     */
    private IStatus statusThatOperationDoesNotHaveSimilarBindingOperation(final IValidationContext validationContext) {
        return ConstraintStatus.createStatus(validationContext, this.operation, null,
                OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION, OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION);
    }

    /**
     * 
     * "R2705 A wsdl:binding in a DESCRIPTION MUST either be a rpc-literal
     * binding or a document-literal binding."
     * 
     * @param validationContext
     *            is the IValidationContext from the doValidate(..) method
     * @param port
     *            is the PortType which contain the current "this.operation"
     * @param currentBinding
     *            is the Binding which contain the current "this.operation"
     * @param theBindingStyle
     *            is the specified binding style
     * @return status for "this.operation" according to WS-I specification
     */
    private IStatus getStatusAccordingToSoapBindingStyle(final IValidationContext validationContext, final PortType port,
            final Binding currentBinding, final String theBindingStyle) {

        initializeInputAndOutputParts();

        if (DOCUMENT_STYLE.equalsIgnoreCase(theBindingStyle)) {
            return checkInCaseOfDocumentStyleBinding(validationContext, port, currentBinding);
        }

        if (RPC_STYLE.equalsIgnoreCase(theBindingStyle)) {
            return checkInCaseOfRpcStyleBinding(validationContext, port, currentBinding);
        }

        if (WSIManager.isShouldBeShownAMessageForInappropriateSOAPBindingStyle(port)) {
            WSIManager.setShouldBeShownAMessageForInappropriateSOAPBindingStyle(port, false);
            return ConstraintStatus.createStatus(validationContext, port, null, INAPPROPRIATE_SOAP_BINDING_STYLE,
                    INAPPROPRIATE_SOAP_BINDING_STYLE);
        } else {
            return ConstraintStatus.createSuccessStatus(validationContext, port, null);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeInputAndOutputParts() {
        this.hasPartsAttributeInBindingInputParameters = false;
        this.hasPartsAttributeInBindingOutputParameters = false;

        final Input input = this.operation.getEInput();
        if (input != null) {
            final Message message = input.getEMessage();
            if (message != null) {
                this.partsInInputMessage = message.getEParts();
            }
        }
        final Output output = this.operation.getEOutput();
        if (output != null) {
            final Message message = output.getEMessage();
            if (message != null) {
                this.partsInOutputMessage = message.getEParts();
            }
        }
    }

    /**
     * Validate different fine grained rules, when Document-literal SOAP binding
     * style is specified
     * 
     * @param validationContext
     *            is the IValidationContext from the doValidate(..) method
     * @param portType
     *            is the PortType which contain the current "this.operation"
     * @param currentBinding
     *            is the Binding which contain the current "this.operation"
     * @return status in case of document-style binding
     */
    @SuppressWarnings("unchecked")
    private IStatus checkInCaseOfDocumentStyleBinding(final IValidationContext validationContext, final PortType portType,
            final Binding currentBinding) {
        for (final BindingOperation bindingOperationForWSIChecking : (List<BindingOperation>) currentBinding
                .getEBindingOperations()) {
            if (!this.operation.equals(bindingOperationForWSIChecking.getEOperation()))
                continue;

            if (!haveConsistentBetweenInputOutputAndBindingInputOutput(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION,
                        INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION);
            }

            if (!hasLiteralValueForAllUseAttributesInBindingOperation(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL,
                        THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL);
            }

            if (isNamespaceAttributeExistInSoapElementsForSpecifiedBindingOperation(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED,
                        NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED);
            }

            final Collection<IStatus> statuses = new HashSet<IStatus>();

            validateFaults(validationContext, statuses, bindingOperationForWSIChecking);

            return getStatusForMoreThenOnePartInMessage(validationContext, bindingOperationForWSIChecking, statuses);
        }
        // Possible when current this.operation hasn't binding operation
        return ConstraintStatus.createStatus(validationContext, portType, null, OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION,
                OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION);
    }

    /**
     * R2201 A document-literal binding in a DESCRIPTION MUST, in each of its
     * soapbind:body element(s), have at most one part listed in the parts
     * attribute, if the parts attribute is specified.
     * 
     * R2209 A wsdl:binding in a DESCRIPTION SHOULD bind every wsdl:part of a
     * wsdl:message in the wsdl:portType to which it refers with a binding
     * extension element.
     * 
     * @param bindingOperationForWSIChecking
     *            is not null binding operation for check according to WS-I
     * @return whether has at most one part listed in parts attribute for each
     *         soapbind:body
     */
    @SuppressWarnings("unchecked")
    private boolean isCorrectIfPartsAttributeIsSpecifiedInSomeSoapBody(final BindingOperation bindingOperationForWSIChecking) {
        final BindingInput input = (BindingInput) bindingOperationForWSIChecking.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperationForWSIChecking.getBindingOutput();
        final List<BindingFault> faults = bindingOperationForWSIChecking.getEBindingFaults();

        final boolean resultFromFaultsCheking = hasAtMostOnePartListedInPartsAttributeInFaults(faults);

        return (ensureAppropriatePartElements(input) & ensureAppropriatePartElements(output)) && resultFromFaultsCheking;
    }

    @SuppressWarnings("unchecked")
    private boolean ensureAppropriatePartElements(final ExtensibleElement extensibleElement) {
        if (extensibleElement == null) {
            return true;
        }
        final List<ExtensibilityElement> extensibilityElements = ((extensibleElement).getExtensibilityElements());

        if (extensibleElement instanceof BindingInput) {
            return checkThePartsWhichAreRefferedByBindingOperation(extensibilityElements, this.partsInInputMessage, true);
        }

        if (extensibleElement instanceof BindingOutput) {
            return checkThePartsWhichAreRefferedByBindingOperation(extensibilityElements, this.partsInOutputMessage, false);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean checkThePartsWhichAreRefferedByBindingOperation(final List<ExtensibilityElement> extensibilityElements,
            final List<Part> partsInTheMessage, final boolean areElementsFromBindingInput) {

        if (partsInTheMessage == null || partsInTheMessage.size() == 0) {
            return true;
        }

        boolean isSpecifiedPartsAttribute = false;
        int numberOfPartsRefferedBySoapHeader = 0;
        boolean isCorrectTheSizeOfListedParts = true;// change the name

        for (final ExtensibilityElement element : extensibilityElements) {
            if (element instanceof SOAPBody) {
                final SOAPBody soapPart = (SOAPBody) element;
                if (!soapPart.getElement().hasAttribute(PARTS_ATTRIBUTE)) {
                    continue;
                }
                final List<Part> listedParts = soapPart.getParts();
                if (listedParts == null || listedParts.size() == 0) {
                    continue;
                }
                isSpecifiedPartsAttribute = true;
                if (areElementsFromBindingInput) {
                    this.hasPartsAttributeInBindingInputParameters = true;
                } else {
                    this.hasPartsAttributeInBindingOutputParameters = true;
                }
                if (listedParts.size() > 1) {
                    isCorrectTheSizeOfListedParts = false;
                }
            } else if (element instanceof SOAPHeader) {
                final SOAPHeader soapPart = (SOAPHeader) element;
                final Part partInHeader = soapPart.getEPart();
                if (partInHeader == null) {
                    continue;
                }
                if (partsInTheMessage.contains(partInHeader)) {
                    ++numberOfPartsRefferedBySoapHeader;
                }
            }
        }

        if (isSpecifiedPartsAttribute && (numberOfPartsRefferedBySoapHeader != (partsInTheMessage.size() - 1))) {
            return false;
        }
        return isCorrectTheSizeOfListedParts;
    }

    @SuppressWarnings("unchecked")
    private boolean hasAtMostOnePartListedInSoapBody(final List extensibilityElements) {
        for (final ExtensibilityElement element : (List<ExtensibilityElement>) extensibilityElements) {
            if (!(element instanceof SOAPBody)) {
                continue;
            }
            final SOAPBody soapPart = (SOAPBody) element;
            final List<Part> listedParts = soapPart.getEParts();
            if (listedParts != null && listedParts.size() > 1) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean hasAtMostOnePartListedInPartsAttributeInFaults(final List<BindingFault> faults) {
        boolean resultFromFaultsCheking = true;
        if (faults == null || faults.isEmpty()) {
            return resultFromFaultsCheking;
        }
        for (final BindingFault bindingFault : faults) {
            if (bindingFault == null) {
                continue;
            }
            resultFromFaultsCheking &= hasAtMostOnePartListedInSoapBody(bindingFault.getExtensibilityElements());
        }
        return resultFromFaultsCheking;
    }

    /**
     * R2716 A document-literal binding in a DESCRIPTION MUST NOT have the
     * namespace attribute specified on contained soapbind:body,
     * soapbind:header, soapbind:headerfault and soapbind:fault elements.
     * 
     * @param bindingOperationForWSIChecking
     *            is not null binding operation for check according to WS-I
     * @return is namespace attribute exist in soap element for a given binding
     *         operation
     */
    @SuppressWarnings("unchecked")
    private boolean isNamespaceAttributeExistInSoapElementsForSpecifiedBindingOperation(final BindingOperation bindingOperation) {
        final BindingInput input = (BindingInput) bindingOperation.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperation.getBindingOutput();
        final List<BindingFault> faults = bindingOperation.getEBindingFaults();
        return haveNamespaceAttributesForExtensibleElement(input) || haveNamespaceAttributesForExtensibleElement(output)
                || isNamespaceAttributeExistInSoapFaults(faults);
    }

    @SuppressWarnings("unchecked")
    private <T extends ExtensibleElement> boolean haveNamespaceAttributesForExtensibleElement(final T bindingExtensibleElement) {
        boolean hasNamespaceAttribute = false;
        if (bindingExtensibleElement == null)
            return false;

        hasNamespaceAttribute = haveNamespaceAttributesInExtensibilityElements((bindingExtensibleElement)
                .getExtensibilityElements());

        return hasNamespaceAttribute;
    }

    @SuppressWarnings("unchecked")
    private boolean isNamespaceAttributeExistInSoapFaults(final List<BindingFault> faults) {
        if (faults == null || faults.isEmpty())
            return false;
        boolean isNamespaceAttributeExist = false;
        for (final BindingFault bindingFault : faults) {
            final List<ExtensibilityElement> extensibilityElements = bindingFault.getExtensibilityElements();
            if (extensibilityElements == null)
                return true;
            isNamespaceAttributeExist = haveNamespaceAttributesInExtensibilityElements(extensibilityElements);
            if (isNamespaceAttributeExist == true)
                break;
        }
        return isNamespaceAttributeExist;
    }

    private boolean haveNamespaceAttributesInExtensibilityElements(final List<ExtensibilityElement> extensibleElements) {
        if (extensibleElements == null || extensibleElements.isEmpty())
            return false;
        for (final ExtensibilityElement element : extensibleElements) {
            if (element instanceof SOAPBody) {
                final SOAPBody soapPart = (SOAPBody) element;
                if (soapPart.getNamespaceURI() == null)
                    return false;
            } else if (element instanceof SOAPFault) {
                final SOAPFault soapPart = (SOAPFault) element;
                if (soapPart.getNamespaceURI() == null)
                    return false;
            } else if (element instanceof SOAPHeader) {
                final SOAPHeader soapPart = (SOAPHeader) element;
                if (soapPart.getNamespaceURI() == null)
                    return false;
            } else if (element instanceof SOAPHeaderFault) {
                final SOAPHeaderFault soapPart = (SOAPHeaderFault) element;
                if (soapPart.getNamespaceURI() == null)
                    return false;
            }
        }
        return true;
    }

    /**
     * A document-literal binding in a DESCRIPTION MUST refer only to wsdl:part
     * element(s) that have been defined using the element attribute.
     * 
     * R2205 A wsdl:binding in a DESCRIPTION MUST refer, in each of its
     * soapbind:header, soapbind:headerfault and soapbind:fault elements, only
     * to wsdl:part element(s) that have been defined using the element
     * attribute.
     * 
     * R2210 If a document-literal binding in a DESCRIPTION does not specify the
     * parts attribute on a soapbind:body element, the corresponding abstract
     * wsdl:message MUST define zero or one wsdl:parts.
     * 
     * @param validationContext
     * @return status according to the rules mentioned above from WS-I
     *         validation
     */
    private IStatus getStatusForMoreThenOnePartInMessage(final IValidationContext validationContext,
            final BindingOperation bindingOperationForWSIChecking, final Collection<IStatus> statuses) {

        if (!isCorrectIfPartsAttributeIsSpecifiedInSomeSoapBody(bindingOperationForWSIChecking)) {
            statuses.add(ConstraintStatus.createStatus(validationContext, this.operation, null,
                    HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE, HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE));
        }

        final Input input = (Input) this.operation.getInput();
        if (input != null) {
            checkThePartsForWSICompliantWhenDocumentBindingStyleIsSpecified(validationContext, statuses,
                    !this.hasPartsAttributeInBindingInputParameters, this.partsInInputMessage,
                    THE_OPERATION_HAS_MORE_THAN_ONE_INPUT_PARAMETER,
                    THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_INPUT_MESSAGE);
        }
        final Output output = (Output) this.operation.getOutput();
        if (output != null) {
            checkThePartsForWSICompliantWhenDocumentBindingStyleIsSpecified(validationContext, statuses,
                    !this.hasPartsAttributeInBindingOutputParameters, this.partsInOutputMessage,
                    THE_OPERATION_HAS_MORE_THAN_ONE_OUTPUT_PARAMETER,
                    THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE);
        }

        return createStatusFromAGivenListOfStatues(validationContext, statuses);
    }

    private void checkThePartsForWSICompliantWhenDocumentBindingStyleIsSpecified(final IValidationContext validationContext,
            final Collection<IStatus> statuses, final Boolean shouldBeCheckedForMoreThanOneInputParameter,
            final List<Part> currentParts, final String warningForInappropiateCountOfParts,
            final String warningForMissingElementAttribute) {

        if (shouldBeCheckedForMoreThanOneInputParameter && currentParts != null && currentParts.size() > 1) {
            statuses.add(ConstraintStatus.createStatus(validationContext, this.operation, null,
                    warningForInappropiateCountOfParts, warningForInappropiateCountOfParts));
        }
        checkTheElementAttributes(validationContext, currentParts, warningForMissingElementAttribute, statuses);
    }

    private void checkTheElementAttributes(final IValidationContext validationContext, final List<Part> currentParts,
            final String warningMessage, final Collection<IStatus> statuses) {
        if (currentParts == null) {
            return;
        }
        for (final Part part : currentParts) {
            if (!ElementAttributeUtils.hasAttributeValue(part.getElement(), WSDLConstants.ELEMENT_ATTRIBUTE)) {
                statuses.add(ConstraintStatus.createStatus(validationContext, part, null, warningMessage, warningMessage));

            } else if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(part.getElement().getAttributeNS(
                    XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, WSDLConstants.ELEMENT_ATTRIBUTE))) {
                statuses.add(ConstraintStatus.createStatus(validationContext, part, null, ELEMENT_ATTRIBUTE_HAS_INVALID_VALUE,
                        ELEMENT_ATTRIBUTE_HAS_INVALID_VALUE));
            }
        }
    }

    /**
     * Validate different fine grained rules, when RPC-literal SOAP binding
     * style is specified
     * 
     * @param validationContext
     *            is the IValidationContext from the doValidate(..) method
     * @param portType
     *            is the PortType which contain the current "this.operation"
     * @param currentBinding
     *            is the Binding which contain the current "this.operation"
     * @return status in case of document-style binding
     */
    @SuppressWarnings("unchecked")
    private IStatus checkInCaseOfRpcStyleBinding(final IValidationContext validationContext, final PortType portType,
            final Binding currentBinding) {
        for (final BindingOperation bindingOperationForWSIChecking : (List<BindingOperation>) currentBinding
                .getEBindingOperations()) {
            // if current binding operation != from the target operation
            if (!this.operation.equals(bindingOperationForWSIChecking.getEOperation()))
                continue;

            if (!haveConsistentBetweenInputOutputAndBindingInputOutput(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION,
                        INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION);
            }
            if (!hasLiteralValueForAllUseAttributesInBindingOperation(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL,
                        THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL);
            }
            if (!hasNamespaceAttributeInAllSoapBodies(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY, MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY);
            }
            if (!isNamespaceAttributeMissingInNonSoapBodyElement(bindingOperationForWSIChecking)) {
                return ConstraintStatus.createStatus(validationContext, this.operation, null,
                        NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER,
                        NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER);
            }

            final Collection<IStatus> statuses = new HashSet<IStatus>();

            validateFaults(validationContext, statuses, bindingOperationForWSIChecking);

            return hasTypeAttributeInAllParts(validationContext, statuses);

        }
        // Possible when current operation hasn't binding for operation
        return ConstraintStatus.createStatus(validationContext, portType, null, OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION,
                OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION);
    }

    private void validateFaults(final IValidationContext validationContext, final Collection<IStatus> statuses,
            final BindingOperation bindingOperationForWSIChecking) {
        final FaultsState faultState = getFaultsState(bindingOperationForWSIChecking);
        if (faultState != FaultsState.FAULT_STATE_IS_OK) {
            statuses.add(ConstraintStatus.createStatus(validationContext, operation, null, faultState.getMessage(), faultState
                    .getMessage()));
        }

    }

    /**
     * R2726 An rpc-literal binding in a DESCRIPTION MUST NOT have the namespace
     * attribute specified on contained soapbind:header, soapbind:headerfault
     * and soapbind:fault elements.
     * 
     * @param bindingOperation
     * @return whether namespace attribute missing in non soap body element
     */
    @SuppressWarnings("unchecked")
    private boolean isNamespaceAttributeMissingInNonSoapBodyElement(final BindingOperation bindingOperation) {
        final BindingInput input = (BindingInput) bindingOperation.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperation.getBindingOutput();
        final List<BindingFault> faults = bindingOperation.getEBindingFaults();

        boolean isNamespaceAttributeMissingInFaultsExtensibleElementWhichIsNotSoapBody = true;
        if (faults != null && !faults.isEmpty()) {
            for (final BindingFault bindingFault : faults) {
                if (bindingFault == null) {
                    continue;
                }
                isNamespaceAttributeMissingInFaultsExtensibleElementWhichIsNotSoapBody &= isNamespaceAttributeMissingInNonSoapBodyExtensibleElements(bindingFault
                        .getExtensibilityElements());
            }
        }
        return isNamespaceAttributeMissingInFaultsExtensibleElementWhichIsNotSoapBody
                && isNamespaceAttributesMissingInNonSoapBodyForExtensibleElement(input)
                && isNamespaceAttributesMissingInNonSoapBodyForExtensibleElement(output);

    }

    @SuppressWarnings("unchecked")
    private <T extends ExtensibleElement> boolean isNamespaceAttributesMissingInNonSoapBodyForExtensibleElement(
            final T bindingExtensibleElement) {
        boolean isNamespaceAttributeMissing = true;
        if (bindingExtensibleElement == null) {
            return isNamespaceAttributeMissing;
        }

        isNamespaceAttributeMissing = isNamespaceAttributeMissingInNonSoapBodyExtensibleElements((bindingExtensibleElement)
                .getExtensibilityElements());

        return isNamespaceAttributeMissing;
    }

    private boolean isNamespaceAttributeMissingInNonSoapBodyExtensibleElements(final List<ExtensibilityElement> extensibleElements) {
        if (extensibleElements == null || extensibleElements.isEmpty()) {
            return true;
        }
        for (final ExtensibilityElement element : extensibleElements) {
            if (element instanceof SOAPFault) {
                final SOAPFault soapPart = (SOAPFault) element;
                if (soapPart.getNamespaceURI() != null)
                    return false;
            } else if (element instanceof SOAPHeader) {
                final SOAPHeader soapPart = (SOAPHeader) element;
                if (soapPart.getNamespaceURI() != null)
                    return false;
            } else if (element instanceof SOAPHeaderFault) {
                final SOAPHeaderFault soapPart = (SOAPHeaderFault) element;
                if (soapPart.getNamespaceURI() != null)
                    return false;
            }
        }
        return true;
    }

    /**
     * R2717 An rpc-literal binding in a DESCRIPTION MUST have the namespace
     * attribute specified, the value of which MUST be an absolute URI, on
     * contained soapbind:body elements.
     * 
     * @param bindingOperation
     *            is not null binding operation which content must be validated
     * @return whether have namespace attribute for all soap bodies when RPC
     *         binding style is specified
     */
    private boolean hasNamespaceAttributeInAllSoapBodies(final BindingOperation bindingOperation) {
        final BindingInput input = (BindingInput) bindingOperation.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperation.getBindingOutput();
        return isNamespaceAttributesAppearForExtensibleElement(input) && isNamespaceAttributesAppearForExtensibleElement(output);
    }

    @SuppressWarnings("unchecked")
    private <T extends ExtensibleElement> boolean isNamespaceAttributesAppearForExtensibleElement(final T bindingExtensibleElement) {
        boolean isNamespaceAttributeExist = true;
        if (bindingExtensibleElement == null) {
            return isNamespaceAttributeExist;
        }
        isNamespaceAttributeExist = isNamespaceAttributeAppearCorrectlyInSoapBodyWhenRPCStyleIsSpecified((bindingExtensibleElement)
                .getExtensibilityElements());
        return isNamespaceAttributeExist;
    }

    /**
     * An rpc-literal binding in a DESCRIPTION MUST refer,only to wsdl:part
     * element(s) that have been defined using the type attribute.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @return status according to referred messages
     */
    private IStatus hasTypeAttributeInAllParts(final IValidationContext validationContext, final Collection<IStatus> statuses) {
        if (this.operation.getInput() != null) {
            checkForTypeAttribute(validationContext, this.partsInInputMessage, statuses,
                    THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_INPUT_MESSAGE);
        }

        if (this.operation.getOutput() != null) {// in case of asynchronous
            // operations
            checkForTypeAttribute(validationContext, this.partsInOutputMessage, statuses,
                    THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE);
        }

        return createStatusFromAGivenListOfStatues(validationContext, statuses);
    }

    private void checkForTypeAttribute(final IValidationContext validationContext, final List<Part> currentParts,
            final Collection<IStatus> statuses, final String warringMessage) {
        if (currentParts == null) {
            return;
        }
        for (final Part part : currentParts) {
            final Element partElement = part.getElement();
            if (ElementAttributeUtils.hasAttributeValue(partElement, WSDLConstants.TYPE_ATTRIBUTE)) {
                continue;
            }
            statuses.add(ConstraintStatus.createStatus(validationContext, part, null, warringMessage, warringMessage));
        }
    }

    private boolean isNamespaceAttributeAppearCorrectlyInSoapBodyWhenRPCStyleIsSpecified(
            final List<ExtensibilityElement> extensibleElements) {
        for (final ExtensibilityElement element : extensibleElements) {
            if (!(element instanceof SOAPBody)) {
                continue;
            }
            final SOAPBody soapBody = (SOAPBody) element;
            final String namespaceURI = soapBody.getNamespaceURI();
            if (namespaceURI == null) {
                return false;
            }
            try {
                final URI uri = new URI(namespaceURI);
                if (!uri.isAbsolute()) {
                    return false;
                }
            } catch (final URISyntaxException e) {
                // in this case the namespace attribute is not valid
                return false;
            }
        }

        return true;

    }

    /**
     * Incompatibility between the Input/Output in Operation and
     * BindingInput/BindingOutput in BindingOperation. Check missing of
     * WSDL:Input or WSDL:Output in WSDL:binding element.
     * 
     * @param bindingOperationForWSIChecking
     *            the binding operation which must be validated
     * @return whether have consistent between Input/Output in binding operation
     *         and Input/Output in wsdl:operation
     */
    private boolean haveConsistentBetweenInputOutputAndBindingInputOutput(final BindingOperation bindingOperationForWSIChecking) {
        final BindingInput input = (BindingInput) bindingOperationForWSIChecking.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperationForWSIChecking.getBindingOutput();

        final Input operationInput = this.operation.getEInput();
        if ((operationInput != null && input == null) || (operationInput == null && input != null)) {
            return false;
        }

        final Output operationOutput = this.operation.getEOutput();
        if ((operationOutput != null && output == null) || (operationOutput == null && output != null)) {
            return false;
        }
        return true;
    }

    /**
     * 2706 A wsdl:binding in a DESCRIPTION MUST use the value of "literal" for
     * the use attribute in all soapbind:body, soapbind:fault, soapbind:header
     * and soapbind:headerfault elements.
     * 
     * R2707 A wsdl:binding in a DESCRIPTION that contains one or more
     * soapbind:body, soapbind:fault, soapbind:header or soapbind:headerfault
     * elements that do not specify the use attribute MUST be interpreted as
     * though the value "literal" had been specified in each case.
     * 
     * @param bindingOperationForWSIChecking
     *            the binding operation which must be validated
     * @return whether have literal value for all use attributes
     */
    @SuppressWarnings("unchecked")
    private boolean hasLiteralValueForAllUseAttributesInBindingOperation(final BindingOperation bindingOperationForWSIChecking) {
        final BindingInput input = (BindingInput) bindingOperationForWSIChecking.getBindingInput();
        final BindingOutput output = (BindingOutput) bindingOperationForWSIChecking.getBindingOutput();
        final List<BindingFault> faults = bindingOperationForWSIChecking.getEBindingFaults();

        final boolean resultFromFaultsCheking = hasLiteralValueForUseAttributeInSoapFaults(faults);

        return hasLiteralValueInExtensibilityElements(input) && hasLiteralValueInExtensibilityElements(output)
                && resultFromFaultsCheking;
    }

    @SuppressWarnings("unchecked")
    private <T extends ExtensibleElement> boolean hasLiteralValueInExtensibilityElements(final T bindingExtensibleElement) {
        boolean hasLiteralValueForUseAttributeInExtensibilityElements = true;
        if (bindingExtensibleElement == null) {
            return hasLiteralValueForUseAttributeInExtensibilityElements;
        }
        hasLiteralValueForUseAttributeInExtensibilityElements = hasApproriateValueForUseAttributeInExtesibilityElements((bindingExtensibleElement)
                .getExtensibilityElements());
        return hasLiteralValueForUseAttributeInExtensibilityElements;
    }

    @SuppressWarnings("unchecked")
    private boolean hasLiteralValueForUseAttributeInSoapFaults(final List<BindingFault> faults) {
        boolean resultFromFaultsCheking = true;
        if (faults == null || faults.isEmpty()) {
            return resultFromFaultsCheking;
        }
        for (final BindingFault bindingFault : faults) {
            if (bindingFault == null)
                continue;
            resultFromFaultsCheking &= hasApproriateValueForUseAttributeInExtesibilityElements(bindingFault
                    .getExtensibilityElements());
        }
        return resultFromFaultsCheking;
    }

    private boolean hasApproriateValueForUseAttributeInExtesibilityElements(final List<ExtensibilityElement> extensions) {
        for (final ExtensibilityElement element : extensions) {
            if (element instanceof SOAPBody) {
                final SOAPBody soapPart = (SOAPBody) element;
                if ((soapPart.getUse() != null) && !soapPart.getUse().equalsIgnoreCase(LITERAL))
                    return false;
            } else if (element instanceof SOAPFault) {
                final SOAPFault soapPart = (SOAPFault) element;
                if ((soapPart.getUse() != null) && !soapPart.getUse().equalsIgnoreCase(LITERAL))
                    return false;
            } else if (element instanceof SOAPHeader) {
                final SOAPHeader soapPart = (SOAPHeader) element;
                if ((soapPart.getUse() != null) && !soapPart.getUse().equalsIgnoreCase(LITERAL))
                    return false;
            } else if (element instanceof SOAPHeaderFault) {
                final SOAPHeaderFault soapPart = (SOAPHeaderFault) element;
                if ((soapPart.getUse() != null) && !soapPart.getUse().equalsIgnoreCase(LITERAL))
                    return false;
            }
        }
        return true;
    }

    /**
     * R2754 In a DESCRIPTION, the value of the name attribute on a
     * soapbind:fault element MUST match the value of the name attribute on its
     * parent wsdl:fault element.
     * 
     * R2721 A wsdl:binding in a DESCRIPTION MUST have the name attribute
     * specified on all contained soapbind:fault elements
     * 
     * R2740 A wsdl:binding in a DESCRIPTION SHOULD contain a soapbind:fault
     * describing each known fault.
     * 
     * R2722 A wsdl:binding in a DESCRIPTION MAY specify the use attribute on
     * contained soapbind:fault elements.
     * 
     * R2723 If in a wsdl:binding in a DESCRIPTION the use attribute on a
     * contained soapbind:fault element is present, its value MUST be "literal".
     * 
     * R2728 A wsdl:binding in a DESCRIPTION that omits the use attribute on a
     * contained soapbind:fault element MUST be interpreted as though
     * use="literal" had been specified.(from WS-I BP 1.0)
     * 
     * 
     * R2742 An ENVELOPE MAY contain fault with a detail element that is not
     * described by a soapbind:fault element in the corresponding WSDL
     * description.(not for WSDL description)
     * 
     * R2743 An ENVELOPE MAY contain the details of a header processing related
     * fault in a SOAP header block that is not described by a
     * soapbind:headerfault element in the corresponding WSDL description.(not
     * for WSDL description)
     * 
     * @param bindingOperationForWSIChecking
     *            is not null binding operation for check according to WS-I
     * @return FaultState according to WS-I
     */
    @SuppressWarnings("unchecked")
    private FaultsState getFaultsState(final BindingOperation bindingOperationForWSIChecking) {
        final List<BindingFault> bindingFaultsForCurrentOperation = bindingOperationForWSIChecking.getEBindingFaults();
        final List<Fault> wsdlFaultsDescribedForCurrentOperation = this.operation.getEFaults();
        final int expectedSoapFaultsCount = bindingFaultsForCurrentOperation.size();
        int actualSoapFaultsCount = 0;
        for (final BindingFault fault : bindingFaultsForCurrentOperation) {
            if (fault == null)
                continue;
            for (final ExtensibilityElement element : (List<ExtensibilityElement>) fault.getEExtensibilityElements()) {
                if (!(element instanceof SOAPFault)) {
                    continue;
                }
                ++actualSoapFaultsCount;
                final SOAPFault soapFault = (SOAPFault) element;
                if (soapFault.getName() == null)
                    return FaultsState.MISSING_NAME_ATTRIBUTE_IN_SOME_SOAPFAULT;
                if (!soapFault.getName().equals(fault.getName()))
                    return FaultsState.SOAPFAULT_NAME_AND_WSDLBINDING_FAULT_NAME_DONOT_MATCH;
                if (soapFault.getUse() != null && !soapFault.getUse().equals(LITERAL))
                    return FaultsState.THE_SOAPFAULT_HAS_SPECIFIED_USE_ATTTRIBUTE_WITH_NOT_LITERAL_VALUE;
                break;// only one soap:fault is needed
            }

        }
        if ((expectedSoapFaultsCount == actualSoapFaultsCount)
                && bindingFaultsForCurrentOperation.size() >= wsdlFaultsDescribedForCurrentOperation.size()) {
            return FaultsState.FAULT_STATE_IS_OK;
        } else {
            return FaultsState.FOR_EVERY_WSDL_FAULT_MUST_HAVE_CORRESPONDING_SOAP_FAULT_IN_OPERATION_BINDING;
        }
    }

    private IStatus createStatusFromAGivenListOfStatues(final IValidationContext validationContext,
            final Collection<IStatus> statuses) {
        if (statuses.isEmpty()) {
            return ConstraintStatus.createSuccessStatus(validationContext, this.operation, null);
        } else {
            return ConstraintStatus.createMultiStatus(validationContext, statuses);
        }
    }

}
