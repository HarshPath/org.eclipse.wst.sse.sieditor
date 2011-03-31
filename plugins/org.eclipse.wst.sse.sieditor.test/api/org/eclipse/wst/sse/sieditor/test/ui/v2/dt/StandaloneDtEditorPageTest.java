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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StandaloneDtEditorPage;


public class StandaloneDtEditorPageTest extends DataTypesEditorPageTest {
    
    @Override
    protected DataTypesEditorPage createPage(TestFormEditor editor) {
        DataTypesEditorPage page = new StandaloneDtEditorPage(editor) {
            protected void createMasterDetailsBlock(IManagedForm managedForm) {
                
            }
            @Override
            protected void createContextMenu() {
            }
            
            @Override
            protected void createNsComposite(IManagedForm managedForm) {
            }

            @Override
            protected void initNs() {
            }
            
        };
        return page;
    }
}
