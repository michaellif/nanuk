package com.nanukreader.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.Librarian;
import com.nanukreader.client.library.PackagingDescriptor;

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

        final HorizontalPanel contentViewer = new HorizontalPanel();
        contentViewer.setHeight("450px");

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                //  final String url = "http://127.0.0.1:8899/wasteland.epub";

                final String url = "http://127.0.0.1:8899/moby-dick.epub";

                Librarian.instance().addBook(url, new AsyncCallback<Book>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new Error(caught);
                    }

                    @Override
                    public void onSuccess(final Book book) {
                        packagingDescriptorViewer.setText(JsonUtils.stringify(book.getPackagingDescriptor()));

                        final PackagingDescriptor descriptor = book.getPackagingDescriptor();

                        coverViewer.setUrl("data:image/png;base64," + book.getContentItem(descriptor.getCoverImageItem().getId()));

                        navViewer.setHTML(book.getContentItem(descriptor.getNavItem().getId()));

                        for (int i = 0; i < descriptor.getItemRefs().length(); i++) {
                            final int index = i;
                            final Frame contentViewport = new Frame("javascript:''");
                            contentViewport.setSize("300px", "450px");
                            contentViewport.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

                            contentViewer.add(contentViewport);

                            logger.log(Level.SEVERE, "++++++++++++" + book.getContentItem(descriptor.getItemRefs().get(index).getIdref()));

                            Document document = (contentViewport.getElement().<FrameElement> cast()).getContentDocument();
                            document.getBody().setInnerHTML(book.getContentItem(descriptor.getItemRefs().get(index).getIdref()));
                            document.getBody().getStyle().setProperty("columnWidth", "300px");
                            document.getBody().getStyle().setProperty("WebkitColumnWidth", "300px");
                            document.getBody().getStyle().setProperty("MozColumnWidth", "300px");

                        }

                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(new ScrollPanel(contentViewer));
        contentPanel.add(packagingDescriptorViewer);
        contentPanel.add(coverViewer);
        contentPanel.add(navViewer);

    }
}
