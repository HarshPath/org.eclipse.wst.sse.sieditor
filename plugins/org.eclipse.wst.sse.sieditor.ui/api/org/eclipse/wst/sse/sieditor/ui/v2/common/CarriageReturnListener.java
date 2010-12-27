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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

public class CarriageReturnListener extends KeyAdapter {
	
	private static CarriageReturnListener instance;
	
	public static CarriageReturnListener getInstance() {
		if (instance == null) {
			instance = new CarriageReturnListener();
		}
		
		return instance;
	}
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.CR) {
            ((Control) e.getSource()).notifyListeners(SWT.FocusOut, null);
        }
    }
}
