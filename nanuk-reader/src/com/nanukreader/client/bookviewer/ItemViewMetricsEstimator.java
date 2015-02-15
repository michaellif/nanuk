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
package com.nanukreader.client.bookviewer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.nanukreader.client.cfi.CfiContentHandler;
import com.nanukreader.client.cfi.CfiParser;

class ItemViewMetricsEstimator extends SimplePanel {

    private static final Logger logger = Logger.getLogger(ItemViewMetricsEstimator.class.getName());

    private final BookViewer bookViewer;

    private final PageContentViewport estimatorFrame;

    /**
     * key - itemId, value totalPageNumber in item
     */
    private final Map<String, Integer> totalPageNumberCache;

    ItemViewMetricsEstimator(BookViewer bookViewer) {
        this.bookViewer = bookViewer;
        totalPageNumberCache = new HashMap<>();

        estimatorFrame = new PageContentViewport();
        add(estimatorFrame);
    }

    void invalidate() {
        totalPageNumberCache.clear();
    }

    void getPreviousPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_003", 1));
    }

    void getNextPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_005", 1));
    }

    void getPageLocation(final String cfi, final AsyncCallback<ItemPageLocation> callback) {

        final String itemId = CfiParser.getItemIdFromCfi(cfi);

        bookViewer.getBook().getContentItem(itemId, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String content) {

                estimatorFrame.fillIframe(content);
                IFrameElement element = estimatorFrame.getElement().<IFrameElement> cast();
                final BodyElement document = element.getContentDocument().getBody();

                new CfiParser(new CfiContentHandler() {
                    Element currentElement = null;

                    Text currentText = null;

                    @Override
                    public void step(int number, String assertion) {
                        if (currentElement == null) {
                            if (number == 4) {
                                currentElement = document;
                            } else {
                                throw new Error("First position of local_path should be a body tag which place is '4' .");
                            }
                        } else if (number % 2 == 0) {
                            currentElement = currentElement.getChild(number - 1).cast();
                        } else {
                            currentText = currentElement.getChild(number - 1).cast();
                        }
                    }

                    @Override
                    public void complete() {

                        //TODO handle text with offset by wrapping char on that position in div and measuring it's location

                        if (currentElement != null) {
                            currentElement.getStyle().setBackgroundColor("red");

                            int pageNumber = currentElement.getOffsetLeft() / estimatorFrame.getOffsetWidth();
                            callback.onSuccess(new ItemPageLocation(itemId, pageNumber));
                        }
                    }
                }, null).parse(cfi);

            }

        });
    }

    void getPageStartCfi(final ItemPageLocation location, final AsyncCallback<String> callback) {
        bookViewer.getBook().getContentItem(location.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String content) {

                estimatorFrame.fillIframe(content);
                IFrameElement element = estimatorFrame.getElement().<IFrameElement> cast();
                BodyElement document = element.getContentDocument().getBody();

                //TODO implement
            }

        });
    }

}
