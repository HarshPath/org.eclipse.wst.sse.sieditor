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
package org.eclipse.wst.sse.sieditor.test.v2.ui.editor;

import static org.junit.Assert.assertNotNull;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.quickFix.MissingSchemaForSchemaQuickFix;
import org.junit.Test;


public class MissingSchemaForSchemaQuickFixTest extends AbstractQuickfixTest {
    @Override
    @Test
    public void testQuickFix() throws Exception {
        MissingSchemaForSchemaQuickFix fix = new MissingSchemaForSchemaQuickFix("test quick fix", root.getSchema());
        fix.run(null);
        assertNotNull(root.getSchema().getComponent().getSchemaForSchema());
    }
}
