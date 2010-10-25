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
package org.eclipse.wst.sse.sieditor.model.wsdl.api;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.model.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * Represents the root of the ServiceInterface Objects
 * 
 * 
 * 
 */
public interface IDescription extends INamespacedObject, IExtensibleObject {
    String getLocation();

    String getRawLocation();

    Collection<IServiceInterface> getAllInterfaces();

    /**
     * Returns a list of service interfaces having the given name.<br>
     * By spec, service interface names should be unique, but in the case<br>
     * of an invalid model, such with duplicate names could exist.
     * 
     * @param name
     *            the name of the service interface, which is searched for
     * @return a list, containing one or more service interfaces
     */
    List<IServiceInterface> getInterface(String name);

    List<ISchema> getContainedSchemas();

    List<ISchema> getAllVisibleSchemas();

    ISchema[] getSchema(String namespace);

    Collection<IDescription> getReferencedServices();

    URI getContainingResource();

    Definition getComponent();

    /**
     * @return The first imported IDescription that corresponds to the given targetNamspace
     */
    IDescription getReferencedDescription(String targetNamspace);
}