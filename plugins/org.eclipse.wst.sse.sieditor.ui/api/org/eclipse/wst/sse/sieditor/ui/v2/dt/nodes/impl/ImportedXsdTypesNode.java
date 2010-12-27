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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;

/**
 * A special case for the XSD data types editor. The root object is {@link ISchema} rather than {@link IDescription}
 */
public class ImportedXsdTypesNode extends ImportedTypesNode {

	public ImportedXsdTypesNode(IModelObject modelObject,
			TreeNodeMapper nodeMapper) {
		super(modelObject, nodeMapper);
	}

	@Override
	protected List<ISchema> getContainedSchemas() {
		List<ISchema> result = new ArrayList<ISchema>();
		result.add((ISchema)getModelObject());
		return result;
	}
}
