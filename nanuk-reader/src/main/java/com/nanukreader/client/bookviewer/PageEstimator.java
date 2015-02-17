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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.nanukreader.client.cfi.CfiContentHandler;
import com.nanukreader.client.cfi.CfiParser;

public class PageEstimator extends SimplePanel {

    private static final Logger logger = Logger.getLogger(PageEstimator.class.getName());

    private static final String CFI_MARKER_ATTR = "data-nanuk-cfimarker";

    private final IBookViewer bookViewer;

    private final PageContentViewport estimatorFrame;

    /**
     * key - itemId, value totalPageNumber in item
     */
    private final Map<String, Integer> totalPageNumberCache;

    public PageEstimator(IBookViewer bookViewer) {
        this.bookViewer = bookViewer;
        totalPageNumberCache = new HashMap<>();

        estimatorFrame = new PageContentViewport();
        add(estimatorFrame);
    }

    void invalidate() {
        totalPageNumberCache.clear();
    }

    public void getPreviousPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_003", 1));
    }

    public void getNextPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_005", 1));
    }

    public void getPageLocation(final String cfi, final AsyncCallback<ItemPageLocation> callback) {

        final String itemId = CfiParser.getItemIdFromCfi(cfi);

        bookViewer.getBook().getContentItem(itemId, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String content) {
                getPageLocation(itemId, CfiParser.getLocalPathFromCfi(cfi), content, callback);
            }

        });
    }

    void getPageLocation(final String itemId, final String cfiLocalPath, String content, final AsyncCallback<ItemPageLocation> callback) {
        estimatorFrame.fillIframe(content);
        final IFrameElement element = estimatorFrame.getElement().<IFrameElement> cast();

        injectCfiMarker(cfiLocalPath, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String elementId) {
                Element cfiMarker = element.getContentDocument().getElementById(elementId);
                int pageNumber = cfiMarker.getOffsetLeft() / estimatorFrame.getOffsetWidth();
                callback.onSuccess(new ItemPageLocation(itemId, pageNumber));
            }
        });

    }

    void injectCfiMarker(final String cfiLocalPath, final AsyncCallback<String> callback) {
        final IFrameElement element = estimatorFrame.getElement().<IFrameElement> cast();
        final Element html = element.getContentDocument().getBody().getParentElement();

        new CfiParser(new CfiContentHandler() {
            Node currentNode = html;

            @Override
            public void step(int number, String assertion) {
                int counter = 0;
                Node child = currentNode.getFirstChild();
                while (child != null) {
                    counter++;
                    if (Element.is(child)) {
                        if (isCfiMarker(child.<Element> cast())) {
                            child = child.getNextSibling();
                            break;
                        }
                        if (counter % 2 != 0) {
                            counter++;
                        }
                    }
                    if (counter == number) {
                        currentNode = child;
                        return;
                    } else {
                        child = child.getNextSibling();
                    }
                }

                throw new Error("No such node");
            }

            private boolean isCfiMarker(Element child) {
                return child.getAttribute(CFI_MARKER_ATTR) == "true";
            }

            @Override
            public void complete() {
                Element marker = Document.get().createDivElement();
                marker.getStyle().setWidth(10, Unit.PX);
                marker.getStyle().setHeight(10, Unit.PX);
                marker.getStyle().setBackgroundColor("green");
                String markerId = Document.get().createUniqueId();
                marker.setAttribute("id", markerId);
                marker.setAttribute(CFI_MARKER_ATTR, "true");
                if (Element.is(currentNode)) {
                    currentNode.insertFirst(marker);
                } else {
                    currentNode.insertFirst(marker);
                }

                callback.onSuccess(markerId);
            }
        }, null).parse(cfiLocalPath);
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

    PageContentViewport getEstimatorFrame() {
        return estimatorFrame;
    }
}
