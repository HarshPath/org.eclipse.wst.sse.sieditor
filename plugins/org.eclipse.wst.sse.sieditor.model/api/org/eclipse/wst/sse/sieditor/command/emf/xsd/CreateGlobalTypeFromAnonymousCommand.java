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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class CreateGlobalTypeFromAnonymousCommand extends AbstractNotificationOperation {
	
    public final static String ELEMENT_SUFFIX = "element"; //$NON-NLS-1$
    public final static String SEPERATOR = "."; //$NON-NLS-1$
    
    private final String _name;
    private final AbstractType _type;
    private final IElement element;

    public CreateGlobalTypeFromAnonymousCommand(final IModelRoot root, final ISchema modelObject, final IElement element,
            final String name) {
        super(root, modelObject, Messages.CreateGlobalTypeFromAnonymousCommand_create_global_type_from_anonymous_command_label);
        this.element = element;
        this._type = (AbstractType) element.getType();
        this._name = name;
    }

    public org.eclipse.core.runtime.IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor,
            org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
    	
        final Schema targetSchema = (Schema) modelObject;
        XSDTypeDefinition anonymouseTypeDefinition = (XSDTypeDefinition) _type.getComponent();
        anonymouseTypeDefinition.setName(_name);
        anonymouseTypeDefinition.setTargetNamespace(targetSchema.getNamespace());
        XSDSchema xsdSchema = targetSchema.getComponent();
        xsdSchema.getContents().add(anonymouseTypeDefinition);

        XSDConcreteComponent elementComponent = element.getComponent();
        if (elementComponent instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) elementComponent;
            final XSDParticleContent content = particle.getContent();
            if (content instanceof XSDElementDeclaration) {
                ((XSDElementDeclaration) content).setTypeDefinition(anonymouseTypeDefinition);
            }
        }

        if (elementComponent instanceof XSDAttributeDeclaration) {
            XSDAttributeDeclaration attributeDeclaration = (XSDAttributeDeclaration) elementComponent;
            attributeDeclaration.setTypeDefinition((XSDSimpleTypeDefinition) anonymouseTypeDefinition);
        }

        return Status.OK_STATUS;
    }
    
    @Override
    public boolean canExecute() {
    	return element.getType() instanceof AbstractType;
    }
}
