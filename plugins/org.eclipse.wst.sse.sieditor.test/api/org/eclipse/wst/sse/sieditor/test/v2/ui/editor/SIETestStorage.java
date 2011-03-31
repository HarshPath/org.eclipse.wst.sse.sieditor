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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.ui.v2.input.IWritableEncodedStorage;


public class SIETestStorage implements IWritableEncodedStorage {
	
	private String wsdlAsString; 
	private String changedWsdlAsString;
	private IPath fullPath;

	private String name;
	private boolean readOnly;
	private String charset;

	public String getWsdlAsString() {
		return wsdlAsString;
	}

	public void setWsdlAsString(String wsdlAsString) {
		this.wsdlAsString = wsdlAsString;
	}

	public String getChangedWsdlAsString() {
		return changedWsdlAsString;
	}

	public void setChangedWsdlAsString(String changedWsdlAsString) {
		this.changedWsdlAsString = changedWsdlAsString;
	}

	@Override
	public void setContents(InputStream source, IProgressMonitor monitor) throws CoreException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for(int ch = -1; (ch=source.read())>=0;) {
				baos.write(ch);
			}
			changedWsdlAsString = new String(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IStatus validateEdit(Object context) {
		return Status.OK_STATUS;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(wsdlAsString.getBytes());
	}

	@Override
	public IPath getFullPath() {
		return fullPath;
	}
	
	public void setFullPath(IPath fullPath) {
		this.fullPath = fullPath;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharset() throws CoreException {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullPath == null) ? 0 : fullPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SIETestStorage other = (SIETestStorage) obj;
		if (fullPath == null) {
			if (other.fullPath != null)
				return false;
		} else if (!fullPath.equals(other.fullPath))
			return false;
		return true;
	}
}
