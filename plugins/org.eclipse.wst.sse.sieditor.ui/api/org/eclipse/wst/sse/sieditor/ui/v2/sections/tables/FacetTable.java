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
package org.eclipse.wst.sse.sieditor.ui.v2.sections.tables;

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class FacetTable {

	private final TableViewer tableViewer;
    private final Button addButton;
    private final Button removeButton;
    private final TableViewerColumn column;
    
    private RemoveHandler removeHandler;
    private AddHandler addHandler;
    
    private final SelectionListener addButtonListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			
			if (addHandler != null && addHandler.addElement()) {
				Object input = tableViewer.getInput();
				IStructuredContentProvider contentProvider = (IStructuredContentProvider)tableViewer.getContentProvider();
				
				Object[] elements = contentProvider.getElements(input);
				if(elements != null && elements.length > 0) {
					Object newlySelected = elements[elements.length-1];
					tableViewer.setSelection(new StructuredSelection(newlySelected), true);
				}
			}				
		}
    };
    
    private final SelectionListener removeButtonListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			
			if (removeHandler != null) {

				Object selectedForRemove = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();

				Object input = tableViewer.getInput();
				IStructuredContentProvider contentProvider = (IStructuredContentProvider)tableViewer.getContentProvider();
				
				Object[] elements = contentProvider.getElements(input);
				int index = Arrays.asList(elements).indexOf(selectedForRemove);
				
				removeHandler.removeElement(selectedForRemove);

				int itemCount = tableViewer.getTable().getItemCount();
				if (itemCount > 0) {
					index = Math.min(index, itemCount - 1);

					elements = contentProvider.getElements(input);
					Object newlySelected = elements[index];
					tableViewer.setSelection(new StructuredSelection(newlySelected), true);
					removeButton.forceFocus();
				}
			}				
			
							
		}
    };
    
    private final ISelectionChangedListener tableViewerSelectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			boolean isEmpty = event.getSelection().isEmpty();
			removeButton.setEnabled(!isEmpty);
		}
	};
	private boolean isEnabled = true;

    public FacetTable(final FormToolkit toolkit, final Composite composite, final IContentProvider contentProvider,
            final ILabelProvider labelProvider) {

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 1;
        layout.marginHeight = 0;
        layout.marginTop = 1; // added in order the top line of displayed
        // tables to be visible.
        composite.setLayout(layout);

        final Table table = toolkit.createTable(composite, SWT.V_SCROLL | SWT.H_SCROLL);
        tableViewer = new TableViewer(table);
        table.setLinesVisible(true);
        //setUp the horizontalBat to be invisible
        table.getHorizontalBar().setVisible(false);

        column = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewer.setContentProvider(contentProvider);
        tableViewer.setLabelProvider(labelProvider);
		tableViewer.addSelectionChangedListener(tableViewerSelectionListener);
		
        addButton = toolkit.createButton(composite, Messages.SimpleTypeConstraintsSection_add, SWT.PUSH);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).applyTo(addButton);
        addButton.addSelectionListener(addButtonListener);
        
        removeButton = toolkit.createButton(composite, Messages.SimpleTypeConstraintsSection_remove, SWT.PUSH);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(removeButton);
		removeButton.addSelectionListener(removeButtonListener );
		
        final int maxHeight = table.getItemHeight() * 2;
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(1, 2).hint(SWT.DEFAULT, maxHeight)
                .applyTo(table);
        table.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(final ControlEvent e) {
                final Rectangle area = table.getBounds();
                int width = area.width - 2 * table.getBorderWidth();
                if (table.getItems().length > 2) {
                    // Subtract the scrollbar width from the total column
                    // width
                    // if a vertical scrollbar will be required
                    final Point vBarSize = table.getVerticalBar().getSize();
                    width -= vBarSize.x;
                }
                column.getColumn().setWidth(width);
            }
        });

        toolkit.paintBordersFor(composite);

    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setAddHandler(final AddHandler handler) {
        addHandler = handler;
    }

    public void setRemoveHandler(final RemoveHandler handler) {
    	removeHandler = handler;
    }

    public void setEditingSupport(final EditingSupport editingSupport) {
        column.setEditingSupport(editingSupport);
    }

    public Control getAddPatternButton() {
        return addButton;
    }

    public Control getRemovePatternButton() {
        return removeButton;
    }
    
    public void setEnabled(boolean isEnabled) {
    	this.isEnabled = isEnabled;
   		removeButton.setEnabled(isEnabled && !tableViewer.getSelection().isEmpty());
   		addButton.setEnabled(isEnabled);
   		tableViewer.getTable().setEnabled(isEnabled);
    }
    
    public boolean isEnabled() {
    	return isEnabled;
    }
    
    public interface AddHandler {
		boolean addElement();		
	}
	
	public interface RemoveHandler {
		void removeElement(Object toRemove);		
	}
	
    
}