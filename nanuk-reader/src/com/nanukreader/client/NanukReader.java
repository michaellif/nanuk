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
import com.google.gwt.user.client.ui.ScrollPanel;
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

        final HTML contentViewer = new HTML();
        contentViewer.setSize("100%", (HEIGHT + 50) + "px");
        contentViewer.getElement().getStyle().setProperty("columnWidth", "300px");
        contentViewer.getElement().getStyle().setProperty("WebkitColumnWidth", "300px");
        contentViewer.getElement().getStyle().setProperty("MozColumnWidth", "300px");

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                //final String url = "http://epub-samples.googlecode.com/files/wasteland-20120118.epub";

                final String url = "http://127.0.0.1:8888/wasteland.epub";

                Librarian.instance().addBook(url, new AsyncCallback<Book>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new Error(caught);
                    }

                    @Override
                    public void onSuccess(Book book) {
                        packagingDescriptorViewer.setText(JsonUtils.stringify(book.getPackagingDescriptor()));
                        coverViewer.setUrl("data:image/png;base64," + book.getCoverImage());

                        contentViewer.setHTML(book.getContentItem("EPUB/wasteland-content.xhtml"));

                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(packagingDescriptorViewer);
        contentPanel.add(coverViewer);
        contentPanel.add(new ScrollPanel(contentViewer));

    }
}
