package com.nanukreader.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.nanukreader.client.loader.BookGrabber;
import com.nanukreader.client.loader.OcfBookLoader;
import com.nanukreader.client.locallib.Book;
import com.nanukreader.client.locallib.Librarian;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    private HTML viewer;

    @Override
    public void onModuleLoad() {
        FlowPanel contentPanel = new FlowPanel();
        RootPanel.get().add(contentPanel);

        viewer = new HTML();

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                final String url = "http://127.0.0.1:8888/wasteland.epub";

                new BookGrabber().grab(url, new AsyncCallback<Int8Array>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(Int8Array result) {
                        Book book = new OcfBookLoader(result).load();
                        viewer.setText(book.getContainerDescriptor());
                        Librarian.instance().addBook(book);
                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(viewer);

    }

}
