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
package org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.easymock.IExpectationSetters;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.SIEditorSearchListProvider;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SIEditorSearchListProviderTest {

    private static ArrayList finalList;
    private IExpectationSetters<IProject> expect2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        // need initialisation for further calls
        Display.getDefault();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Class implemented to replace the IComponentList passed to the provider.
     * EasyMock is not used due to the reflection currently used in the populate
     * listMethod
     * 
     *
     * 
     */
    private static class ComponentListForTesting implements IComponentList {
        public boolean calledFromUIThread = false;

        @Override
        public Iterator iterator() {
            return finalList.iterator();
        }

        @Override
        public void add(Object o) {
            finalList.add(o);
            calledFromUIThread = Display.getCurrent() != null;
        }

        public void addAll(Collection collecction) {
            finalList.addAll(collecction);
        }
    }

    @Test
    public final void testPopulateComponentListIsCalledFromUIthread() throws Exception {
        XSDSchema[] schemas = new XSDSchema[2];
        EList[] elementLists = new EList[2];
        createListsWithDefaultContent(elementLists);
        createSchemasMocks(schemas, elementLists);
        for (XSDSchema schema : schemas) {
            replay(schema);
        }
        finalList = new ArrayList();

        // the list we are going to fill via our lis provider
        final ComponentListForTesting componentList = new ComponentListForTesting();

        // mocks are provided in order to initialise the eclipse search list
        // provider
        // nothing related to the test
        IFile fileMock = createMock(IFile.class);
        IProject projectMock = createMock(IProject.class);
        expect(fileMock.getProject()).andReturn(projectMock).atLeastOnce();
        expect(projectMock.getReferencedProjects()).andReturn(new IProject[0]).atLeastOnce();
        replay(fileMock, projectMock);

        final SIEditorSearchListProvider listProvider = new SIEditorSearchListProvider(fileMock, schemas, true, true, true);

        // may be first call to schemaForSchema - generate it
        XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);

        // let the provider do it's business
        final Job job = new Job("populateComponentList") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                listProvider.populateComponentList(componentList, null, new NullProgressMonitor());
                return Status.OK_STATUS;
            }

        };
        assertFalse(componentList.calledFromUIThread);

        job.schedule();

        ThreadUtils.callOutOfUI(new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                job.join();
            }
        });

        assertTrue(componentList.calledFromUIThread);
    }

    @Test
    public final void testSIEditorSearchListProvider() throws CoreException {
        XSDSchema[] schemas = new XSDSchema[2];
        EList[] elementLists = new EList[2];
        createListsWithDefaultContent(elementLists);
        createSchemasMocks(schemas, elementLists);
        for (XSDSchema schema : schemas) {
            replay(schema);
        }
        finalList = new ArrayList();

        // the list we are going to fill via our lis provider
        IComponentList componentList = new ComponentListForTesting();

        // mocks are provided in order to initialise the eclipse search list
        // provider
        // nothing related to the test
        IFile fileMock = createMock(IFile.class);
        IProject projectMock = createMock(IProject.class);
        expect(fileMock.getProject()).andReturn(projectMock).atLeastOnce();
        expect(projectMock.getReferencedProjects()).andReturn(new IProject[0]).atLeastOnce();
        replay(fileMock, projectMock);

        SIEditorSearchListProvider listProvider = new SIEditorSearchListProvider(fileMock, schemas, true, true, true);

        verify(fileMock, projectMock);

        // may be first call to schemaForSchema - generate it
        XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);

        // let the provider do it's business
        listProvider.populateComponentList(componentList, null, new NullProgressMonitor());

        // assert our list is properly filled
        int listsSize = 0;
        for (EList list : elementLists) {
            for (Object object : list) {
                assertTrue(finalList.contains(object));
            }
            listsSize += list.size();
        }
        // assert all simple types are here
        Map<String, XSDSimpleTypeDefinition> simpleTypeIdMap = schemaForSchema.getSimpleTypeIdMap();
        for (Object key : simpleTypeIdMap.keySet()) {
            assertTrue(finalList.contains(simpleTypeIdMap.get(key)));
            listsSize++;
        }
        // assert there are no unchecked elements
        assertEquals(listsSize, finalList.size());

        for (XSDSchema schema : schemas) {
            verify(schema);
        }
    }

    @Test
    public final void testFilterListSIEditorSearchListProvider() throws CoreException {
        XSDSchema[] schemas = new XSDSchema[3];
        EList[] elementLists = new EList[3];
        createListsWithDefaultContent(elementLists);
        createSchemasMocks(schemas, elementLists);
        for (XSDSchema schema : schemas) {
            replay(schema);
        }

        // set the duplicate elements from list 1 to list 3
        for (int i = 0; i < elementLists[0].size(); i++) {
            elementLists[2].set(i, elementLists[0].get(i));
        }

        // create type to be filtered - third element from the second list - a
        // simple type
        IType typeMock = createMock(IType.class);
        Object typeComponentToBeFiltered = elementLists[1].get(2);
        expect(typeMock.getComponent()).andReturn((XSDNamedComponent) typeComponentToBeFiltered).atLeastOnce();
        replay(typeMock);

        // the list we are going to fill via our lis provider
        finalList = new ArrayList();

        IComponentList componentList = new ComponentListForTesting();

        // mocks are provided in order to initialise the eclipse search list
        // provider
        // nothing related to the test

        IFile fileMock = createMock(IFile.class);
        IProject projectMock = createMock(IProject.class);
        expect(fileMock.getProject()).andReturn(projectMock).atLeastOnce();
        expect(projectMock.getReferencedProjects()).andReturn(new IProject[0]).atLeastOnce();
        replay(fileMock, projectMock);

        SIEditorSearchListProvider listProvider = new SIEditorSearchListProvider(fileMock, schemas, true, true, false);

        verify(fileMock, projectMock);

        listProvider.setTypeToFilter(typeMock);

        // may be first call to schemaForSchema - generate it
        XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);

        // let the provider do it's business
        listProvider.populateComponentList(componentList, null, new NullProgressMonitor());

        // assert our list is properly filled
        int listsSize = 0;
        for (EList list : elementLists) {
            for (Object object : list) {
                // assert that the propper type has been filtered out
                if (typeComponentToBeFiltered.equals(object)) {
                    assertFalse(finalList.contains(object));
                    listsSize--; // remove the type which has been filtered out
                } else {
                    assertTrue(finalList.contains(object));
                }
            }
            listsSize += list.size();
        }
        // remove the duplichate -should be filtered out - elements count (the
        // size of the first list :
        listsSize -= elementLists[0].size();
        // assert all simple types are here
        Map<String, XSDSimpleTypeDefinition> simpleTypeIdMap = schemaForSchema.getSimpleTypeIdMap();
        for (Object key : simpleTypeIdMap.keySet()) {
            assertTrue(finalList.contains(simpleTypeIdMap.get(key)));
            listsSize++;
        }
        assertEquals(listsSize, finalList.size());

        verify(typeMock);
        for (XSDSchema schema : schemas) {
            verify(schema);
        }
    }

    @Test
    public final void testOnlySimpleTypesAreFilteredInSearchListProvider() throws CoreException {
        XSDSchema[] schemas = new XSDSchema[3];
        EList[] elementLists = new EList[3];
        createListsWithDefaultContent(elementLists);
        createSchemasMocks(schemas, elementLists);
        for (XSDSchema schema : schemas) {
            replay(schema);
        }

        // the list we are going to fill via our lis provider
        finalList = new ArrayList();

        IComponentList componentList = new ComponentListForTesting();

        // mocks are provided in order to initialise the eclipse search list
        // provider
        // nothing related to the test

        IFile fileMock = createMock(IFile.class);
        IProject projectMock = createMock(IProject.class);
        expect(fileMock.getProject()).andReturn(projectMock).atLeastOnce();
        expect(projectMock.getReferencedProjects()).andReturn(new IProject[0]).atLeastOnce();
        replay(fileMock, projectMock);

        SIEditorSearchListProvider listProvider = new SIEditorSearchListProvider(fileMock, schemas, true, false, false);

        verify(fileMock, projectMock);

        // let the provider do it's business
        listProvider.populateComponentList(componentList, null, new NullProgressMonitor());

        Iterator iterator = componentList.iterator();
        while (iterator.hasNext()) {
            assertTrue(iterator.next() instanceof XSDElementDeclaration);
        }

        for (XSDSchema schema : schemas) {
            verify(schema);
        }
    }

    @SuppressWarnings("unchecked")
    private void createListsWithDefaultContent(EList[] elementLists) {
        for (int i = 0; i < elementLists.length; i++) {
            elementLists[i] = new BasicEList();
            elementLists[i].add(createMock(XSDElementDeclaration.class));
            elementLists[i].add(createMock(XSDComplexTypeDefinition.class));
            elementLists[i].add(createMock(XSDSimpleTypeDefinition.class));
        }

    }

    private void createSchemasMocks(XSDSchema[] schemas, EList<XSDSchemaContent>[] lists) {
        for (int i = 0; i < schemas.length && i < lists.length; i++) {
            schemas[i] = createMock(XSDSchema.class);
            expect(schemas[i].getContents()).andReturn(lists[i]).anyTimes();
        }
    }

}
