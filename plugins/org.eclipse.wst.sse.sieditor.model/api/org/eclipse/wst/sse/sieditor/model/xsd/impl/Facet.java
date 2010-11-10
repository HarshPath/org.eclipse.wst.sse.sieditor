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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDFacet;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetCommand;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;

/**
 * 
 * 
 *
 */
public class Facet extends AbstractXSDComponent implements IFacet, org.eclipse.wst.sse.sieditor.model.write.xsd.api.IFacet {
	
	private final XSDConstrainingFacet _xsdFacet;
	private final SimpleType _simpleType;
	private final EClass _facetClass;

	public Facet(final IXSDModelRoot root, final XSDConstrainingFacet facet, final SimpleType simpleType,
			EClass facetClass) {
		super(root);
		Nil.checkNil(facet, "facet"); //$NON-NLS-1$
		this._xsdFacet = facet;
		this._simpleType = simpleType;
		this._facetClass = facetClass;
	}

	public String getValue() {
		return _xsdFacet.getLexicalValue();
	}

	public void setValue(String value) throws ExecutionException {
		Nil.checkNil(value, "value"); //$NON-NLS-1$
		AddFacetCommand command = new AddFacetCommand(getModelRoot(), _simpleType, _facetClass, _xsdFacet, value);
	        getModelRoot().getEnv().execute(command);
	}
	
	public XSDFacet getComponent(){
		return _xsdFacet;
	}
	
	public SimpleType getParent(){
		return _simpleType;
	}

	public Element setDocumentation(String description) {
		//Do nothing
	    return null;
	}

	public String getDocumentation() {
		//Do Nothing
		return ""; //$NON-NLS-1$
	}

}
