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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class PageContentViewport extends Frame implements ProvidesResize, RequiresResize {

    private static final List<PageContentViewport> viewports = new ArrayList<>();

    public PageContentViewport() {
        super("javascript:''");
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setProperty("border", "none");
        viewports.add(this);
    }

    public final void fillIframe(String content) {
        IFrameElement element = getElement().<IFrameElement> cast();

        fillIframe(element, content);

        Document document = element.getContentDocument();
        document.getBody().getStyle().setProperty("height", "100%");
        document.getBody().getStyle().setProperty("overflow", "hidden");
        recalculateColumnWidth();
    }

    public static void setViewportSize(String width, String height) {
        for (PageContentViewport pageContentViewport : viewports) {
            pageContentViewport.setSize(width, height);
        }
    }

    private final native void fillIframe(IFrameElement iframe, String content) /*-{
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.writeln(content);
		doc.close();
    }-*/;

    @Override
    public void onResize() {
        recalculateColumnWidth();
    }

    private void recalculateColumnWidth() {
        IFrameElement element = getElement().<IFrameElement> cast();
        Document document = element.getContentDocument();
        document.getBody().getStyle().setProperty("columnWidth", getOffsetWidth() + "px");
        document.getBody().getStyle().setProperty("WebkitColumnWidth", getOffsetWidth() + "px");
        document.getBody().getStyle().setProperty("MozColumnWidth", getOffsetWidth() + "px");
    }
}
