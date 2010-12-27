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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpace;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Facet;

public class FacetsCommandFactory {
	
	public static AddFacetCommand createAddFacetCommand(int facetId, IModelRoot model, ISimpleType type, String value) {
		EClass facetEClass = getFacetEClass(facetId);
		if (facetEClass == null) {
			throw new IllegalArgumentException("Unsupported facetID: " + facetId); //$NON-NLS-1$
		}
		return new AddFacetCommand(model, type, facetEClass, null, value);
	}
	
	public static DeleteFacetCommand createDeleteFacetCommand(int facetId, IModelRoot model, ISimpleType type) {
		EClass facetEClass = getFacetEClass(facetId);
		if (facetEClass == null) {
			throw new IllegalArgumentException("Unsupported facetID: " + facetId); //$NON-NLS-1$
		}
		return new DeleteFacetCommand(model, type, facetEClass);
	}
	
	public static AddFacetCommand createSetFacetCommand(int facetId, IModelRoot model, ISimpleType type, IFacet facet, String value) {
		EClass facetEClass = getFacetEClass(facetId);
		if (facetEClass == null) {
			throw new IllegalArgumentException("Unsupported facetID: " + facetId); //$NON-NLS-1$
		}
		return new AddFacetCommand(model, type, facetEClass, (XSDConstrainingFacet) facet.getComponent(), value);
	}	
	
	public static DeleteFacetCommand createDeleteFacetCommand(IModelRoot model, ISimpleType type, IFacet facet) {
		return new DeleteFacetCommand(model, type, facet.getComponent());
	}	

	public static AddFacetCommand createAddMinLengthFacetCommand(IModelRoot model, ISimpleType type, String length) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MIN_LENGTH_FACET, component.getMinLengthFacet(), length);
	}
	
	public static AddFacetCommand createAddMaxLengthFacetCommand(IModelRoot model, ISimpleType type, String length) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MAX_LENGTH_FACET, component.getMaxLengthFacet(), length);
	}
	
	public static AddFacetCommand createAddMinInclusiveFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MIN_INCLUSIVE_FACET, component.getMinInclusiveFacet(), value);
	}
	
	public static AddFacetCommand createAddMinExclusiveFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MIN_EXCLUSIVE_FACET, component.getMinExclusiveFacet(), value);
	}
	
	public static AddFacetCommand createAddMaxInclusiveFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MAX_INCLUSIVE_FACET, component.getMaxInclusiveFacet(), value);
	}
	
	public static AddFacetCommand createAddMaxExclusiveFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_MAX_EXCLUSIVE_FACET, component.getMaxExclusiveFacet(), value);
	}
	
	public static AddFacetCommand createAddLengthFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_LENGTH_FACET, component.getLengthFacet(), value);
	}
	
	public static AddFacetCommand createAddPatternFacetCommand(IModelRoot model, ISimpleType type, String value) {
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_PATTERN_FACET, null, value);
	}
	
	public static AddFacetCommand createAddEnumerationFacetCommand(IModelRoot model, ISimpleType type, String value) {
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_ENUMERATION_FACET, null, value);
	}
	
	public static AddFacetCommand createAddTotalDigitsFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_TOTAL_DIGITS_FACET, component.getTotalDigitsFacet(), value);
	}
	
	public static AddFacetCommand createAddFractionDigitsFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new AddFacetCommand(model, type, XSDPackage.Literals.XSD_FRACTION_DIGITS_FACET, component.getFractionDigitsFacet(), value);
	}
	
	public static DeleteFacetCommand createDeleteMinLengthFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMinLengthFacet());
	}
	
	public static DeleteFacetCommand createDeleteMaxLengthFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMaxLengthFacet());
	}
	
	public static DeleteFacetCommand createDeleteMinInclusiveFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMinInclusiveFacet());
	}
	
	public static DeleteFacetCommand createDeleteMinExclusiveFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMinExclusiveFacet());
	}
	
	public static DeleteFacetCommand createDeleteMaxInclusiveFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMaxInclusiveFacet());
	}
	
	public static DeleteFacetCommand createDeleteMaxExclusiveFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getMaxExclusiveFacet());
	}
	
	public static DeleteFacetCommand createDeleteLengthFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getLengthFacet());
	}
	
	public static DeleteFacetCommand createDeleteTotalDigitsFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getTotalDigitsFacet());
	}
	
	public static DeleteFacetCommand createDeleteFractionDigitsFacetCommand(IModelRoot model, ISimpleType type) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
        return new DeleteFacetCommand(model, type, component.getFractionDigitsFacet());
	}
	
	public static DeleteFacetCommand createDeleteEnumerationFacetCommand(IModelRoot model, ISimpleType type, IFacet facet) {
        if (facet instanceof Facet && type.equals(((Facet) facet).getParent())) {
            final XSDFacet enumeration = facet.getComponent();
            if (enumeration instanceof XSDEnumerationFacet) {
                return new DeleteFacetCommand(model, type, enumeration);
            }
        }
        
        return null;
	}
	
	public static AbstractNotificationOperation createSetEnumerationFacetCommand(
			IModelRoot model, ISimpleType type, IFacet facet, String value) {
		return new AddFacetCommand(model, type, getXSDPackage().getXSDEnumerationFacet(), (XSDConstrainingFacet) facet.getComponent(), value);
	}
	
	public static DeleteFacetCommand createDeletePatternFacetCommand(IModelRoot model, ISimpleType type, IFacet facet) {
        if (facet instanceof Facet && type.equals(((Facet) facet).getParent())) {
            final XSDFacet pattern = facet.getComponent();
            if (pattern instanceof XSDPatternFacet) {
                return new DeleteFacetCommand(model, type, pattern);
            }
        }
        
        return null;
	}
	
	public static AddFacetCommand createSetPatternFacetCommand(IModelRoot model, ISimpleType type, IFacet facet, String value) {
        return new AddFacetCommand(model, type, getXSDPackage().getXSDPatternFacet(), (XSDConstrainingFacet) facet.getComponent(), value);
	}
	
	public static AddFacetCommand createAddWhiteSpaceFacetCommand(IModelRoot model, ISimpleType type, Whitespace whitespace) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition) type.getComponent();
		XSDWhiteSpace value = null;
		switch (whitespace) {
			case COLLAPSE:
				value = XSDWhiteSpace.COLLAPSE_LITERAL;
				break;
			case PRESERVE:
				value = XSDWhiteSpace.PRESERVE_LITERAL;
				break;
			case REPLACE:
				value = XSDWhiteSpace.REPLACE_LITERAL;
				break;
			default:
				throw new IllegalArgumentException();
		}
        return new AddFacetCommand(model, type, getXSDPackage().getXSDWhiteSpaceFacet(), component.getWhiteSpaceFacet(), value.getLiteral());
	}
	
	public static DeleteFacetCommand createDeleteWhiteSpaceFacetCommand(IModelRoot model, ISimpleType type, String value) {
		XSDSimpleTypeDefinition component = (XSDSimpleTypeDefinition)  type.getComponent();
        return new DeleteFacetCommand(model, type, component.getWhiteSpaceFacet());
	}
	
	private static EClass getFacetEClass(int facetId) {
		switch (facetId) {
			case XSDPackage.XSD_LENGTH_FACET:
				return XSDPackage.Literals.XSD_LENGTH_FACET;
			case XSDPackage.XSD_MIN_LENGTH_FACET:
				return XSDPackage.Literals.XSD_MIN_LENGTH_FACET;
			case XSDPackage.XSD_MAX_LENGTH_FACET:
				return XSDPackage.Literals.XSD_MAX_LENGTH_FACET;
			case XSDPackage.XSD_MIN_INCLUSIVE_FACET:
				return XSDPackage.Literals.XSD_MIN_INCLUSIVE_FACET;
			case XSDPackage.XSD_MAX_INCLUSIVE_FACET:
				return XSDPackage.Literals.XSD_MAX_INCLUSIVE_FACET;
			case XSDPackage.XSD_MIN_EXCLUSIVE_FACET:
				return XSDPackage.Literals.XSD_MIN_EXCLUSIVE_FACET;
			case XSDPackage.XSD_MAX_EXCLUSIVE_FACET:
				return XSDPackage.Literals.XSD_MAX_EXCLUSIVE_FACET;
			case XSDPackage.XSD_TOTAL_DIGITS_FACET:
				return XSDPackage.Literals.XSD_TOTAL_DIGITS_FACET;
			case XSDPackage.XSD_FRACTION_DIGITS_FACET:
				return XSDPackage.Literals.XSD_FRACTION_DIGITS_FACET;
			case XSDPackage.XSD_PATTERN_FACET:
				return XSDPackage.Literals.XSD_PATTERN_FACET;
			case XSDPackage.XSD_ENUMERATION_FACET:
				return XSDPackage.Literals.XSD_ENUMERATION_FACET;
			case XSDPackage.XSD_WHITE_SPACE_FACET:
				return XSDPackage.Literals.XSD_WHITE_SPACE_FACET;
			default:
				return null;
		}
	}
	
}
