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
package org.eclipse.wst.sse.sieditor.ui.preedit;

/**
 *	Listener for DTR
 */
public interface ISIEditorPreEditListener {
	//params: object to be edited, context of the edit
	
	/**
	 * Returns true if the given editedObject is editable otherwise returns false
	 * @param editedObject Object to be edited
	 * @return True if editable else false
	 */
	boolean startObjectEdit(Object editedObject);
	
	/**
	 * Returns true if the given deletedObject is deletable otherwise returns false
	 * @param deletedObject Object to be deleted
	 * @return True if deletion allowed else false
	 */
	boolean startObjectDelete(Object deletedObject);
}
