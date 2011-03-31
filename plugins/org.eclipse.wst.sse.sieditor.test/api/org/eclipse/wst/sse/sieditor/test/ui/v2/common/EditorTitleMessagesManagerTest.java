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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ValidationListener;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;

public class EditorTitleMessagesManagerTest {

    private static final String[] WARNINGS = { "warning1", "warning2", "warning3", "warning4", "warning5" };
    private static final String[] ERRORS = { "error1", "error2", "error3", "error4", "error5", "error6" };

    private static final String EXPECTED_TOOLTIP_1 = "Errors (5 of 6)\n- error1\n- error2\n- error3\n- error4\n- error5\nWarnings (5)";
    private static final String EXPECTED_TITLE_1 = "6 errors, 5 warnings detected";

    private static final String EXPECTED_TOOLTIP_2 = "Warnings (2 of 2)\n- warning1\n- warning2\n";
    private static final String EXPECTED_TITLE_2 = "2 warnings detected";

    private static final String EXPECTED_TOOLTIP_3 = "Errors (3 of 3)\n- error1\n- error2\n- error3\nWarnings (2 of 3)\n- warning1\n- warning2\n";
    private static final String EXPECTED_TITLE_3 = "3 errors, 3 warnings detected";

    private static final String EXPECTED_TOOLTIP_4 = "Errors (1 of 1)\n- error1\n";
    private static final String EXPECTED_TOOLTIP_5 = "Warnings (1 of 1)\n- warning1\n";

    private static final String EXPECTED_TOOLTIP_6 = "Errors (1 of 1)\n- error1\nWarnings (3 of 3)\n- warning1\n- warning2\n- warning3\n";
    private static final String EXPECTED_TITLE_6 = "1 error, 3 warnings detected";

    private static final String EXPECTED_TOOLTIP_7 = "Errors (3 of 3)\n- error1\n- error2\n- error3\nWarnings (1 of 1)\n- warning1\n";
    private static final String EXPECTED_TITLE_7 = "3 errors, 1 warning detected";

    @Before
    public void setUp() {

    }

    @Test
    public void createCreateMessages() {
        final EditorTitleMessagesManagerExpose manager = new EditorTitleMessagesManagerExpose(new LinkedList<AbstractEditorPage>());
        testMessages(manager, 6, 5, EXPECTED_TOOLTIP_1, EXPECTED_TITLE_1, IMessageProvider.ERROR);
        testMessages(manager, 0, 2, EXPECTED_TOOLTIP_2, EXPECTED_TITLE_2, IMessageProvider.WARNING);
        testMessages(manager, 3, 3, EXPECTED_TOOLTIP_3, EXPECTED_TITLE_3, IMessageProvider.ERROR);
        testMessages(manager, 1, 0, EXPECTED_TOOLTIP_4, ERRORS[0], IMessageProvider.ERROR);
        testMessages(manager, 0, 1, EXPECTED_TOOLTIP_5, WARNINGS[0], IMessageProvider.WARNING);
        testMessages(manager, 1, 3, EXPECTED_TOOLTIP_6, EXPECTED_TITLE_6, IMessageProvider.ERROR);
        testMessages(manager, 3, 1, EXPECTED_TOOLTIP_7, EXPECTED_TITLE_7, IMessageProvider.ERROR);
    }

    private void testMessages(final EditorTitleMessagesManagerExpose manager, final int errors, final int warnings,
            final String tooltip, final String title, final int status) {
        manager.resetValidationMessages();
        assertEquals("", manager.createTooltipMessage());
        assertEquals("", manager.createTitleMessageText());
        assertEquals(IMessageProvider.NONE, manager.createTitleMessageStatus());

        for (int i = 0; i < warnings; i++) {
            manager.addWarningMessage(WARNINGS[i]);
        }
        for (int i = 0; i < errors; i++) {
            manager.addErrorMessage(ERRORS[i]);
        }
        assertEquals(tooltip, manager.createTooltipMessage());
        assertEquals(title, manager.createTitleMessageText());
        assertEquals(status, manager.createTitleMessageStatus());
    }

    private class EditorTitleMessagesManagerExpose extends ValidationListener {

        public EditorTitleMessagesManagerExpose(final List<AbstractEditorPage> pages) {
            super(pages);
        }

        @Override
        public int createTitleMessageStatus() {
            return super.createTitleMessageStatus();
        }

        @Override
        public String createTitleMessageText() {
            return super.createTitleMessageText();
        }

        @Override
        public String createTooltipMessage() {
            return super.createTooltipMessage();
        }

        @Override
        public void resetValidationMessages() {
            super.resetValidationMessages();
        }
        
        @Override
        public void addErrorMessage(final String message) {
            super.addErrorMessage(message);
        }
        @Override
        public void addWarningMessage(final String message) {
            super.addWarningMessage(message);
        }
    }

}
