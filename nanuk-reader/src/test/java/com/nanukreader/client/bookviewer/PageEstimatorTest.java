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
 * Created on Feb 16, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import name.pehl.totoe.xml.client.HasText;
import name.pehl.totoe.xml.client.Node;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nanukreader.client.AbstractCfiTest;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.ManifestItem;
import com.nanukreader.client.library.PackagingDescriptor;
import com.nanukreader.client.library.SpineItem;

public class PageEstimatorTest extends AbstractCfiTest {

    private Book book;

    private IBookViewer bookViewer;

    private PageEstimator pageEstimator;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        PackagingDescriptor packagingDescriptor = PackagingDescriptor.create();

        JsArray<ManifestItem> manifestItems = JsArray.createArray().<JsArray<ManifestItem>> cast();
        packagingDescriptor.setManifestItems(manifestItems);

        JsArray<SpineItem> spineItems = JsArray.createArray().<JsArray<SpineItem>> cast();
        packagingDescriptor.setSpineItems(spineItems);

        book = new Book(packagingDescriptor, null);
        book.addContentItem("chapter1",
                "<html><head></head><body><div id='chapter1'><div id='title1'>title1</div><div id='content1'>content1</div></div></body></html>");

        bookViewer = new IBookViewer() {

            @Override
            public Widget asWidget() {
                return null;
            }

            @Override
            public void openBook(Book book, String progressCfi) {
            }

            @Override
            public PageEstimator getPageEstimator() {
                return pageEstimator;
            }

            @Override
            public Book getBook() {
                return book;
            }
        };

        pageEstimator = new PageEstimator(bookViewer);

        PageContentViewport.setAllViewportSizes(300, 450);

        RootPanel.get().add(pageEstimator);

        pageEstimator.getEstimatorFrame().show(new ItemPageLocation("chapter1", 0));
    }

    public void testCfiMarkerInjection() {

        injectCfiMarker("/4/2[test6]/2", 0, 0);
        injectCfiMarker("/4/2[test6]/4", 0, 4);

    }

    protected void injectCfiMarker(String cfiLocalPath, final int expectedLeft, final int expectedTop) {
        pageEstimator.injectCfiMarker(cfiLocalPath, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String result) {
                Document documnet = pageEstimator.getEstimatorFrame().getElement().<IFrameElement> cast().getContentDocument();
                Element marker = documnet.getElementById(result);
                assertEquals(expectedLeft, marker.getAbsoluteLeft());
                assertEquals(expectedTop, marker.getAbsoluteTop());
            }
        });
    }
}