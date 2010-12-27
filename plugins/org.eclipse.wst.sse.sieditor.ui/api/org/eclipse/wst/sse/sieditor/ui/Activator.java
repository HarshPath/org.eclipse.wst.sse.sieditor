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
package org.eclipse.wst.sse.sieditor.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID

    public static final String PLUGIN_ID = "org.eclipse.wst.sse.sieditor.ui"; //$NON-NLS-1$

    public static final String NODE_SI = "NODE_SI"; //$NON-NLS-1$
    
    public static final String NODE_SI_GRAY = "NODE_SI_GRAY"; //$NON-NLS-1$

    public static final String NODE_OPERATION = "NODE_OPERATION"; //$NON-NLS-1$
    
    public static final String NODE_OPERATION_GRAY = "NODE_OPERATION_GRAY"; //$NON-NLS-1$

    public static final String NODE_OPER_INPUT = "NODE_OPER_INPUT"; //$NON-NLS-1$
    
    public static final String NODE_OPER_INPUT_GRAY = "NODE_OPER_INPUT_GRAY"; //$NON-NLS-1$

    public static final String NODE_OPER_OUTPUT = "NODE_OPER_OUTPUT"; //$NON-NLS-1$
    
    public static final String NODE_OPER_OUTPUT_GRAY = "NODE_OPER_OUTPUT_GRAY"; //$NON-NLS-1$

    public static final String NODE_OPER_FAULTS = "NODE_OPER_FAULTS"; //$NON-NLS-1$
    
    public static final String NODE_OPER_FAULTS_GRAY = "NODE_OPER_FAULTS_GRAY"; //$NON-NLS-1$

    public static final String NODE_OPER_FAULT_OBJECT = "NODE_OPER_FAULT_OBJECT"; //$NON-NLS-1$
    
    public static final String NODE_OPER_FAULT_OBJECT_GRAY = "NODE_OPER_FAULT_OBJECT_GRAY"; //$NON-NLS-1$

    public static final String NODE_PARAMETER = "NODE_PARAMETER"; //$NON-NLS-1$

    public static final String NODE_PARAMETER_GRAY = "NODE_PARAMETER_GRAY"; //$NON-NLS-1$

    public static final String TOOLBAR_ADD_SI = "TOOLBAR_ADD_SI"; //$NON-NLS-1$

    public static final String TOOLBAR_ADD_CHILD = "TOOLBAR_ADD_CHILD"; //$NON-NLS-1$

    public static final String TOOLBAR_DELETE = "TOOLBAR_DELETE"; //$NON-NLS-1$

    public static final String NODE_PRIMITIVE = "NODE_PRIMITIVE"; //$NON-NLS-1$

    public static final String NODE_INLINE = "NODE_INLINE"; //$NON-NLS-1$

    public static final String NODE_PROJECT = "NODE_PROJECT"; //$NON-NLS-1$

    public static final String NODE_XSD_FILE = "NODE_XSD_FILE"; //$NON-NLS-1$

    public static final String NODE_WSDL_FILE = "NODE_WSDL_FILE"; //$NON-NLS-1$

    public static final String NODE_NAMESPACE = "NODE_NAMESPACE"; //$NON-NLS-1$

    public static final String NODE_NAMESPACE_GRAY = "NODE_NAMESPACE_GRAY"; //$NON-NLS-1$

    public static final String NODE_SIMPLE_TYPE_CATEGORY = "NODE_SIMPLE_TYPE_CATEGORY"; //$NON-NLS-1$

    public static final String NODE_STRUCTURE_TYPE_CATEGORY = "NODE_STRUCTURE_TYPE_CATEGORY"; //$NON-NLS-1$

    public static final String NODE_SIMPLE_TYPE = "NODE_SIMPLE_TYPE"; //$NON-NLS-1$

    public static final String NODE_SIMPLE_TYPE_GRAY = "NODE_SIMPLE_TYPE_GRAY"; //$NON-NLS-1$

    public static final String NODE_STRUCTURE_TYPE = "NODE_STRUCTURE_TYPE"; //$NON-NLS-1$

    public static final String NODE_STRUCTURE_TYPE_GRAY = "NODE_STRUCTURE_TYPE_GRAY"; //$NON-NLS-1$

    public static final String NODE_ELEMENT = "NODE_ELEMENT"; //$NON-NLS-1$

    public static final String NODE_ELEMENT_GRAY = "NODE_ELEMENT_GRAY"; //$NON-NLS-1$
    
    public static final String NODE_ELEMENT_GRAY_REF = "NODE_ELEMENT_GRAY_REF"; //$NON-NLS-1$

    public static final String NODE_ATTRIBUTE = "NODE_ATTRIBUTE"; //$NON-NLS-1$

    public static final String NODE_ATTRIBUTE_GRAY = "NODE_ATTRIBUTE_GRAY"; //$NON-NLS-1$
    
    public static final String NODE_ATTRIBUTE_GRAY_REF = "NODE_ATTRIBUTE_GRAY_REF"; //$NON-NLS-1$

    public static final String NODE_IMPORTED_TYPES = "NODE_IMPORTED_TYPES"; //$NON-NLS-1$

    public static final String TOOL_ITEM_ADD_NAMESPACE = "TOOL_ITEM_ADD_NAMESPACE"; //$NON-NLS-1$

    public static final String TOOL_ITEM_ADD_CHILD = "TOOL_ITEM_ADD_CHILD"; //$NON-NLS-1$

    public static final String TOOL_ITEM_DELETE = "TOOL_ITEM_DELETE"; //$NON-NLS-1$

    public static final String TOOL_ITEM_GO = "TOOL_ITEM_GO"; //$NON-NLS-1$

    public static final String MESSAGE_ERROR = "MESSAGE_ERROR"; //$NON-NLS-1$

    public static final String TOOLBAR_MOVEUP = "TOOLBAR_MOVEUP"; //$NON-NLS-1$

    public static final String TOOLBAR_MOVEDOWN = "TOOLBAR_MOVEDOWN"; //$NON-NLS-1$

    public static final String TOOLBAR_SHOWTYPE = "TOOLBAR_SHOWTYPE"; //$NON-NLS-1$

    public static final String TOOLBAR_ALPHABETIC_SORT = "TOOLBAR_ALPHABETIC_SORT"; //$NON-NLS-1$

    public static final String TOOLBAR_TOGGLE_CATEGORIES = "TOOLBAR_TOGGLE_CATEGORIES"; //$NON-NLS-1$

    public static final String DATA_TYPES_TITLE_IMAGE = "DATA_TYPES_TITLE_IMAGE"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private boolean mImageRegistryInitialized;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        if (this.mImageRegistryInitialized) { // avoid creation on exit
            this.getImageRegistry().dispose();
        }
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    private void addImageDescriptorToRegistryIfPresent(ImageRegistry reg, String key, URL installURL, String location) {
        URL u = null;
        try {
            u = new URL(installURL, location);
            u.getContent();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }

        ImageDescriptor id = ImageDescriptor.createFromURL(u);
        reg.put(key, id);
    }

    public Image getImage(String key) {
        return getImageRegistry().get(key);
    }

    public Image getImage(Image originalImage, int severity) {
        if (severity == IStatus.OK || severity == IStatus.INFO) {
            return originalImage;
        }

        final String fullKey = originalImage.hashCode() + UIConstants.UNDERSCORE + severity;
        Image image = getImageRegistry().get(fullKey);
        if (image == null) {
            String decorationId = null;
            switch (severity) {
            case IStatus.ERROR:
                decorationId = FieldDecorationRegistry.DEC_ERROR;
                break;
            case IStatus.WARNING:
                decorationId = FieldDecorationRegistry.DEC_WARNING;
                break;
            }

            final Image errImage = FieldDecorationRegistry.getDefault().getFieldDecoration(decorationId).getImage();
            if (errImage == null) {
                return originalImage;
            }

            DecorationOverlayIcon overlay;

            overlay = new DecorationOverlayIcon(originalImage, ImageDescriptor.createFromImage(errImage), IDecoration.BOTTOM_LEFT);
            image = overlay.createImage();
            getImageRegistry().put(fullKey, image);
        }

        return image;
    }

    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        this.mImageRegistryInitialized = true;
        URL lInstallURL = null;
        try {
            Bundle bundle = getDefault().getBundle(); // 
            lInstallURL = FileLocator.resolve(bundle.getEntry("/")); //$NON-NLS-1$
        } catch (IOException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to initialize image registry.", e);//$NON-NLS-1$
            return;
        }

        addImageDescriptorToRegistryIfPresent(reg, NODE_SI, lInstallURL, "icons/SI Panel/SI_obj.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPERATION, lInstallURL, "icons/SI Panel/operation_obj.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_INPUT, lInstallURL, "icons/SI Panel/input_obj.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_OUTPUT, lInstallURL, "icons/SI Panel/output_obj.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_FAULTS, lInstallURL, "icons/SI Panel/fault_folder.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_FAULT_OBJECT, lInstallURL, "icons/SI Panel/fault_obj.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_PARAMETER, lInstallURL, "icons/SI Panel/parameter_obj.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_PARAMETER_GRAY, lInstallURL, "icons/SI Panel/parameter_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_ADD_SI, lInstallURL, "icons/SI Panel/tool Bar/add_SI.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_ADD_CHILD, lInstallURL, "icons/SI Panel/tool Bar/add_child.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_DELETE, lInstallURL, "icons/SI Panel/tool Bar/delete_obj.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_PARAMETER_GRAY, lInstallURL, "icons/SI Panel/parameter_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_PRIMITIVE, lInstallURL, "icons/Type Browser Panel/primitive_type.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_INLINE, lInstallURL, "icons/Type Browser Panel/inline_type.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_PROJECT, lInstallURL, "icons/Type Browser Panel/project_type.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_XSD_FILE, lInstallURL, "icons/Type Browser Panel/XSDFile_gray.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_WSDL_FILE, lInstallURL, "icons/Type Browser Panel/wsdl_file_obj_gray.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_NAMESPACE, lInstallURL, "icons/Type Browser Panel/namespace.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_NAMESPACE_GRAY, lInstallURL,
                "icons/Type Browser Panel/namespace_gray.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_SIMPLE_TYPE_CATEGORY, lInstallURL,
                "icons/Type Browser Panel/simple_type_folder.gif"); //$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_STRUCTURE_TYPE_CATEGORY, lInstallURL,
                "icons/Type Browser Panel/structured_type_folder.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_SIMPLE_TYPE, lInstallURL, "icons/Type Browser Panel/XSDSimpleType.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_SIMPLE_TYPE_GRAY, lInstallURL,
                "icons/Type Browser Panel/XSDSimpleType_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_STRUCTURE_TYPE, lInstallURL,
                "icons/Type Browser Panel/XSDComplexType.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_STRUCTURE_TYPE_GRAY, lInstallURL,
                "icons/Type Browser Panel/XSDComplexType_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_ELEMENT, lInstallURL, "icons/Type Browser Panel/XSDElement.gif");//$NON-NLS-1$		
        addImageDescriptorToRegistryIfPresent(reg, NODE_ELEMENT_GRAY, lInstallURL, "icons/Type Browser Panel/XSDElement_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_ELEMENT_GRAY_REF, lInstallURL, "icons/Type Browser Panel/XSDElement_gray_ref.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_ATTRIBUTE, lInstallURL, "icons/Type Browser Panel/XSDAttribute.gif");//$NON-NLS-1$		
        addImageDescriptorToRegistryIfPresent(reg, NODE_ATTRIBUTE_GRAY, lInstallURL,
                "icons/Type Browser Panel/XSDAttribute_gray.gif");//$NON-NLS-1$	
        addImageDescriptorToRegistryIfPresent(reg, NODE_ATTRIBUTE_GRAY_REF, lInstallURL,
        "icons/Type Browser Panel/XSDAttribute_gray_ref.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_IMPORTED_TYPES, lInstallURL,
                "icons/Type Browser Panel/imported_types_folder.gif");//$NON-NLS-1$ 
        addImageDescriptorToRegistryIfPresent(reg, TOOL_ITEM_ADD_NAMESPACE, lInstallURL,
                "icons/Type Browser Panel/tool bar/add_namespace.gif");//$NON-NLS-1$	
        addImageDescriptorToRegistryIfPresent(reg, TOOL_ITEM_ADD_CHILD, lInstallURL,
                "icons/Type Browser Panel/tool bar/add_child.gif");//$NON-NLS-1$	
        addImageDescriptorToRegistryIfPresent(reg, TOOL_ITEM_DELETE, lInstallURL,
                "icons/Type Browser Panel/tool bar/delete_obj.gif");//$NON-NLS-1$	
        addImageDescriptorToRegistryIfPresent(reg, TOOL_ITEM_GO, lInstallURL, "icons/Type Browser Panel/tool bar/go.gif");//$NON-NLS-1$	
        addImageDescriptorToRegistryIfPresent(reg, MESSAGE_ERROR, lInstallURL, "icons/message_error.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_MOVEUP, lInstallURL, "icons/SI Panel/tool Bar/arrow_up.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_MOVEDOWN, lInstallURL, "icons/SI Panel/tool Bar/arrow_down.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_SHOWTYPE, lInstallURL, "icons/SI Panel/tool Bar/show_type.gif");//$NON-NLS-1$

        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_ALPHABETIC_SORT, lInstallURL, "icons/Alphabetic_sort.gif"); //$NON-NLS-1$

        addImageDescriptorToRegistryIfPresent(reg, TOOLBAR_TOGGLE_CATEGORIES, lInstallURL, "icons/showCategories.gif"); //$NON-NLS-1$
        
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_FAULT_OBJECT_GRAY, lInstallURL, "icons/SI Panel/fault_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_SI_GRAY, lInstallURL, "icons/SI Panel/SI_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_FAULTS_GRAY, lInstallURL, "icons/SI Panel/fault_folder_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_INPUT_GRAY, lInstallURL, "icons/SI Panel/input_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPERATION_GRAY, lInstallURL, "icons/SI Panel/operation_obj_gray.gif");//$NON-NLS-1$
        addImageDescriptorToRegistryIfPresent(reg, NODE_OPER_OUTPUT_GRAY, lInstallURL, "icons/SI Panel/output_obj_gray.gif");//$NON-NLS-1$

        addImageDescriptorToRegistryIfPresent(reg, DATA_TYPES_TITLE_IMAGE, lInstallURL, "icons/Type Browser Panel/DataTypes.gif"); //$NON-NLS-1$
    }

    /**
     * Method copied from {@link TabbedPropertySheetPage}. Creates flat
     * {@link CCombo} using the supplied {@link FormToolkit}.
     * 
     * @param toolkit
     * @param parent
     * @param comboStyle
     * @return
     */
    public CCombo createCCombo(FormToolkit toolkit, Composite parent, int comboStyle) {
        CCombo combo = new CCombo(parent, comboStyle);
        toolkit.adapt(combo, true, false);
        // Bugzilla 145837 - workaround for no borders on Windows XP
        if (toolkit.getBorderStyle() == SWT.BORDER) {
            combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        }
        return combo;
    }

}
