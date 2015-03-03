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
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nanukreader.client.Callback;
import com.nanukreader.client.bookviewer.BookViewer.PageViewType;
import com.nanukreader.client.cfi.CfiContentHandler;
import com.nanukreader.client.cfi.CfiParser;

public class PageEstimator extends ContentTerminal {

    private static final Logger logger = Logger.getLogger(PageEstimator.class.getName());

    /**
     * key - itemId, value totalPageNumber in item
     */
    private final Map<String, Integer> pageCountCache;

    public PageEstimator(BookViewer bookViewer) {
        super(bookViewer, -1);
        pageCountCache = new HashMap<>();
    }

    //TODO call that method when Viewport size is changed
    void invalidate() {
        pageCountCache.clear();
    }

    public void getPageLocation(final String cfi, final AsyncCallback<PageLocation> callback) {
        final String itemId = CfiParser.getItemIdFromCfi(cfi);
        if (itemId == null) { // Return first page of first Spine Item
            callback.onSuccess(null);
            return;
        }
        getPageLocation(itemId, CfiParser.getLocalPathFromCfi(cfi), callback);
    }

    private void getPageLocation(final String itemId, final String cfiLocalPath, final AsyncCallback<PageLocation> callback) {
        show(new PageLocation(getBookViewer().getBook(), itemId, 0), new Callback<Void>() {

            @Override
            public void onCall(Void result) {

                injectCfiMarker(cfiLocalPath, new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(String elementId) {
                        Element cfiMarker = getIFrameElement().getContentDocument().getElementById(elementId);
                        int pageNumber = cfiMarker.getOffsetLeft() / getOffsetWidth();
                        callback.onSuccess(new PageLocation(getBookViewer().getBook(), itemId, pageNumber));
                    }
                });

            }
        });
    }

    void getPageCount(final String itemId, final Callback<Integer> callback) {
        if (pageCountCache.containsKey(itemId)) {
            callback.onCall(pageCountCache.get(itemId));
            return;
        }
        show(new PageLocation(getBookViewer().getBook(), itemId, 0), new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                callback.onCall(pageCountCache.get(itemId));
            }
        });
    }

    private void getPageStartCfi(final PageLocation pageLocation, final AsyncCallback<String> callback) {
        getBookViewer().getBook().getContentItem(pageLocation.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String content) {

                show(pageLocation);
                IFrameElement element = getElement().<IFrameElement> cast();
                BodyElement document = element.getContentDocument().getBody();

                //TODO implement
            }

        });
    }

    void updatePageCount(String itemId, int pageCount) {
        pageCountCache.put(itemId, pageCount);
    }

    void getPreviousPageLocation(PageLocation pageLocation, final Callback<PageLocation> callback) {
        if (pageLocation == null) {
            callback.onCall(null);
        } else if (pageLocation.getPageNumber() > 1 && getBookViewer().getPageViewType() == PageViewType.sideBySide && pageLocation.getPageNumber() % 2 == 0) {
            callback.onCall(new PageLocation(getBookViewer().getBook(), pageLocation.getItemId(), pageLocation.getPageNumber() - 2));
        } else if (pageLocation.getPageNumber() > 0) {
            callback.onCall(new PageLocation(getBookViewer().getBook(), pageLocation.getItemId(), pageLocation.getPageNumber() - 1));
        } else {
            final String previousItemId = getBookViewer().getBook().getPreviousSpineItemId(pageLocation.getItemId());
            if (previousItemId == null) {
                callback.onCall(null);
            } else {
                getPageCount(previousItemId, new Callback<Integer>() {

                    @Override
                    public void onCall(Integer pageCount) {
                        callback.onCall(new PageLocation(getBookViewer().getBook(), previousItemId, pageCount - 1));
                    }
                });
            }
        }
    }

    /**
     * Return ItemPageLocation pointing to next page or null if this is the last page of book.
     * In case of Page View Type is sideBySide, the next page would be the nearest odd numbered page, following the current one.
     * 
     * @param pageLocation
     * @param callback
     */
    void getNextPageLocation(final PageLocation pageLocation, final Callback<PageLocation> callback) {
        if (pageLocation == null) {
            callback.onCall(null);
        } else {
            getPageCount(pageLocation.getItemId(), new Callback<Integer>() {

                @Override
                public void onCall(Integer pageCount) {
                    if ((pageLocation.getPageNumber() < pageCount - 2) && getBookViewer().getPageViewType() == PageViewType.sideBySide) {
                        callback.onCall(new PageLocation(getBookViewer().getBook(), pageLocation.getItemId(), pageLocation.getPageNumber() + 2
                                - pageLocation.getPageNumber() % 2));
                    } else if ((pageLocation.getPageNumber() < pageCount - 1) && getBookViewer().getPageViewType() == PageViewType.single) {
                        callback.onCall(new PageLocation(getBookViewer().getBook(), pageLocation.getItemId(), pageLocation.getPageNumber() + 1));
                    } else {
                        final String nextItemId = getBookViewer().getBook().getNextSpineItemId(pageLocation.getItemId());
                        if (nextItemId == null) {
                            callback.onCall(null);
                        } else {
                            callback.onCall(new PageLocation(getBookViewer().getBook(), nextItemId, 0));
                        }
                    }
                }
            });
        }
    }

    void injectCfiMarker(final String cfiLocalPath, final AsyncCallback<String> callback) {
        final Element html = getIFrameElement().getContentDocument().getBody().getParentElement();

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
                throw new Error("Failed to inject CFI marker for cfiLocalPath [" + cfiLocalPath + "]");
            }

            private boolean isCfiMarker(Element child) {
                return child.getAttribute(CfiMarker.CFI_MARKER_ATTR) == "true";
            }

            @Override
            public void complete() {
                CfiMarker marker = new CfiMarker();

                if (Element.is(currentNode)) {
                    currentNode.insertFirst(marker.getElement());
                } else {
                    currentNode.insertFirst(marker.getElement());
                }

                callback.onSuccess(marker.getId());
            }
        }, null).parse(cfiLocalPath);
    }
}
