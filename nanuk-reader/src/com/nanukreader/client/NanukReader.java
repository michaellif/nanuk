package com.nanukreader.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.Librarian;

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

        final HTML packagingDescriptorViewer = new HTML();
        packagingDescriptorViewer.getElement().getStyle().setPadding(20, Unit.PX);

        final Image coverViewer = new Image();

        final Frame contentViewer = new Frame();
        contentViewer.setSize("100%", (HEIGHT + 50) + "px");

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final String url = "http://127.0.0.1:8888/wasteland.epub";

                Librarian.instance().addBook(url, new AsyncCallback<Book>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new Error(caught);
                    }

                    @Override
                    public void onSuccess(Book book) {
                        packagingDescriptorViewer.setText(book.getPackagingDescriptor());
                        coverViewer.setUrl("data:image/png;base64," + book.getCoverImage());

                        Document document = (contentViewer.getElement().<FrameElement> cast()).getContentDocument();
                        document.getBody().getStyle().setHeight(HEIGHT, Unit.PX);
                        document.getBody().setInnerHTML(book.getContentItem("EPUB/wasteland-content.xhtml"));
                        document.getBody().getStyle().setProperty("columnWidth", "300px");
                        document.getBody().getStyle().setProperty("WebkitColumnWidth", "300px");
                        document.getBody().getStyle().setProperty("MozColumnWidth", "300px");
                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(packagingDescriptorViewer);
        contentPanel.add(coverViewer);
        contentPanel.add(contentViewer);

    }
}
