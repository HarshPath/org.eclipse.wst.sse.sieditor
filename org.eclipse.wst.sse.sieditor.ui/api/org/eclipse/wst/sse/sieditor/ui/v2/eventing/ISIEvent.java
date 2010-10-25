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
package org.eclipse.wst.sse.sieditor.ui.v2.eventing;

public interface ISIEvent {
    public static final int ID_NONE = 0;
    public static final int ID_SELECT_TREENODE = 1;
    public static final int ID_ERROR_MSG = 8;
    public static final int ID_REFRESH_INPUT = 16;
    public static final int ID_REFRESH_TREE = 32;
    public static final int ID_REFRESH_TREE_NODE = 64;
    public static final int ID_EDIT_TREENODE = 128;
    public static final int ID_TREE_NODE_EXPAND = 256;

    public int getEventId();

    public Object[] getEventParams();
}
