package org.eclipse.wst.sse.sieditor.test.ui;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.listeners.IPageChangedListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.PageChangedListenersManager;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.ModelReconcilerPageChangedLister;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.SelectionUpdaterPageChangedListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.TreeRefresherPageChangedListener;
import org.eclipse.wst.sse.sieditor.ui.view.impl.SISourceEditorPart;
import org.junit.Test;

public class PageChangedListenersManagerTest extends SIEditorBaseTest {

    @Test
    public void testCreatePageChangedListeners() {
        SISourceEditorPart sourcePage = new SISourceEditorPart();
        PageChangedListenersManager pageChangedListenersManager = new PageChangedListenersManager(sourcePage);

        List<IPageChangedListener> listeners = pageChangedListenersManager.getListeners();
        assertEquals(ModelReconcilerPageChangedLister.class, listeners.get(0).getClass());
        assertEquals(SelectionUpdaterPageChangedListener.class, listeners.get(1).getClass());
        assertEquals(TreeRefresherPageChangedListener.class, listeners.get(2).getClass());
    }

    @Test
    public void testNotifyPageChangedListeners() throws Exception {
        final int OLD_PAGE_INDEX = 1;
        final int NEW_PAGE_INDEX = 0;

        // open the editor in order to get an instance of it in the protected
        // "editor" variable
        IModelRoot modelRoot = getModelRoot("pub/csns/renameMultiReferredPart.wsdl", //$NON-NLS-1$
                "renameMultiReferredPart.wsdl", ServiceInterfaceEditor.EDITOR_ID);

        editor.pageChange(OLD_PAGE_INDEX);

        IPageChangedListener mockPageChangedListener = createMock(IPageChangedListener.class);
        mockPageChangedListener.pageChanged(NEW_PAGE_INDEX, OLD_PAGE_INDEX, editor.getPages(), modelRoot);
        replay(mockPageChangedListener);
        
        editor.getPageChangedListenersManager().addPageChangeListener(mockPageChangedListener);
        
        editor.pageChange(NEW_PAGE_INDEX);

        verify(mockPageChangedListener);
    }

}
