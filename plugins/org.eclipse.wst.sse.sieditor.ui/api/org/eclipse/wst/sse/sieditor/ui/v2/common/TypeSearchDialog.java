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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

/**
 * Types Search Dialog
 */
public class TypeSearchDialog extends Dialog implements SelectionListener {
    protected final int TEXT_TYPED_DELAY = 400;
    
    private String dialogTitle;

    private ArrayList<IType> typesTableViewerInput;
    protected List<IType> masterTypeList;

    protected Button all, inlineType, primitiveType;
    protected HashMap<String, Integer> tableDecoratorTrackingTool = new HashMap<String, Integer>();
    // widgets
    protected Composite topComposite;
    protected Composite bottomComposite;
    protected Text textFilter;
    protected TableViewer typesTableViewer;

    protected ViewForm fileLocationView;
    protected CLabel locationLabel;
    // keep track of the item previously selected in the table

    protected Object typeSelection;
    protected Object qualifierTextSelection;

    protected ToolBar filterToolBar;
    protected ToolItem toolItem;
    protected MenuManager fMenuManager;

    protected TypesLableProvider lableProvider;

    public TypeSearchDialog(Shell shell, String dialogTitle) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.dialogTitle = dialogTitle;
        typesTableViewerInput = new ArrayList<IType>();
        masterTypeList = new ArrayList<IType>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(false);

    }

    @Override
    protected Control createDialogArea(Composite parent) {

        getShell().setText(dialogTitle);

        Composite mainComposite = (Composite) super.createDialogArea(parent);
        GridData gData = (GridData) mainComposite.getLayoutData();
        gData.heightHint = 400;
        gData.widthHint = 270;

        Composite filterLabelAndText = new Composite(mainComposite, SWT.NONE);
        GridLayout layoutFilterLabelAndText = new GridLayout(2, false);
        layoutFilterLabelAndText.marginWidth = 0;
        layoutFilterLabelAndText.marginHeight = 5;
        filterLabelAndText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        filterLabelAndText.setLayout(layoutFilterLabelAndText);

        // Create Text textFilter

        Label filterLabel = new Label(filterLabelAndText, SWT.LEFT);
        filterLabel.setText(Messages.TypeSearchDialog_filter_filter_label);
        GridData filterLabelData = new GridData();
        filterLabelData.horizontalSpan = 4;
        filterLabelData.horizontalAlignment = 4;
        filterLabelData.grabExcessHorizontalSpace = true;
        filterLabel.setLayoutData(filterLabelData);

        textFilter = new Text(filterLabelAndText, SWT.SINGLE | SWT.BORDER);
        textFilter.setText(Messages.TypeSearchDialog_filter_text_text);
        textFilter.addModifyListener(new TextFilterModifyAdapter());
        textFilter.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                if (textFilter.getText().equals(Messages.TypeSearchDialog_filter_text_text)) {
                    textFilter.setText(UIConstants.EMPTY_STRING);
                }

            }

            public void focusLost(FocusEvent e) {

            }

        });
        textFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData textFilterData = new GridData();
        textFilterData.horizontalSpan = 4;
        textFilterData.horizontalAlignment = GridData.FILL;
        textFilterData.grabExcessHorizontalSpace = true;
        textFilter.setLayoutData(textFilterData);

        Group optionsComposite = new Group(mainComposite, SWT.NONE);
        optionsComposite.setText(Messages.TypeSearchDialog_filter_scope_label);
        GridLayout layoutOptions = new GridLayout(4, false);
        layoutOptions.marginWidth = 20;
        layoutOptions.marginHeight = 10;
        optionsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        optionsComposite.setLayout(layoutOptions);

        all = new Button(optionsComposite, SWT.RADIO);
        all.setText(Messages.TypeSearchDialog_all_label);
        all.setSelection(true);
        all.addSelectionListener(this);

        inlineType = new Button(optionsComposite, SWT.RADIO);
        inlineType.setText(Messages.TypeSearchDialog_inline_label);
        inlineType.addSelectionListener(this);
        primitiveType = new Button(optionsComposite, SWT.RADIO);
        primitiveType.addSelectionListener(this);
        primitiveType.setText(Messages.TypeSearchDialog_primitive_label);

        createTypesTableViewer(mainComposite);

        // Populate the Component TableViewer via the provider
        typesTableViewer.setContentProvider(new TypesTableContentProvider());
        lableProvider = new TypesLableProvider();
        typesTableViewer.setLabelProvider(lableProvider);
        typesTableViewer.setSorter(new ViewerSorter());
        populateMasterTypeList();
        typesTableViewer.setInput(typesTableViewerInput);

        refreshTableViewer(null, ""); //$NON-NLS-1$

        return mainComposite;
    }

    /*
     * Creates the Table TableViewer.
     */
    private void createTypesTableViewer(Composite base) {
        typesTableViewer = createFiltersWithdTableViewer(base);

        typesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                updateCanFinish();
            }
        });

        typesTableViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }

        });
    }

    protected TableViewer createFiltersWithdTableViewer(Composite comp) {
        Composite labelAndFilter = new Composite(comp, SWT.NONE);
        labelAndFilter.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        labelAndFilter.setLayout(layout);

        labelAndFilter.setLayout(layout);
        Label tableLabel = new Label(labelAndFilter, SWT.NONE);

        tableLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        tableLabel.setText(Messages.TypeSearchDialog_types_table_label);

        TableViewer tableViewer = new TableViewer(new Table(labelAndFilter, SWT.SINGLE | SWT.BORDER));
        Control TableWidget = tableViewer.getTable();
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        TableWidget.setLayoutData(gd);
        return tableViewer;
    }

    /**
     * Looking at each item in the Table. If there are other items with same
     * name , then add extra info (namespace, file) into the the text label of
     * all these duplicated items. - This should be called everytime the Table
     * viewer is refreshed..
     */
    protected void decorateTable() {
        tableDecoratorTrackingTool.clear();

        // init the name-duplicates counter
        if (null != typesTableViewerInput) {
            for (int i = 0; i < typesTableViewerInput.size(); i++) {
                Object currentItem = typesTableViewerInput.get(i);
                String name = lableProvider.getName(currentItem);
                Integer count = tableDecoratorTrackingTool.get(name);
                if (count == null) {
                    tableDecoratorTrackingTool.put(name, Integer.valueOf(1));
                } else {
                    tableDecoratorTrackingTool.put(name, Integer.valueOf(count.intValue() + 1));
                }
            }

            // Modify/decorate those items in the Table that have duplicated
            // name
            TableItem[] items = typesTableViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                Object currentItem = items[i].getData();
                Integer count = tableDecoratorTrackingTool.get(lableProvider.getName(currentItem));
                if (count != null && count.intValue() > 1) {
                    String fileName = null;
                    if (currentItem instanceof IType) {
                        IModelObject root = ((IType) currentItem).getRoot();
                        if (root != null) {
                            if (root instanceof ISchema) {
                                fileName = getFileName(((ISchema) root).getLocation());
                            } else if (root instanceof IDescription) {
                                fileName = getFileName(((IDescription) root).getLocation());
                            }
                        }
                    }
                    if (fileName != null)
                        items[i].setText(lableProvider.getName(currentItem) + "  -  " + fileName); //$NON-NLS-1$
                    else
                        items[i].setText(lableProvider.getName(currentItem));
                }
            }
        }
    }

    private String getFileName(String filePath) {
        if (filePath == null)
            return null;
        if (filePath.lastIndexOf('/') != -1)
            return filePath.substring(filePath.lastIndexOf('/') + 1);
        return null;
    }

    /*
     * Returns the processed filter text for the Text field. Inserts a "."
     * before each supported meta-character.
     */
    protected String getProcessedFilterString() {
        return processFilterString(textFilter.getText());
    }

    /*
     * If supported metacharacters are used in the filter string, we need to
     * insert a "." before each metacharacter.
     */
    private String processFilterString(String inputString) {
        if (!(inputString.equals(""))) { //$NON-NLS-1$
            inputString = insertString("*", ".", inputString); //$NON-NLS-1$ //$NON-NLS-2$
            inputString = insertString("?", ".", inputString); //$NON-NLS-1$ //$NON-NLS-2$
            inputString = inputString + ".*"; //$NON-NLS-1$
        } else {
            inputString = ".*"; //$NON-NLS-1$
        }

        return inputString.toLowerCase(Locale.ENGLISH);
    }

    /*
     * Helper method to insert a "." before each metacharacter in the
     * search/filter string.
     */
    private String insertString(String target, String newString, String string) {
        StringBuffer stringBuffer = new StringBuffer(string);

        int index = stringBuffer.indexOf(target);
        while (index != -1) {
            stringBuffer = stringBuffer.insert(index, newString);
            index = stringBuffer.indexOf(target, index + newString.length() + target.length());
        }

        return stringBuffer.toString();
    }

    /*
     * Listens to changes made in the text filter widget
     */
    private class TextFilterModifyAdapter implements ModifyListener {
        public void modifyText(ModifyEvent e) {
            if (e.widget == textFilter) {
                if (delayedEvent != null) {
                    delayedEvent.CANCEL = true;
                }

                delayedEvent = new DelayedEvent();

				Display.getCurrent().timerExec(TEXT_TYPED_DELAY, delayedEvent);
            }
        }
    }

    // TODO... do we really need one instance?
    private DelayedEvent delayedEvent;
    private IResource currentResource;

    /*
     * Update the Type TableViewer when the text filter is modified. Use a
     * DelayedEvent so we don't update on every keystroke.
     */
    private class DelayedEvent implements Runnable {
        public boolean CANCEL = false;

        public void run() {
            if (!CANCEL) {
                populateTableWithTypes();
            }
        }
    }

    /*
     * Populate the Types TreeViewer with items.
     */
    protected void populateMasterTypeList() {

        masterTypeList.clear();

        List<IType> primitiveList = new ArrayList<IType>(Schema.getSchemaForSchema().getAllContainedTypes());
        if (!primitiveList.isEmpty()) {
            masterTypeList.addAll(primitiveList);
        }

        List<IType> inlinetypes = TypesDialogCreator.getInstance().getInlineTypes();
        if (inlinetypes != null && !inlinetypes.isEmpty())
            masterTypeList.addAll(inlinetypes);
    }

    protected void refreshTableViewer(List<IType> typeList, String filterText) {
        if (typeList == null)
            return;
        typesTableViewerInput.clear();
        Pattern regex = Pattern.compile(filterText);
        Iterator<IType> it = typeList.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            String itemString = lableProvider.getText(item);
            Matcher m = regex.matcher(itemString.toLowerCase(Locale.ENGLISH));
            if (itemString.toLowerCase(Locale.ENGLISH).startsWith(filterText) || m.matches()) {
                typesTableViewerInput.add((IType) item);
            }
        }
        typesTableViewer.refresh();
        decorateTable();

    }

    /*
     * If there is a selection in the ComponentTreeViewer, enable OK
     */
    protected void updateCanFinish() {
        IStructuredSelection selection = (IStructuredSelection) typesTableViewer.getSelection();
        if (selection.getFirstElement() != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    @Override
    protected void okPressed() {
        IStructuredSelection selection = (IStructuredSelection) typesTableViewer.getSelection();
        typeSelection = selection.getFirstElement();

        super.okPressed();
    }

    // return you the selected type.
    public IType getTypeSelection() {
        IType type = (IType) typeSelection;
        return type;
    }

    private static class TypesTableContentProvider implements ITreeContentProvider {
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof List<?>) {
                return ((List<?>) parentElement).toArray();
            }
            return new Object[0];
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            if (getChildren(element).length > 0) {
                return true;
            }
            return false;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }
    }

    private static class TypesLableProvider extends LabelProvider implements ILabelProvider {

        TypesLableProvider() {
        }

        public IFile getFile(Object component) {
            return null;
        }

        public ILabelProvider getLabelProvider() {
            return this;
        }

        public String getName(Object dataType) {
            if (dataType instanceof IType) {

                IType type = (IType) dataType;

                String namespace = null;
                if (type.getNamespace() == null)
                    namespace = MessageFormat.format(Messages.TypeSearchDialog_msg_emtpy_namespace_container, new String[]{Messages.TypeSearchDialog_msg_empty_namespace});
                else
                    namespace = type.getNamespace();

                String tableString = type.getName() + "  -  " + namespace; //$NON-NLS-1$

                if (!tableString.trim().equals(UIConstants.EMPTY_STRING))
                    return tableString;
            }

            return UIConstants.EMPTY_STRING;
        }

        @Override
        public String getText(Object element) {
            return getName(element);
        }

        @Override
        public Image getImage(Object dataType) {
            if (dataType instanceof ISimpleType) {
                return Activator.getDefault().getImage(Activator.NODE_SIMPLE_TYPE);
            } else if (dataType instanceof IStructureType) {
                if (((IStructureType) dataType).isElement())
                    return Activator.getDefault().getImage(Activator.NODE_ELEMENT);
                return Activator.getDefault().getImage(Activator.NODE_STRUCTURE_TYPE);
            } else if (dataType instanceof IType) {
                return Activator.getDefault().getImage(Activator.NODE_PRIMITIVE);
            }
            return null;
        }
    }

    public IResource getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(IResource currentResource) {
        this.currentResource = currentResource;
    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    public void widgetSelected(SelectionEvent e) {
        populateTableWithTypes();
    }

    private void populateTableWithTypes() {
        if (null != typesTableViewerInput)
            typesTableViewerInput.clear();
        List<IType> typesList = null;
        if (all.getSelection()) {
            populateMasterTypeList();
            typesList = new ArrayList<IType>(masterTypeList);
            populateTableWithTypes(typesList);
        }
        if (inlineType.getSelection()) {
            typesList = new ArrayList<IType>(TypesDialogCreator.getInstance().getInlineTypes());
            populateTableWithTypes(typesList);
        }
        if (primitiveType.getSelection()) {
            typesList = new ArrayList<IType>(Schema.getSchemaForSchema().getAllContainedTypes());
            populateTableWithTypes(typesList);
        }
    }

    private void populateTableWithTypes(List<IType> typesList) {
        if (textFilter.getText() != null && !UIConstants.EMPTY_STRING.equals(textFilter.getText().trim())) {
            filterTypes(typesList);
        } else {
            typesTableViewerInput = (ArrayList<IType>) typesList;
            typesTableViewer.setInput(typesTableViewerInput);
            typesTableViewer.refresh();
            decorateTable();
        }
    }

    private void filterTypes(List<IType> typesList) {
        refreshTableViewer(typesList, getProcessedFilterString());
        // Select first match
        if (typesTableViewer.getTable().getItemCount() > 0) {
            TableItem item = typesTableViewer.getTable().getItems()[0];
            TableItem items[] = new TableItem[1];
            items[0] = item;
            typesTableViewer.getTable().setSelection(items);
        }
        updateCanFinish();
    }
}
