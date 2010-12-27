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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider;

/**
 * This is the display label provider interface. Implementors are responsible
 * for the building of the labels to be displayed for each tree node.
 * 
 */
public interface ITreeNodeLabelsProvider {

    public String getDisplayName();

    /**
     * @return the label to be displayed in the tree
     */
    public String getTreeDisplayText();

    /**
     * @return the label to be displayed in the type editor
     */
    public String getTypeDisplayText();

}
