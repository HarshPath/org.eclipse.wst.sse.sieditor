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
package org.eclipse.wst.sse.sieditor.test.util;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.junit.After;
import org.junit.Before;

/**
 * 
 * 
 *
 */
public abstract class ProjectBasedTest extends TestCase implements DisposableTest{
	
	private boolean _deleteOnTearDown;
	private IProject _project;

	protected ProjectBasedTest(){
		super();
	}
	
	protected ProjectBasedTest(final String name){
		super(name);
	}
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		_deleteOnTearDown = false;
		
		final String projectName = getProjectName();
		if(null == projectName)
			return;
		
		// Create a java project
		final IProgressMonitor progressMonitor = new NullProgressMonitor();		

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if(project.exists()){
			project.open(progressMonitor);
			_project = project;
			return;
		}
		project.create(progressMonitor);
		project.open(progressMonitor);
		
		final IProjectDescription description = project.getDescription();
		
		final String[] natures = description.getNatureIds();
		final String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JavaCore.NATURE_ID;
		description.setNatureIds(newNatures);
		
		project.setDescription(description, progressMonitor);
		// final IJavaProject javaProject = JavaCore.create(project);
		// project = javaProject.getProject();
		
		_project = project;

	}
	
	protected void setDeleteProjectOnTearDown() {
		_deleteOnTearDown = true;
	}
	
	protected boolean deleteOntearDown(){
		return _deleteOnTearDown;
	}
	
	protected String getNature(){
		return JavaCore.NATURE_ID;
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		// Delete the project
		if(_deleteOnTearDown)
			_project.delete(true, true, new NullProgressMonitor());
		super.tearDown();
	}
	
	protected abstract String getProjectName();
	
	protected final IProject getProject() {
		String projectName = getProjectName();
		assertNotNull("No project for DC '" + projectName + "' found.", _project); //$NON-NLS-1$ //$NON-NLS-2$
		assertTrue("Project with name '" + _project.getName() + "' is not open.", _project.isAccessible()); //$NON-NLS-1$ //$NON-NLS-2$
		return _project;
	}
	

    public void dispose() throws Exception {
        if (_project != null) {
            _project.close(null);
            _project.delete(true, new NullProgressMonitor());
            _project = null;
        }
    }
	
}
