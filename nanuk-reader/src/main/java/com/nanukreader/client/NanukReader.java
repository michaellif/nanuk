package com.nanukreader.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.nanukreader.client.bookviewer.BookViewer;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.Librarian;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    private static final Logger logger = Logger.getLogger(NanukReader.class.getName());

    private HTML packagingDescriptorViewer;

    private BookViewer bookViewer;

    @Override
    public void onModuleLoad() {
        FlowPanel contentPanel = new FlowPanel();
        RootPanel.get().add(contentPanel);

        packagingDescriptorViewer = new HTML();
        packagingDescriptorViewer.getElement().getStyle().setPadding(20, Unit.PX);

        bookViewer = new BookViewer();

        HorizontalPanel loadToolbar = new HorizontalPanel();

        Button loadMobyDickButton = new Button("Load Moby Dick", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openbook("http://127.0.0.1:8899/moby-dick.epub", "/6/20[xchapter_004]!/4/2[test6]/8");
            }
        });
        loadToolbar.add(loadMobyDickButton);

        Button loadWastelandButton = new Button("Load Wasteland", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openbook("http://127.0.0.1:8899/wasteland.epub", "/6/2[t1]!/4/4");
            }
        });
        loadToolbar.add(loadWastelandButton);

        contentPanel.add(loadToolbar);

        contentPanel.add(bookViewer);
        contentPanel.add(packagingDescriptorViewer);
    }

    void openbook(String url, final String progressCfi) {
        Librarian.instance().addBook(url, new AsyncCallback<Book>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(final Book book) {
                packagingDescriptorViewer.setText(JsonUtils.stringify(book.getPackagingDescriptor()));

                bookViewer.openBook(book, progressCfi);
            }
        });
    }
}
