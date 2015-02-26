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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nanukreader.client.AbstractCfiTest;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.ManifestItem;
import com.nanukreader.client.library.PackagingDescriptor;
import com.nanukreader.client.library.SpineItem;

public class PageEstimatorTest extends AbstractCfiTest {

    private Book book;

    private BookViewer bookViewer;

    private PageEstimator pageEstimator;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        PackagingDescriptor packagingDescriptor = PackagingDescriptor.create();

        JsArray<ManifestItem> manifestItems = JsArray.createArray().<JsArray<ManifestItem>> cast();

        JsArrayString properties = JsArrayString.createArray().cast();
        properties.push("nav");
        manifestItems.push(ManifestItem.create("navig", null, null, properties));
        packagingDescriptor.setManifestItems(manifestItems);

        manifestItems.push(ManifestItem.create("chapter1", null, null, null));

        JsArray<SpineItem> spineItems = JsArray.createArray().<JsArray<SpineItem>> cast();
        spineItems.push(SpineItem.create("chapter1"));
        packagingDescriptor.setSpineItems(spineItems);

        book = new Book(packagingDescriptor, null);
        book.addContentItem("chapter1",
                "<html><head></head><body><div id='chapter1'><div id='title1'>title1</div><div id='content1'>content1</div></div></body></html>");
        book.addContentItem("navig", "<html><head></head><body>navig</body></html>");

        bookViewer = new BookViewer();
        bookViewer.openBook(book, null);

        pageEstimator = bookViewer.getContentViewport().getPageEstimator();

        pageEstimator.show(new PageLocation("chapter1", 0));

    }

    public void testCfiMarkerInjection() {

        injectCfiMarker("/4/2[test6]/2", 0, 0);
        injectCfiMarker("/4/2[test6]/4", 0, 4);

    }

    void injectCfiMarker(String cfiLocalPath, final int expectedLeft, final int expectedTop) {
        pageEstimator.injectCfiMarker(cfiLocalPath, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String result) {
                Document documnet = pageEstimator.getIFrameElement().getContentDocument();
                Element marker = documnet.getElementById(result);
                assertEquals(expectedLeft, marker.getAbsoluteLeft());
                assertEquals(expectedTop, marker.getAbsoluteTop());
            }
        });
    }
}