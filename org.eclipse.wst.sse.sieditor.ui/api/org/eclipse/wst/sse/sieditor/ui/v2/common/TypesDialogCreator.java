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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;


/**
 * A helper class for the properties
 */
public class TypesDialogCreator {

    // private List<IType> allTypes = new ArrayList<IType>();
    // private List<IType> primitiveTypes = new ArrayList<IType>();
    private List<IType> inlineTypes = new ArrayList<IType>();
    // private List<IType> projectTypes = new ArrayList<IType>();;

    private IModelObject root;
    private IModelObject selectedModelObject;
    private TypeSearchDialog dialog;

    boolean isSimpleType = false;
    boolean isStructureType = false;

    //	private final static String SCHEMAFORSCHEMANS = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
    //	
    // /**
    // * An array of commonly used type names
    // */
    // private final String[] commonlyUsedTypes = new String[] {
    // Messages.PROP_BROWSE_XLST,
    //			"boolean",  //$NON-NLS-1$
    //			"date",  //$NON-NLS-1$
    //			"dateTime",  //$NON-NLS-1$
    //			"double",  //$NON-NLS-1$
    //			"float",  //$NON-NLS-1$
    //			"hexBinary",  //$NON-NLS-1$
    //			"int",  //$NON-NLS-1$
    //			"string",  //$NON-NLS-1$
    //			"time" //$NON-NLS-1$
    // };
    //
    // /**
    // * An array of types for which inclusive porperty is not applicable
    // */
    // private final String[] excludeInclusiveUIControls = new String[] {
    //			"anyURI", //$NON-NLS-1$
    //			"base64Binary",//$NON-NLS-1$
    //			"ENTITIES", //$NON-NLS-1$
    //			"ENTITY", //$NON-NLS-1$
    //			"hexBinary",//$NON-NLS-1$ 
    //			"ID",//$NON-NLS-1$
    //			"IDREF", //$NON-NLS-1$
    //			"IDREFS",//$NON-NLS-1$ 
    //			"language",//$NON-NLS-1$ 
    //			"Name",//$NON-NLS-1$ 
    //			"NCName",//$NON-NLS-1$ 
    //			"NMTOKEN", //$NON-NLS-1$
    //			"NMTOKENS", //$NON-NLS-1$ 
    //			"normalizedString",//$NON-NLS-1$ 
    //			"NOTATION", //$NON-NLS-1$
    //			"QName", //$NON-NLS-1$
    //			"string",//$NON-NLS-1$ 
    //			"token" //$NON-NLS-1$
    // };
    /*	
	*//**
     * An array of types for which only "Collapse whitespace" property is
     * applicable
     */

    private static TypesDialogCreator singletonInstance;

    private TypesDialogCreator() {
    }

    public static TypesDialogCreator getInstance() {
        if (null == singletonInstance)
            singletonInstance = new TypesDialogCreator();
        return singletonInstance;
    }

    /*
     * // get the IType object for the input type(string); public IType
     * getTypeForPremtiveTypes(IModelObject root, String type) { IDescription
     * description = (IDescription) root; final Collection<ISchema>
     * visibleSchemas = description .getAllVisibleSchemas(); for (ISchema
     * visibleSchema : visibleSchemas) { if
     * (SCHEMAFORSCHEMANS.equals(visibleSchema.getNamespace())) { return
     * visibleSchema.getType(type); } } return null;
     * 
     * }
     * 
     * // this is actually to filtering the constraints UI for SimpleTypes and
     * // elements. public boolean filterOnlyWhiteSpacesUI(String type) {
     * ArrayList<String> onlyWhiteSpaceAl = new ArrayList<String>(); for (int i
     * = 0; i < showOnlyWhitespaceUIControl.length; i++)
     * onlyWhiteSpaceAl.add(showOnlyWhitespaceUIControl[i]); return
     * onlyWhiteSpaceAl.contains(type); }
     * 
     * public boolean filterNonInclusiveUI(String type) { ArrayList<String>
     * nonInclusiveAL = new ArrayList<String>(); for (int i = 0; i <
     * excludeInclusiveUIControls.length; i++)
     * nonInclusiveAL.add(excludeInclusiveUIControls[i]); return
     * nonInclusiveAL.contains(type); }
     * 
     * public String[] getCommonlyUsedTypeNames() { return commonlyUsedTypes; }
     */

    // Creates and opens the type dialog.
    /**
     * Method creating and opening a TypesDialog.<br>
     * In order to obtain the types contained in the document it finds the IDescription via the IModelObject parameter.  
     * @param modelObject 
     */
    public IType openTypesDialog(IModelObject modelObject) {
        if (modelObject == null) {
            throw new InvalidParameterException(
                    "there must be a model object for the dialogue to retrieve the model and inlite types from"); //$NON-NLS-1$
        }
        
        this.selectedModelObject = modelObject;
        
        isSimpleType = false;
        isStructureType = false;
        if (modelObject instanceof ISimpleType || (modelObject instanceof IElement && ((IElement) modelObject).isAttribute())) {
            isSimpleType = true;
        } else if (modelObject instanceof IStructureType) {
            isStructureType = true;
        }
        
        if (modelObject instanceof IDescription) {
            this.root = modelObject;
        } else {
            this.root = modelObject.getRoot();
        }

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String dialogTitle = Messages.TypesDialogCreator_dialog_title;
        dialog = new TypeSearchDialog(shell, dialogTitle);
        if (dialog != null) {
            dialog.setBlockOnOpen(true);
            dialog.create();
            int returnValue = dialog.open();
            if (returnValue == Window.OK) {
                return dialog.getTypeSelection();
            }
        }
        
        modelObject = null;
        return null;
    }

    public boolean isQNameEqual(final IType type1, final IType type2) {
        return isEqual(type1.getName(), type2.getName()) && isEqual(type1.getNamespace(), type2.getNamespace());
    }

    public boolean isEqual(final Object x, final Object y) {
        return x == null? y == null : x.equals(y);
    }

    // Method will return you all the In line types for the Schema.
    public List<IType> getInlineTypes() {
        inlineTypes.clear();
        if (null != root) {
            IDescription description = (IDescription) root;
            final Collection<ISchema> containedSchemas = description.getContainedSchemas();
            for (ISchema containedSchema : containedSchemas) {
                addTypesFromSchema(containedSchema);
                for (ISchema referredSchema : containedSchema.getAllReferredSchemas()) {
                    addTypesFromSchema(referredSchema);
                }
                
            }
            return inlineTypes;
        }
        return Collections.emptyList();
    }

    private void addTypesFromSchema(ISchema containedSchema) {
        List<IType> typeList = (List<IType>) containedSchema.getAllContainedTypes();
        for (IType inlineType : typeList) {
            if (selectedModelObject instanceof IType && isQNameEqual((IType) selectedModelObject, inlineType))
                continue;
            if (isSimpleType) {
                if (inlineType instanceof ISimpleType)
                    inlineTypes.add(inlineType);
            } else if (isStructureType) {
                if (inlineType instanceof IStructureType) {
                    if (!((IStructureType) inlineType).isElement())
                        inlineTypes.add(inlineType);
                } else {
                    inlineTypes.add(inlineType);
                }
            } else
                inlineTypes.add(inlineType);
        }
    }
}
