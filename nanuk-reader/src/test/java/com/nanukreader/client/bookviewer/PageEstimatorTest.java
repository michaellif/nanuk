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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nanukreader.client.AbstractCfiTest;
import com.nanukreader.client.library.Book;

public class PageEstimatorTest extends AbstractCfiTest {

    private Book book;

    private IBookViewer bookViewer;

    private PageEstimator pageEstimator;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        book = new Book(null, null);
        book.addContentItem("chapter1", "<html><head></head><body><div id='chapter1'><div id='22'>chapter1</div></div></body></html>");

        bookViewer = new IBookViewer() {

            @Override
            public Widget asWidget() {
                return null;
            }

            @Override
            public void openBook(Book book) {
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

        PageContentViewport.setViewportSize("300px", "450px");

        RootPanel.get().add(pageEstimator);

        bookViewer.getBook().getContentItem("chapter1", new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String content) {
                pageEstimator.getEstimatorFrame().fillIframe(content);
            }

        });
    }

    public void testCfiMarkerInjection() {

        pageEstimator.injectCfiMarker("/4/2[test6]/2", new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String result) {
                assertEquals("gwt-uid-", result.substring(0, 8));
                final IFrameElement element = pageEstimator.getEstimatorFrame().getElement().<IFrameElement> cast();
                final Element html = element.getContentDocument().getBody().getParentElement();
                assertTrue(html.toString().contains("data-nanuk-cfimarker"));
            }
        });

    }
}