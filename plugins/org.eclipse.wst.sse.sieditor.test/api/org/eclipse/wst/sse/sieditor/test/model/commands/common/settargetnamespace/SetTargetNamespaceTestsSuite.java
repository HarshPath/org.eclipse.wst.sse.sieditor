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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.settargetnamespace;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
    
    SetDefinitionTargetNamespaceTest1.class,
    SetDefinitionTargetNamespaceTest2.class,
    SetDefinitionTargetNamespaceTest3.class,
    SetDefinitionTargetNamespaceTest4.class,
    
    SetSchemaTargetNamespaceTest1.class,
    SetSchemaTargetNamespaceTest2.class,
    SetSchemaTargetNamespaceTest3.class,
    SetSchemaTargetNamespaceTest4.class,
    SetSchemaTargetNamespaceTest5.class
    
})
public class SetTargetNamespaceTestsSuite {

}
