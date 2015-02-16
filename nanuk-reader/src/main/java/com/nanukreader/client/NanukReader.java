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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.nanukreader.client.bookviewer.BookViewer;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.Librarian;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    private static final Logger logger = Logger.getLogger(NanukReader.class.getName());

    @Override
    public void onModuleLoad() {
        FlowPanel contentPanel = new FlowPanel();
        RootPanel.get().add(contentPanel);

        final HTML packagingDescriptorViewer = new HTML();
        packagingDescriptorViewer.getElement().getStyle().setPadding(20, Unit.PX);

        final Image coverViewer = new Image();

        final HTML navViewer = new HTML();

        final BookViewer bookViewer = new BookViewer();

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                // final String url = "http://127.0.0.1:8899/wasteland.epub";

                final String url = "http://127.0.0.1:8899/moby-dick.epub";

                Librarian.instance().addBook(url, new AsyncCallback<Book>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new Error(caught);
                    }

                    @Override
                    public void onSuccess(final Book book) {
                        packagingDescriptorViewer.setText(JsonUtils.stringify(book.getPackagingDescriptor()));

                        book.getContentItem(book.getPackagingDescriptor().getCoverImageItem().getId(), new AsyncCallback<String>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                // TODO Auto-generated method stub
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(String content) {
                                coverViewer.setUrl("data:image/png;base64," + content);
                            }
                        });

                        book.getContentItem(book.getPackagingDescriptor().getNavItem().getId(), new AsyncCallback<String>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                // TODO Auto-generated method stub
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(String content) {
                                navViewer.setHTML(content);
                            }
                        });

                        bookViewer.openBook(book);

                    }
                });
            }
        });

        contentPanel.add(loadButton);
        contentPanel.add(bookViewer);
        contentPanel.add(packagingDescriptorViewer);
        contentPanel.add(coverViewer);
        contentPanel.add(navViewer);

    }

}
