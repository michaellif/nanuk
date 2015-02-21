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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class PageContentViewport extends Frame {

    private static final Logger logger = Logger.getLogger(PageContentViewport.class.getName());

    private static final List<PageContentViewport> viewports = new ArrayList<>();

    private static final int COLUMN_GAP = 10;

    public enum PageViewType {

        //TODO consider dualPage (see rendition:page-spread-center in EPUB3 spec)

        onePageView, twoPageView;

    }

    private int pageWidth;

    private final PageViewType pageViewType;

    private BodyWrapper bodyWrapper;

    private ItemPageLocation pageLocation;

    private final IBookViewer bookViewer;

    private final int viewportNumber;

    public PageContentViewport(IBookViewer bookViewer, int viewportNumber) {
        super("javascript:''");
        this.bookViewer = bookViewer;
        this.viewportNumber = viewportNumber;
        //TODO hardcoded pageViewType for now
        pageViewType = PageViewType.twoPageView;
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setProperty("border", "none");
        getElement().getStyle().setProperty("background", "#eee");
        viewports.add(this);

    }

    public final void show(final ItemPageLocation pageLocation) {
        show(pageLocation, null);
    }

    final void show(final ItemPageLocation pageLocation, final AsyncCallback<Void> callback) {
        assert pageLocation != null;

        logger.log(Level.SEVERE, "+++++++++++++ show " + viewportNumber + " - " + pageLocation.getItemId() + " - " + pageLocation.getPageNumber());

        bookViewer.getBook().getContentItem(pageLocation.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String content) {
                ItemPageLocation previousPageLocation = PageContentViewport.this.pageLocation;
                PageContentViewport.this.pageLocation = pageLocation;
                if (previousPageLocation == null || (previousPageLocation.getItemId() != pageLocation.getItemId())) {
                    IFrameElement element = getElement().<IFrameElement> cast();
                    fillIframe(element, content);
                    bodyWrapper = new BodyWrapper(element.getContentDocument().getBody());
                    updatePageCount();
                }

                //Go to page 
                bodyWrapper.setPage(pageLocation.getPageNumber());
                if (callback != null) {
                    callback.onSuccess(null);
                }

            }
        });

    }

    private int updatePageCount() {
        //Update PageEstimator with the latest page count. Count pages when item is in position 0. Translation is changing scroll offset. 
        BodyElement bodyElement = getIFrameElement().getContentDocument().getBody();
        int pageCount = bodyElement.getScrollWidth()
                / ((viewportNumber == 2 && pageViewType == PageViewType.twoPageView) ? (getOffsetWidth() - COLUMN_GAP) / 2 : getOffsetWidth());

        if (pageViewType == PageViewType.twoPageView && pageCount % 2 == 1) {
            pageCount += 1;
        }
        bookViewer.getPageEstimator().updatePageCount(pageLocation.getItemId(), pageCount);
        return pageCount;
    }

    public void clearView() {
        this.pageLocation = null;
        IFrameElement element = getElement().<IFrameElement> cast();
        fillIframe(element, "");
    }

    void setViewportSize(int width, int height) {
        pageWidth = width;
        setPixelSize((viewportNumber == 2 && pageViewType == PageViewType.twoPageView) ? (width * 2) + COLUMN_GAP : width, height);
        if (bodyWrapper != null) {
            bodyWrapper.recalculateColumnWidth();
        }
    }

    private final native void fillIframe(IFrameElement iframe, String content) /*-{
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.writeln(content);
		doc.close();
    }-*/;

    public static void setAllViewportSizes(int width, int height) {
        for (PageContentViewport pageContentViewport : viewports) {
            pageContentViewport.setViewportSize(width, height);
        }
    }

    class BodyWrapper extends Widget {

        public BodyWrapper(Element bodyElement) {
            setElement(bodyElement);

            getElement().getStyle().setProperty("height", "100%");
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("padding", "0px");
            getElement().getStyle().setProperty("margin", "0px");
            getElement().getStyle().setProperty("columnGap", COLUMN_GAP + "px");
            getElement().getStyle().setProperty("WebkitColumnGap", COLUMN_GAP + "px");
            getElement().getStyle().setProperty("MozColumnGap", COLUMN_GAP + "px");

            recalculateColumnWidth();

            //Frame onAttach should be called manually!
            onAttach();

            addDomHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    NativeEvent nEvent = event.getNativeEvent();
                    if (Arrays.asList(32, 37, 38, 39, 40).contains(nEvent.getKeyCode())) {
                        nEvent.preventDefault();
                    }
                }
            }, KeyDownEvent.getType());
        }

        private void recalculateColumnWidth() {
            getElement().getStyle().setProperty("columnWidth", pageWidth + "px");
            getElement().getStyle().setProperty("WebkitColumnWidth", pageWidth + "px");
            getElement().getStyle().setProperty("MozColumnWidth", pageWidth + "px");
        }

        private void setPage(int pageNumber) {
            getElement().getStyle().setProperty("transform", "translate(" + (-pageNumber * (pageWidth + COLUMN_GAP)) + "px, 0)");
            getElement().getStyle().setProperty("WebkitTransform", "translate(" + (-pageNumber * (pageWidth + COLUMN_GAP)) + "px, 0)");
        }

        @Override
        public void onAttach() {
            super.onAttach();
        }
    }

    IFrameElement getIFrameElement() {
        return getElement().<IFrameElement> cast();
    }
}
