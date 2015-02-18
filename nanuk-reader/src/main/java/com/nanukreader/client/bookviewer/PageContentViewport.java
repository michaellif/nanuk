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
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class PageContentViewport extends Frame implements ProvidesResize, RequiresResize {

    private static final Logger logger = Logger.getLogger(PageContentViewport.class.getName());

    private static final List<PageContentViewport> viewports = new ArrayList<>();

    private int pageWidth;

    private final boolean mainPage;

    public PageContentViewport(boolean mainPage) {
        super("javascript:''");
        this.mainPage = mainPage;
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setProperty("border", "none");
        getElement().getStyle().setProperty("background", "#aaa");
        viewports.add(this);
    }

    public final void show(String content) {
        show(content, null);
    }

    public final void show(String content, Integer pageNumber) {
        IFrameElement element = getElement().<IFrameElement> cast();

        fillIframe(element, content);

        Document document = element.getContentDocument();
        document.getBody().getStyle().setProperty("height", "100%");
        document.getBody().getStyle().setProperty("overflow", "hidden");
        document.getBody().getStyle().setProperty("padding", "0px");
        document.getBody().getStyle().setProperty("margin", "0px");
        document.getBody().getStyle().setProperty("columnGap", "10px");
        document.getBody().getStyle().setProperty("WebkitColumnGap", "10px");
        document.getBody().getStyle().setProperty("MozColumnGap", "10px");

        recalculateColumnWidth();

        // Don't set scroll position on Estimator - it breaks page calculation
        if (pageNumber != null) {
            // document.getBody().setScrollLeft(pageNumber * (pageWidth + 10) - 2);
            document.getBody().getStyle().setProperty("transform", "translate(" + (-pageNumber * (pageWidth + 10)) + "px, 0)");
            document.getBody().getStyle().setProperty("WebkitTransform", "translate(" + (-pageNumber * (pageWidth + 10)) + "px, 0)");
        }
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
        recalculateColumnWidth();
    }

    private void recalculateColumnWidth() {
        IFrameElement element = getElement().<IFrameElement> cast();
        Document document = element.getContentDocument();
        document.getBody().getStyle().setProperty("columnWidth", pageWidth + "px");
        document.getBody().getStyle().setProperty("WebkitColumnWidth", pageWidth + "px");
        document.getBody().getStyle().setProperty("MozColumnWidth", pageWidth + "px");
    }
}
