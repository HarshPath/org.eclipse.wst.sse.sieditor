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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractXSDComponent;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Updates documentation for XSD Components
 * 
 * 
 */
public class SetDocumentationCommand extends AbstractNotificationOperation {
    private static final String DOCU_ELEMENT_TAG = "documentation"; //$NON-NLS-1$

    private final String _text;
    private Element _documentationElement;
    private boolean _isNew;
    private IStatus lastAddAnnotationCommandStatus = Status.CANCEL_STATUS;

    public SetDocumentationCommand(final IModelRoot root, final IModelObject modelObject, final String text) {
        super(root, modelObject, Messages.SetDocumentationCommand_set_documentation_command_label);
        this._text = text;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	lastAddAnnotationCommandStatus = Status.CANCEL_STATUS;
    	XSDAnnotation annotation = getDocumentationAnnotation(modelObject);
    	if (annotation == null) {
    		return lastAddAnnotationCommandStatus;
    	}
    	
        // this command only ensures that the text is inserted as the first
        // userinfo in the documentation and it only considers the first text
        // after the documentation element for inserting the text
        final List<Element> userInfo = annotation.getUserInformation();
        final Element annotationElement = annotation.getElement();
        final Document document = annotationElement.getOwnerDocument();
        if (userInfo.size() == 0) {
            // Have to ensure the documentation element since the EMF doesn't
            // include
            // userinfo and appinfo as part of model and is included in the DOM
            // for documentation
            final Node firstChild = annotationElement.getFirstChild();
            final String prefix = annotationElement.getPrefix();
            _documentationElement = (prefix != null && prefix.length() > 0) ? document.createElement(prefix
                    + ":" + DOCU_ELEMENT_TAG) : //$NON-NLS-1$
                    document.createElement(DOCU_ELEMENT_TAG);

            if (firstChild == null)
                annotationElement.appendChild(_documentationElement);
            else
                annotationElement.insertBefore(_documentationElement, firstChild);
            _isNew = true;
        } else {
            _documentationElement = userInfo.get(0);
        }

        final Node textNode = _documentationElement.getFirstChild();
        if (textNode != null) {
            if (textNode instanceof Text)
                textNode.setNodeValue(_text);
            else {
                org.w3c.dom.Text newTextNode = document.createTextNode(_text);
                _documentationElement.insertBefore(newTextNode, textNode);
            }
        } else {
            if (_text.length() > 0) {
                org.w3c.dom.Text newTextNode = document.createTextNode(_text);
                _documentationElement.appendChild(newTextNode);
            }
        }
        if (_isNew)
            annotation.getUserInformation().add(_documentationElement);
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return _text != null;
    }

    public Element getDocumentationElement() {
        return _documentationElement;
    }

    public boolean isNewElement() {
        return _isNew;
    }
    
    private XSDAnnotation getDocumentationAnnotation(IModelObject modelObject) throws ExecutionException {
    	if (modelObject instanceof org.eclipse.wst.sse.sieditor.model.xsd.impl.Element) {
    		return getElementDocumentationAnnotation((AbstractXSDComponent) modelObject);
    	} else if (modelObject instanceof SimpleType) {
    		return getSimpleTypeDocumentationAnnotation((SimpleType) modelObject);
    	} else if (modelObject instanceof StructureType) {
    		return getStructureTypeDocumentationAnnotation((StructureType) modelObject);
    	} else if (modelObject instanceof Schema) {
    		return getSchemaDocumentationAnnotation((Schema) modelObject);
    	}
    	
    	return null; 
    }
    
    private XSDAnnotation getSchemaDocumentationAnnotation(Schema schema) throws ExecutionException {
    	XSDSchema component = schema.getComponent();
        final List<XSDAnnotation> annotations = component.getAnnotations();
        XSDAnnotation annotation = null;
        if (annotations.size() > 0) {
            annotation = annotations.get(0);
        } else {
            annotation = executeAddAnnotationCommand(component, schema.getModelRoot(), schema);
        }
        
        return annotation;
    }

	private XSDAnnotation getElementDocumentationAnnotation(AbstractXSDComponent modelObject) throws ExecutionException {
		XSDConcreteComponent component = modelObject.getComponent();
		XSDAnnotation annotation = null;
		
        if (component instanceof XSDParticle) {
            XSDElementDeclaration element = (XSDElementDeclaration) ((XSDParticle) component).getContent();
            annotation = element.getAnnotation();
            if (null == annotation) {
               annotation = executeAddAnnotationCommand(element, modelObject.getModelRoot(), modelObject);
            }
        } else if (component instanceof XSDAttributeDeclaration) {
            XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) component;
            annotation = attribute.getAnnotation();
            if (null == annotation) {
               annotation = executeAddAnnotationCommand(attribute, modelObject.getModelRoot(), modelObject);
            }
        }
        
		return annotation;
	}
	
	private XSDAnnotation getSimpleTypeDocumentationAnnotation(SimpleType simpleType) throws ExecutionException {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) simpleType.getComponent();
		XSDAnnotation annotation = component.getAnnotation();
        if (annotation == null) {
            annotation = executeAddAnnotationCommand(component, ((Schema) simpleType.getParent()).getModelRoot(), simpleType);
        }
        
        return annotation;
	}
	
	private XSDAnnotation getStructureTypeDocumentationAnnotation(StructureType structureType) throws ExecutionException {
		XSDElementDeclaration element = structureType.getElement();
		XSDAnnotation annotation = null;
		
        if (element != null) {
            annotation = element.getAnnotation();
            if (null == annotation) {
            	annotation = executeAddAnnotationCommand(element, ((Schema) structureType.getParent()).getModelRoot(), structureType);
            }
        } else {
        	XSDTypeDefinition xsdTypeDefinition = structureType.getXSDTypeDefinition();
        	if (xsdTypeDefinition != null) {
	            annotation = xsdTypeDefinition.getAnnotation();
	            if (null == annotation) {
                    annotation = executeAddAnnotationCommand(xsdTypeDefinition, ((Schema) structureType.getParent()).getModelRoot(), structureType);
	            }
        	}
        }
        
        return annotation;
	}
	
	private XSDAnnotation executeAddAnnotationCommand(Object targetObject, IXSDModelRoot root, IModelObject object) throws ExecutionException {
		AddAnotationCommand command = new AddAnotationCommand(targetObject, root, object);
        lastAddAnnotationCommandStatus = getModelRoot().getEnv().execute(command);
		if (StatusUtils.canContinue(lastAddAnnotationCommandStatus)) {
            return command.getAnnotation();
        }
		return null;
	}
}