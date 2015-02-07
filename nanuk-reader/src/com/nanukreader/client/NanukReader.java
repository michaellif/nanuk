package com.nanukreader.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.Librarian;
import com.nanukreader.client.loader.BookGrabber;
import com.nanukreader.client.loader.OcfBookLoader;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    Logger logger = Logger.getLogger("NameOfYourLogger");

    public static final int HEIGHT = 400;

    @Override
    public void onModuleLoad() {
        FlowPanel contentPanel = new FlowPanel();
        RootPanel.get().add(contentPanel);

        final HTML containerDescriptorViewer = new HTML();
        containerDescriptorViewer.getElement().getStyle().setPadding(20, Unit.PX);

        final HTML packagingDescriptorViewer = new HTML();
        packagingDescriptorViewer.getElement().getStyle().setPadding(20, Unit.PX);

        final Image coverViewer = new Image();

        final Frame contentViewer = new Frame();
        contentViewer.setSize("100%", (HEIGHT + 50) + "px");

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
                        containerDescriptorViewer.setText(book.getContainerDescriptor());
                        packagingDescriptorViewer.setText(book.getPackagingDescriptor());
                        coverViewer.setUrl("data:image/png;base64," + book.getCoverImage());

                        logger.log(Level.SEVERE, book.getContent());

                        Document document = (contentViewer.getElement().<FrameElement> cast()).getContentDocument();
                        document.getBody().getStyle().setHeight(HEIGHT, Unit.PX);
                        document.getBody().setInnerHTML(book.getContent());
                        document.getBody().getStyle().setProperty("columnWidth", "300px");
                        document.getBody().getStyle().setProperty("WebkitColumnWidth", "300px");
                        document.getBody().getStyle().setProperty("MozColumnWidth", "300px");

                        Librarian.instance().addBook(book);
                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(containerDescriptorViewer);
        contentPanel.add(packagingDescriptorViewer);
        contentPanel.add(coverViewer);
        contentPanel.add(contentViewer);

    }
}
