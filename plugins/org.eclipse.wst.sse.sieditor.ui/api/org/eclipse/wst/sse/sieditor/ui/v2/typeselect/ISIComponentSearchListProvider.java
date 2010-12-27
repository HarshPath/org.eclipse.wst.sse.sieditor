/**
 * 
 */
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
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;

/**
 * Temporary interface in order not to break the ESR colleagues,
 *         and still be able to submit until the end of the tact and the code
 *         line pull. This interface should be deleted when the ESR colleagues
 *         decide to add the dependency to org.eclispe.wst.common.ui plugin in
 *         theri project. Everywhere used, it should be replaced with it's super interface.
 *         (Idealy - only one place - the return type of the method {@link AbstractFormPageController#createSearchListProvider()}
 */
public interface ISIComponentSearchListProvider extends IComponentSearchListProvider {

}
