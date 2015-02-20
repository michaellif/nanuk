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
import com.google.gwt.user.client.ui.SimplePanel;
import com.nanukreader.client.cfi.CfiContentHandler;
import com.nanukreader.client.cfi.CfiParser;

public class PageEstimator extends SimplePanel {

    private static final Logger logger = Logger.getLogger(PageEstimator.class.getName());

    private final IBookViewer bookViewer;

    private final PageContentViewport estimatorContentViewport;

    /**
     * key - itemId, value totalPageNumber in item
     */
    private final Map<String, Integer> pageCountCache;

    public PageEstimator(IBookViewer bookViewer) {
        this.bookViewer = bookViewer;
        pageCountCache = new HashMap<>();

        estimatorContentViewport = new PageContentViewport(bookViewer, false);
        add(estimatorContentViewport);
    }

    //TODO call that method when Viewport size is changed
    void invalidate() {
        pageCountCache.clear();
    }

    public void getPreviousPageLocation(ItemPageLocation pageLocation, final AsyncCallback<ItemPageLocation> callback) {
        if (pageLocation.getPageNumber() > 0) {
            callback.onSuccess(new ItemPageLocation(pageLocation.getItemId(), pageLocation.getPageNumber() - 1));
        } else {
            final String previousItemId = bookViewer.getBook().getPreviousSpineItemId(pageLocation.getItemId());
            if (previousItemId == null) {
                callback.onSuccess(null);
            } else {
                getPageCount(previousItemId, new AsyncCallback<Integer>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Integer pageCount) {
                        callback.onSuccess(new ItemPageLocation(previousItemId, pageCount - 1));
                    }
                });
            }
        }
    }

    /**
     * Return ItemPageLocation pointing to next page or null if this is the last page of book
     * 
     * @param pageLocation
     * @param callback
     */
    public void getNextPageLocation(final ItemPageLocation pageLocation, final AsyncCallback<ItemPageLocation> callback) {
        getPageCount(pageLocation.getItemId(), new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Integer pageCount) {
                if (pageLocation.getPageNumber() < pageCount - 1) {
                    callback.onSuccess(new ItemPageLocation(pageLocation.getItemId(), pageLocation.getPageNumber() + 1));
                } else {
                    final String nextItemId = bookViewer.getBook().getPreviousSpineItemId(pageLocation.getItemId());
                    if (nextItemId == null) {
                        callback.onSuccess(null);
                    } else {
                        callback.onSuccess(new ItemPageLocation(nextItemId, 0));
                    }
                }
            }
        });

    }

    public void getPageLocation(final String cfi, final AsyncCallback<ItemPageLocation> callback) {
        final String itemId = CfiParser.getItemIdFromCfi(cfi);
        getPageLocation(itemId, CfiParser.getLocalPathFromCfi(cfi), callback);
    }

    private void getPageLocation(final String itemId, final String cfiLocalPath, final AsyncCallback<ItemPageLocation> callback) {
        estimatorContentViewport.show(new ItemPageLocation(itemId, 0), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {

                injectCfiMarker(cfiLocalPath, new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(String elementId) {
                        Element cfiMarker = estimatorContentViewport.getIFrameElement().getContentDocument().getElementById(elementId);
                        int pageNumber = cfiMarker.getOffsetLeft() / estimatorContentViewport.getOffsetWidth();

                        //  logger.log(Level.SEVERE, "+++++++++++++++pageNumber " + pageNumber);

                        callback.onSuccess(new ItemPageLocation(itemId, pageNumber));
                    }
                });

            }
        });
    }

    void getPageCount(final String itemId, final AsyncCallback<Integer> callback) {
        if (pageCountCache.containsKey(itemId)) {
            callback.onSuccess(pageCountCache.get(itemId));
            return;
        }
        estimatorContentViewport.show(new ItemPageLocation(itemId, 0), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(pageCountCache.get(itemId));
            }
        });
    }

    void injectCfiMarker(final String cfiLocalPath, final AsyncCallback<String> callback) {
        final IFrameElement element = estimatorContentViewport.getElement().<IFrameElement> cast();
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

    private void getPageStartCfi(final ItemPageLocation pageLocation, final AsyncCallback<String> callback) {
        bookViewer.getBook().getContentItem(pageLocation.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String content) {

                estimatorContentViewport.show(pageLocation);
                IFrameElement element = estimatorContentViewport.getElement().<IFrameElement> cast();
                BodyElement document = element.getContentDocument().getBody();

                //TODO implement
            }

        });
    }

    PageContentViewport getEstimatorFrame() {
        return estimatorContentViewport;
    }

    void updatePageCount(String itemId, int pageCount) {
        pageCountCache.put(itemId, pageCount);
    }

}
