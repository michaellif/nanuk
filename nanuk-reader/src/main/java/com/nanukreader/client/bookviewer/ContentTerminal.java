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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nanukreader.client.Callback;
import com.nanukreader.client.bookviewer.BookViewer.PageViewType;

// See http://pediapress.com/books/setup/1cafc1e1874fceec863d82f67f/ for spine background image - 2-page real book imitation 
public class ContentTerminal extends SimplePanel {

    private static final Logger logger = Logger.getLogger(ContentTerminal.class.getName());

    private static final String CONTENT_TERMINAL_CLASS_PREFIX = "ContentTerminal";

    public enum PageLayoutType {
        leftSide, rightSide, sideBySide;
    }

    private final Frame frame;

    private PageLayoutType pageLayoutType = PageLayoutType.leftSide;

    private int columnWidth;

    private BodyWrapper bodyWrapper;

    private PageLocation pageLocation;

    private final BookViewer bookViewer;

    private final int terminalNumber;

    private int zIndex;

    public ContentTerminal(BookViewer bookViewer, int terminalNumber) {
        super();

        this.bookViewer = bookViewer;
        this.terminalNumber = terminalNumber;

        frame = new Frame("javascript:''");
        frame.getElement().getStyle().setProperty("border", "none");
        frame.setSize("100%", "100%");
        setWidget(frame);

        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setProperty("boxSizing", "border-box");
        getElement().getStyle().setProperty("background", "#fff");

    }

    public final void show(final PageLocation pageLocation) {
        show(pageLocation, null);
    }

    final void show(final PageLocation pageLocation, final Callback<Void> callback) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "ContentTerminal show() called for terminalNumber=[" + terminalNumber + "] and pageLocation=["
                    + (pageLocation == null ? "NONE" : pageLocation) + "]");
        }
        if (pageLocation == null) {
            reset();
            if (callback != null) {
                callback.onCall(null);
            }
        } else if (this.pageLocation == pageLocation) {
            if (callback != null) {
                callback.onCall(null);
            }
        } else {
            bookViewer.getBook().getContentItem(pageLocation.getItemId(), new Callback<String>() {

                @Override
                public void onCall(String content) {
                    PageLocation previousPageLocation = ContentTerminal.this.pageLocation;
                    ContentTerminal.this.pageLocation = pageLocation;
                    if (previousPageLocation == null || (previousPageLocation.getItemId() != pageLocation.getItemId())) {
                        fillIframe(content);
                        updatePageCount();
                    }

                    //Go to page 
                    bodyWrapper.setPage(pageLocation.getPageNumber());
                    if (callback != null) {
                        callback.onCall(null);
                    }

                }
            });
        }
    }

    private int updatePageCount() {
        //Update PageEstimator with the latest page count. Count pages when item is in position 0. Translation is changing scroll offset. 
        BodyElement bodyElement = getIFrameElement().getContentDocument().getBody();
        int columnWidth = bookViewer.getContentViewport().getColumnWidth();
        int pageCount = (bodyElement.getScrollWidth() + bookViewer.getColumnGap()) / (columnWidth + bookViewer.getColumnGap());

        if ((bookViewer.getPageViewType() == PageViewType.sideBySide) && pageCount % 2 == 1) {
            pageCount += 1;
        }
        bookViewer.getContentViewport().getPageEstimator().updatePageCount(pageLocation.getItemId(), pageCount);
        return pageCount;
    }

    public void reset() {
        this.pageLocation = null;
        fillIframe("");
    }

    void setPageDimensions(PageLayoutType pageLayoutType, int columnWidth) {
        this.pageLayoutType = pageLayoutType;
        this.columnWidth = columnWidth;
        getElement().getStyle().setPaddingTop(bookViewer.getColumnGap() / 2, Unit.PX);
        getElement().getStyle().setPaddingBottom(bookViewer.getColumnGap() / 2, Unit.PX);

        if (bodyWrapper != null) {
            bodyWrapper.resetPageDimensions();
        }
    }

    private final void fillIframe(String content) {
        fillIframe(getIFrameElement(), content);
        bodyWrapper = new BodyWrapper(getIFrameElement().getContentDocument().getBody());
    }

    private final native void fillIframe(IFrameElement iframe, String content) /*-{
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.writeln(content);
		doc.close();
    }-*/;

    class BodyWrapper extends Widget {

        public BodyWrapper(Element bodyElement) {
            setElement(bodyElement);

            getElement().getStyle().setProperty("height", "100%");
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("padding", "0px");
            getElement().getStyle().setProperty("margin", "0px");
            getElement().getStyle().setProperty("columnGap", bookViewer.getColumnGap() + "px");
            getElement().getStyle().setProperty("WebkitColumnGap", bookViewer.getColumnGap() + "px");
            getElement().getStyle().setProperty("MozColumnGap", bookViewer.getColumnGap() + "px");
            //getElement().getStyle().setBackgroundColor("hsl(" + terminalNumber * 40 + ", 50%, 50%)");

            resetPageDimensions();

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

        private void resetPageDimensions() {
            switch (pageLayoutType) {
            case leftSide:
                getElement().getStyle().setProperty("width", columnWidth + "px");
                getElement().getStyle().setPaddingLeft(bookViewer.getColumnGap() / 2, Unit.PX);
                break;
            case rightSide:
                getElement().getStyle().setProperty("width", columnWidth + "px");
                getElement().getStyle().setPaddingLeft(bookViewer.getColumnGap() / 2 - 1, Unit.PX);
                break;
            case sideBySide:
                getElement().getStyle().setProperty("width", (columnWidth * 2 + bookViewer.getColumnGap()) + "px");
                getElement().getStyle().setPaddingLeft(bookViewer.getColumnGap() / 2, Unit.PX);
                break;

            }
            getElement().getStyle().setProperty("columnWidth", columnWidth + "px");
            getElement().getStyle().setProperty("WebkitColumnWidth", columnWidth + "px");
            getElement().getStyle().setProperty("MozColumnWidth", columnWidth + "px");
        }

        private void setPage(int pageNumber) {
            getElement().getStyle().setProperty("transform", "translate(" + (-pageNumber * (columnWidth + bookViewer.getColumnGap())) + "px, 0)");
            getElement().getStyle().setProperty("WebkitTransform", "translate(" + (-pageNumber * (columnWidth + bookViewer.getColumnGap())) + "px, 0)");
        }

        @Override
        public void onAttach() {
            super.onAttach();
        }
    }

    IFrameElement getIFrameElement() {
        return frame.getElement().<IFrameElement> cast();
    }

    protected BookViewer getBookViewer() {
        return bookViewer;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        getElement().getStyle().setZIndex(zIndex);
    }

    public int getZIndex() {
        return zIndex;
    }

    public Frame getFrame() {
        return frame;
    }
}
