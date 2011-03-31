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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.test.SIEditorTestsPlugin;


@SuppressWarnings("nls")
public class ResourceUtils {

    /**
     * Name of folder which contains WSDL files
     */
    protected static final String Document_FOLDER_NAME = "data";

    private static SoftReference<File> tempDir;

    /**
     * Messages
     */
    protected static final String CANNOT_CREATE_IN = " cannot be created in project ";

    protected static final String PROJ_WAS_NOT_CREATED = "Test project was not created.";

    protected static final String CANNOT_FIND_TEST_FILE = "Cannot find test file ";

    protected static final String NO_WELL_XML = "Not a well formed XML: ";

    protected static final String METAMODELS_NOT_DEPLOYED = "Metamodels are not deployed;";

    protected static final String PARA_STRING_INCORRECT = "Parameter of type String is empty or null.";

    protected static final String PARA_IFILE_INCORRECT = "Parameter of type IFile is null or file does not exists.";

    protected static final String PARA_IFOLDER_INCORRECT = "Parameter of type IFolder is null or folder does not exists.";

    protected static final String PARA_IPROJECT_INCORRECT = "Parameter of type IProject is null or project does not exist.";

    protected static final String PARA_INPUTSTREAM_INCORRECT = "Parameter of type Inputstream is null or not available.";

    protected static final String PARA_PATH_INCORRECT = "Parameter of type Path is null or has invalid path.";

    /**
     * Returns the {@link InputStream} for a file in our test
     * {@link #Document_FOLDER_NAME} folder.
     * 
     * @param fileName
     *            file name
     * @return the {@link InputStream} for this file
     * @throws IOException
     *             on I/O error
     * @throws CoreException
     *             from Eclipse
     */
    public static InputStream getInput(final String fileName) throws IOException {
        assertTrue(PARA_STRING_INCORRECT, fileName != null && fileName.length() > 0);

        InputStream in = null;
        URL url = null;

        final String source = IPath.SEPARATOR + Document_FOLDER_NAME + IPath.SEPARATOR + fileName;

        url = FileLocator.find(SIEditorTestsPlugin.getDefault().getBundle(), new Path(source), null);

        assertNotNull("cannot find " + source, url);

        assertNotNull(CANNOT_FIND_TEST_FILE + fileName, url = FileLocator.toFileURL(url));

        assertNotNull(in = url.openStream());
        assertTrue(in.available() >= 0);
        return in;
    }

    /**
     * Copies a file from the test.core plug-in to the folder
     * <code>fldrName</code> in the specified test project.
     * 
     * @param fileName
     *            file name
     * @param fldrName
     *            name of target folder, can be <tt>null</tt>
     * @param testProject
     *            target project
     * @param targetFileName
     *            name of the copied file
     * @return handle to the copied file
     * @throws IOException
     *             on I/O error
     * @throws CoreException
     *             from Eclipse
     */
    public static IFile copyFileIntoTestProject(final String fileName, final String fldrName, final IProject testProject,
            final String targetFileName) throws IOException, CoreException {
        // precondition
        IFile copiedFile = null;
        IFolder data = null;

        // check params
        assertTrue(PARA_IPROJECT_INCORRECT, testProject != null && testProject.exists());

        if (fldrName != null) {
            data = createFolderInProject(testProject, fldrName);
        }

        final InputStream in = getInput(fileName);

        // create copy of current WSDL file in test project
        assertNotNull(copiedFile = CreateFileInFolder(data, targetFileName, in, testProject));
        in.close();

        // postcondition
        assertTrue(copiedFile.getName() + CANNOT_CREATE_IN + testProject.getName(), copiedFile.exists());

        return copiedFile;
    }
   
    /**
     * Copies a file from the plug-in to the folder <code>fldrName</code> in
     * test project.
     * 
     * @param fileName
     *            file name
     * @param fldrName
     *            folder name
     * @param testProject
     *            target project
     * @return {@link IFile} file from copied file
     * @throws IOException
     *             on I/O error
     * @throws CoreException
     *             from Eclipse
     */
    public static IFile copyFileIntoTestProject(final String fileName, final String fldrName, IProject testProject)
            throws IOException, CoreException {
        return copyFileIntoTestProject(fileName, fldrName, testProject, new Path(IPath.SEPARATOR + Document_FOLDER_NAME
                + IPath.SEPARATOR + fileName).lastSegment());
    }

    /**
     * Copies a file from the test.core plug-in to the folder
     * <code>fldrName</code> in the specified test project.
     * 
     * @param file
     *            file name
     * @param fldrName
     *            name of target folder, can be <tt>null</tt>
     * @param testProject
     *            target project
     * @param targetFileName
     *            name of the copied file
     * @return handle to the copied file
     * @throws IOException
     *             on I/O error
     * @throws CoreException
     *             from Eclipse
     */
    public static IFile copyFileIntoTestProject(final File file,final String fldrName, final IProject testProject, final String targetFileName) throws IOException, CoreException {
        // precondition
        IFile copiedFile = null;
        IFolder data = null;

        // check params
        assertTrue(PARA_IPROJECT_INCORRECT, testProject != null && testProject.exists());

        if (fldrName != null) {
            data = createFolderInProject(testProject, fldrName);
        }

        assertNotNull("file does not exist",file);
        assertTrue("file does not exist",file.exists());
        
        final InputStream in = new FileInputStream(file);

        // create copy of current WSDL file in test project
        assertNotNull(copiedFile = CreateFileInFolder(data, targetFileName, in, testProject));
        in.close();

        // postcondition
        assertTrue(copiedFile.getName() + CANNOT_CREATE_IN + testProject.getName(), copiedFile.exists());

        return copiedFile;
    }
    
    /**
     * Get the URL of WSDL file.
     * 
     * @param fileName
     *            String file name
     * @return URL of WSDL file
     * @throws Exception
     *             e.g. IOException on I/O error
     */
    public static URL getWsdlFileUrl(final String fileName) throws Exception {

        // preconditions
        URL url = null;
        Path filelSourcePath = null;

        assertTrue(PARA_STRING_INCORRECT, fileName != null && fileName.length() > 0);
        assertNotNull(filelSourcePath = new Path(IPath.SEPARATOR + Document_FOLDER_NAME + IPath.SEPARATOR + fileName));
        // get current wsdl file from data folder in this plug-in
        assertNotNull(CANNOT_FIND_TEST_FILE + fileName, url = FileLocator.toFileURL(FileLocator.find(SIEditorTestsPlugin
                .getDefault().getBundle(), filelSourcePath, null)));
        return url;

    }

    /**
     * Copies a file from the plug-in to the standard
     * {@link #Document_FOLDER_NAME} in test project.
     * 
     * @param fileName
     *            String file name
     * @param testProject
     *            target project
     * @return {@link IFile} copy of file
     * @throws IOException
     *             on I/O error
     * @throws CoreException
     *             from Eclipse
     */
    public static IFile copyFileIntoTestProject(final String fileName, IProject testProject) throws IOException, CoreException {

        return copyFileIntoTestProject(fileName, Document_FOLDER_NAME, testProject);

    }

    /**
     * Check the existence of a file.
     * 
     * @param file
     *            file to check
     */
    public static void checkFileExistsInFileSystem(IFile file) {
        IPath path = null;

        // precondition
        assertTrue(PARA_IFILE_INCORRECT, file != null && file.exists());

        assertNotNull(path = file.getRawLocation());
        assertTrue(path.isAbsolute());
        File testFile = null;

        // postcondition
        assertNotNull(testFile = path.toFile());
        assertTrue(testFile.exists());
    }

    /**
     * Create a folder if it does not exist.
     * 
     * @param project
     *            target project
     * @param folderName
     *            String name of folder
     * @return {@link IFolder} folder
     * @throws CoreException
     *             from Eclipse
     */
    public static IFolder createFolderInProject(IProject project, String folderName) throws CoreException {

        // precondition
        IFolder newFolder = null;

        assertTrue(PARA_IPROJECT_INCORRECT, project != null && project.exists());
        assertTrue(PARA_STRING_INCORRECT, folderName != null && folderName.length() > 0);

        newFolder = project.getFolder(folderName);
        if (!newFolder.exists()) {
            newFolder.create(true, true, null);
        }

        // postcondition
        assertTrue(newFolder != null && newFolder.exists());

        return newFolder;
    }

    /**
     * Create a new file in a folder. Overwrite content, if file already exists.
     * 
     * @param folder
     *            {@link IFolder} folder, can be <tt>null</tt>
     * @param fileName
     *            String file name
     * @param in
     *            {@link InputStream} content as input stream
     * @return {@link IFile} the file
     * @throws CoreException
     *             from Eclipse
     * @throws IOException
     *             on I/O error
     */
    public static IFile CreateFileInFolder(IFolder folder, String fileName, InputStream in, IProject project)
            throws CoreException, IOException {
        project.refreshLocal(IResource.DEPTH_INFINITE, null);
        // precondition
        IFile newFile = null;

        assertTrue(PARA_IFILE_INCORRECT, fileName != null && fileName.length() > 0);
        assertTrue(PARA_INPUTSTREAM_INCORRECT, in != null && in.available() >= 0);

        if (folder != null) {
            newFile = folder.getFile(fileName);
        } else {
            newFile = project.getFile(fileName);
        }

        if (newFile.exists()) {
        	final ResourceAttributes resourceAttributes = newFile.getResourceAttributes();
        	resourceAttributes.setReadOnly(false);
        	newFile.setResourceAttributes(resourceAttributes);
            newFile.setContents(in, true, false, null);
        } else {
            newFile.create(in, false, null);
        }

        // postcondition
        assertTrue(newFile.exists());

        return newFile;
    }

    /**
     * Writes a text to the temporary file system with a certain file name and
     * returns the file.
     * 
     * @param text
     *            to be written to file system
     * @param filenameWithExtension
     *            file name with extension
     * @return file to which the text was written
     * @throws IOException
     * @throws NullPointerException
     *             if <tt>text</tt> or <tt>filename</tt> is <tt>null</tt>
     */
    public static File writeToTempDir(String text, String filenameWithExtension) throws IOException {

        if (filenameWithExtension == null) {
            throw new NullPointerException("filenameWithExtension must not be null");
        }

        if (text == null) {
            throw new NullPointerException("text must not be null");
        }
        return writeToTempDir(text.getBytes("UTF-8"), filenameWithExtension);

    }

    /**
     * Writes byte array to the temporary file system with a certain file name
     * and returns the file.
     * 
     * @param bytes
     *            to be written to file system
     * @param filenameWithExtension
     *            file name with extension
     * @return file to which the text was written
     * @throws IOException
     * @throws NullPointerException
     *             if <tt>bytes</tt> or <tt>filename</tt> is <tt>null</tt>
     */
    public static File writeToTempDir(byte[] bytes, String filenameWithExtension) throws IOException {

        if (filenameWithExtension == null) {
            throw new NullPointerException("filenameWithExtension must not be null");
        }

        if (bytes == null) {
            throw new NullPointerException("bytes must not be null");
        }
        File tempFile = new File(getTempDir(), filenameWithExtension);

        File parent = tempFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (tempFile.exists()) {
            tempFile.delete();
        }

        writeToTempDir(bytes, tempFile);
        return tempFile;
    }

    /**
     * Writes the contents of an input stream to the temporary file system with
     * a certain file name and returns the file.
     * 
     * @param is
     *            to be written to file system
     * @param filenameWithExtension
     *            file name with extension
     * @return file to which the text was written
     * @throws IOException
     * @throws NullPointerException
     *             if <tt>is</tt> or <tt>filename</tt> is <tt>null</tt>
     */
    public static File writeToTempDir(InputStream is, String filenameWithExtension) throws IOException {
        if (is == null) {
            throw new NullPointerException("is must not be null");
        }
        return writeToTempDir(read(is, true), filenameWithExtension);
    }

    public static void writeToTempDir(byte[] bytes, File file) throws IOException {
        if (bytes == null) {
            throw new NullPointerException("is must not be null");
        }
        if (file == null) {
            throw new NullPointerException("file must not be null");
        }
        FileOutputStream fs = new FileOutputStream(file);
        fs.write(bytes);
        fs.flush();
        fs.close();
    }

    /**
     * Reads a file which is located in the temporary file system. It is assumed
     * that the encoding is UTF-8
     * 
     * @param text
     *            to be read from file system
     * @param filenameWithExtension
     *            file name with extension and with path name
     * @return file in string format, <tt>null</tt> if the path or the file does
     *         not exist
     * @throws IOException
     * @throws NullPointerException
     *             if or <tt>filename</tt> is <tt>null</tt>
     */
    public static String readFromTempDir(String filenameWithExtension) throws IOException {

        if (filenameWithExtension == null) {
            throw new NullPointerException("filenameWithExtension must not be null");
        }

        File tempFile = new File(getTempDir(), filenameWithExtension);

        File parent = tempFile.getParentFile();
        if (!parent.exists()) {
            return null;
        }

        if (tempFile.exists()) {
            byte[] bytes = read(new FileInputStream(tempFile), true);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, "UTF8");
        } else {
            return null;
        }

    }

    /**
     * Getter for {@link #tempDir}. It is created if it does not exist.
     * 
     * @return {@link #tempDir}
     * @throws IOException
     *             on I/O error
     */
    public static final File getTempDir() throws IOException {

        if (tempDir == null || tempDir.get() == null) {
            File tempFile = File.createTempFile("tmp", ".xsd");
            tempDir = new SoftReference<File>(tempFile.getParentFile());
            tempFile.delete();
        }
        return tempDir.get();

    }

    /**
     * Reads byte array from an Input Stream.
     * 
     * @param in
     *            input stream
     * @param closeStream
     *            if <code>true</code> the stream is closed after the reading.
     * @throws NullPointerException
     *             if <code>in</code> is <code>null</code>.
     */
    public static byte[] read(InputStream in, boolean closeStream) throws IOException {

        if (in == null)
            throw new NullPointerException("Parameter in is null.");

        ByteArrayOutputStream lBaos = null;

        try {

            lBaos = new ByteArrayOutputStream();
            byte[] lBuffer = new byte[1024];
            for (int readNumber = in.read(lBuffer); readNumber > -1;) {
                lBaos.write(lBuffer, 0, readNumber);
                readNumber = in.read(lBuffer);
            }
            return lBaos.toByteArray();

        } finally {

            if (lBaos != null)
                lBaos.close();

            if (closeStream) {
                in.close();
            }
        }

    }
    
    public static String getContents(IFile file, String encoding) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		InputStream in = file.getContents();
		try {
			byte buf[] = new byte[1024];
			int readed = 0;
			while((readed = in.read(buf)) > 0) {
				out.write(buf, 0, readed);
			}
		} 
		finally {
			in.close();
		}
		return out.toString(encoding);
	}
    
    public static void setContents(IFile file, String newContents) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(newContents.getBytes("UTF-8"));
		file.setContents(in, IFile.FORCE, null);
	}

}
