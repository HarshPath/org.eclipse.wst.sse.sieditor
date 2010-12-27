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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.forms.widgets.TitleRegion;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationEvent;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationListener;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.SIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ValidationListener implements IValidationListener {

    private final List<AbstractEditorPage> pages;

    private final List<String> errorMessages;
    private final List<String> warningMessages;

    public ValidationListener(final List<AbstractEditorPage> pages) {
        this.pages = pages;
        this.errorMessages = new LinkedList<String>();
        this.warningMessages = new LinkedList<String>();
    }

    @Override
    public boolean isSupportedModelObject(final Object anObject) {
        return anObject instanceof IModelObject;
    }

    @Override
    public void validationPerformed(final IValidationEvent validationEvent) {
        resetValidationMessages();
        resetPagesFormTitle();
        final List<IModelObject> validatedModelObjects = processValidationStatuses(validationEvent);
        updatePagesTreeViewers(validationEvent, validatedModelObjects);
        updatePagesFormTitle();
    }

    /**
     * Utility method. Registers the error && warning messages from the
     * validation event to the manager. Returns a collection of all the model
     * objects that are being validated by the given event.
     */
    private List<IModelObject> processValidationStatuses(final IValidationEvent validationEvent) {
        final List<IModelObject> validatedModelObjects = new LinkedList<IModelObject>();
        for (final Object source : validationEvent.getObjects()) {
            if (source instanceof IModelObject) {
                validatedModelObjects.add((IModelObject) source);
                addValidationStatuses(validationEvent.getStatusProvider().getStatus((IModelObject) source));
            }
        }
        return validatedModelObjects;
    }

    private void updatePagesTreeViewers(final IValidationEvent validationEvent, final List<IModelObject> objects) {
        for (final AbstractEditorPage page : pages) {
            final TreeNodeMapper treeNodeMapper = page.getController().getTreeNodeMapper();
            for (final IModelObject source : objects) {
                final List<ITreeNode> treeNodes = treeNodeMapper.getTreeNode(source, ~ITreeNode.CATEGORY_STATIC_ROOT);
                page.notifyEvent(new SIEvent(ISIEvent.ID_REFRESH_TREE_NODE, treeNodes.toArray(new Object[treeNodes.size()])));
            }
        }
    }

    public void addValidationStatuses(final List<IValidationStatus> statuses) {
        for (final IValidationStatus status : statuses) {
            if (status.getSeverity() == IStatus.WARNING) {
                addWarningMessage(status.getMessage());
            } else if (status.getSeverity() == IStatus.ERROR) {
                addErrorMessage(status.getMessage());
            }
        }
    }

    protected void addWarningMessage(final String message) {
        warningMessages.add(message);
    }

    protected void addErrorMessage(final String message) {
        errorMessages.add(message);
    }

    // =========================================================
    // update form titles methods
    // =========================================================

    public void updatePagesFormTitle() {
        final String titleMessageText = createTitleMessageText();
        final int titleMessageStatus = createTitleMessageStatus();
        final String titleTooltip = createTooltipMessage();

        for (final AbstractEditorPage page : pages) {
            final ScrolledForm form = page.getManagedForm().getForm();
            form.setMessage(titleMessageText, titleMessageStatus);
            setTitleTooltip(form, titleTooltip);
            page.getManagedForm().refresh();
        }
    }

    protected String createTitleMessageText() {
        if (errorMessages.size() == 0) {
            switch (warningMessages.size()) {
            case 0:
                return UIConstants.EMPTY_STRING;
            case 1:
                return warningMessages.get(0);
            default:
                return MessageFormat.format(Messages.EditorTitleMessagesManager_X_warnings_detected_page_msg, warningMessages
                        .size());
            }
        } else if (warningMessages.size() == 0) {
            switch (errorMessages.size()) {
            case 0:
                return UIConstants.EMPTY_STRING;
            case 1:
                return errorMessages.get(0);
            default:
                return MessageFormat.format(Messages.EditorTitleMessagesManager_Y_errors_detected_dlg_msg, errorMessages.size());
            }
        } else {
            if (errorMessages.size() == 1 && warningMessages.size() == 1) {
                return MessageFormat.format(Messages.EditorTitleMessagesManager_Y_error_X_warning_detected_dlg_msg, errorMessages
                        .size(), warningMessages.size());
            } else if (errorMessages.size() == 1) {
                return MessageFormat.format(Messages.EditorTitleMessagesManager_Y_error_X_warnings_detected_dlg_msg,
                        errorMessages.size(), warningMessages.size());
            } else if (warningMessages.size() == 1) {
                return MessageFormat.format(Messages.EditorTitleMessagesManager_X_errors_Y_warning_detected_dlg_msg,
                        errorMessages.size(), warningMessages.size());
            } else {
                return MessageFormat.format(Messages.EditorTitleMessagesManager_Y_errors_X_warnings_detected_dlg_msg,
                        errorMessages.size(), warningMessages.size());
            }
        }
    }

    protected int createTitleMessageStatus() {
        if (errorMessages.size() == 0) {
            if (warningMessages.size() == 0) {
                return IMessageProvider.NONE;
            }
            return IMessageProvider.WARNING;
        } else {
            return IMessageProvider.ERROR;
        }
    }

    protected String createTooltipMessage() {
        final StringBuffer buff = new StringBuffer();
        final int errors = errorMessages.size() > 5 ? 5 : errorMessages.size();

        if (!errorMessages.isEmpty()) {
            buff.append(MessageFormat.format(Messages.EditorTitleMessagesManager_errors_X_of_Y_tooltip_text, errors,
                    errorMessages.size()));
            for (int i = 0; i < errors; i++) {
                buff.append(MessageFormat.format("- {0}\n", errorMessages.get(i))); //$NON-NLS-1$
            }
        }
        if (!warningMessages.isEmpty()) {
            final int warnings = warningMessages.size() > (5 - errors) ? (5 - errors) : warningMessages.size();
            if (warnings != 0) {
                buff.append(MessageFormat.format(Messages.EditorTitleMessagesManager_warnings_X_of_Y_tooltip_text, warnings,
                        warningMessages.size()));
            } else {
                buff.append(MessageFormat.format(Messages.EditorTitleMessagesManager_warings_X_tooltip_text, warningMessages
                        .size()));
            }
            for (int i = 0; i < warnings; i++) {
                buff.append(MessageFormat.format("- {0}\n", warningMessages.get(i))); //$NON-NLS-1$
            }
        }
        return buff.toString();
    }

    /**
     * This is a workaround method to set the tooltip text to the title message.
     * Any exceptions are ignored so that the editor keeps working if the label
     * is not found.
     * 
     */
    protected void setTitleTooltip(final ScrolledForm form, final String tooltip) {
        try {
            form.getForm().getHead().getChildren()[1].setToolTipText(tooltip);
            ((TitleRegion) form.getForm().getHead().getChildren()[0]).getChildren()[0].setToolTipText(tooltip);
            ((TitleRegion) form.getForm().getHead().getChildren()[0]).getChildren()[1].setToolTipText(tooltip);
        } catch (final Exception e) {
            Logger.logError("Form title tooltip failed to be set. Perhaps the form title layout has changed.", e); //$NON-NLS-1$
        }
    }

    // =========================================================
    // reset methods
    // =========================================================

    protected void resetValidationMessages() {
        warningMessages.clear();
        errorMessages.clear();
    }

    public void resetPagesFormTitle() {
        for (final AbstractEditorPage page : pages) {
            final ScrolledForm form = page.getManagedForm().getForm();
            form.setMessage(UIConstants.EMPTY_STRING, IMessageProvider.NONE);
            setTitleTooltip(form, UIConstants.EMPTY_STRING);
        }
    }

    public List<String> getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }

    public List<String> getWarningMessages() {
        return Collections.unmodifiableList(warningMessages);
    }

}
