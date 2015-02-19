/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 14, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer.dev;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.nanukreader.client.bookviewer.IBookViewer;
import com.nanukreader.client.bookviewer.ItemPageLocation;
import com.nanukreader.client.bookviewer.PageContentViewport;
import com.nanukreader.client.bookviewer.PageEstimator;
import com.nanukreader.client.library.Book;

public class DevBookViewer extends FlowPanel implements IBookViewer {

    private static final Logger logger = Logger.getLogger(DevBookContentViewport.class.getName());

    private final DevBookContentViewport contentViewport;

    private final SimplePanel coverViewer;

    private final ScrollPanel navViewer;

    private final PageEstimator pageEstimator;

    private Book book;

    public DevBookViewer() {
        contentViewport = new DevBookContentViewport(this);
        add(contentViewport);

        HorizontalPanel bottomPanel = new HorizontalPanel();
        add(bottomPanel);

        pageEstimator = new PageEstimator(this);
        bottomPanel.add(pageEstimator);

        coverViewer = new SimplePanel();
        coverViewer.setPixelSize(300, 450);
        bottomPanel.add(coverViewer);

        navViewer = new ScrollPanel();
        navViewer.setPixelSize(300, 450);
        bottomPanel.add(navViewer);

        PageContentViewport.setAllViewportSizes(300, 450);
    }

    @Override
    public PageEstimator getPageEstimator() {
        return pageEstimator;
    }

    @Override
    public void openBook(Book book, String progressCfi) {
        contentViewport.clearView();
        this.book = book;
        show(progressCfi);

        book.getContentItem(book.getPackagingDescriptor().getCoverImageItem().getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String content) {
                Image image = new Image("data:image/png;base64," + content);
                image.getElement().getStyle().setProperty("maxHeight", "100%");
                image.getElement().getStyle().setProperty("maxWidth", "100%");
                coverViewer.setWidget(image);
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
                navViewer.setWidget(new HTML(content));
            }
        });
    }

    @Override
    public Book getBook() {
        return book;
    }

    public void show(final String progressCfi) {
        logger.log(Level.INFO, "Page [" + progressCfi + "] is loading");
        pageEstimator.getPageLocation(progressCfi, new AsyncCallback<ItemPageLocation>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, "Can't convert CFI [" + progressCfi + "] to Page Loaction.");
            }

            @Override
            public void onSuccess(ItemPageLocation mainPageLocation) {
                contentViewport.loadPageContent(mainPageLocation, 2);
                logger.log(Level.INFO, "Page [" + mainPageLocation + "] loaded successfully");
            }

        });

    }

}
