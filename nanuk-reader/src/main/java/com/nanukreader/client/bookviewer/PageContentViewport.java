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
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class PageContentViewport extends Frame implements ProvidesResize, RequiresResize {

    private static final Logger logger = Logger.getLogger(PageContentViewport.class.getName());

    private static final List<PageContentViewport> viewports = new ArrayList<>();

    private int pageWidth;

    private final boolean mainPage;

    private BodyWrapper bodyWrapper;

    public PageContentViewport(boolean mainPage) {
        super("javascript:''");
        this.mainPage = mainPage;
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setProperty("border", "none");
        getElement().getStyle().setProperty("background", "#eee");
        viewports.add(this);

    }

    public final void show(String content) {
        show(content, 0);
    }

    public final void show(String content, int pageNumber) {
        IFrameElement element = getElement().<IFrameElement> cast();

        fillIframe(element, content);

        bodyWrapper = new BodyWrapper(element.getContentDocument().getBody());
        bodyWrapper.setPage(pageNumber);
    }

    void setViewportSize(int width, int height) {
        pageWidth = width;
        setPixelSize(mainPage ? (width * 2) + 10 : width, height);
        //  recalculateColumnWidth();
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

    @Override
    public void onResize() {
        if (bodyWrapper != null) {
            bodyWrapper.recalculateColumnWidth();
        }
    }

    class BodyWrapper extends Widget {

        public BodyWrapper(Element bodyElement) {
            setElement(bodyElement);

            getElement().getStyle().setProperty("height", "100%");
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("padding", "0px");
            getElement().getStyle().setProperty("margin", "0px");
            getElement().getStyle().setProperty("columnGap", "10px");
            getElement().getStyle().setProperty("WebkitColumnGap", "10px");
            getElement().getStyle().setProperty("MozColumnGap", "10px");

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
            getElement().getStyle().setProperty("transform", "translate(" + (-pageNumber * (pageWidth + 10)) + "px, 0)");
            getElement().getStyle().setProperty("WebkitTransform", "translate(" + (-pageNumber * (pageWidth + 10)) + "px, 0)");
        }

        @Override
        public void onAttach() {
            super.onAttach();
        }
    }
}
