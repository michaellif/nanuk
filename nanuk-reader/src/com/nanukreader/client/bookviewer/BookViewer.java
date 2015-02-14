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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.PackagingDescriptor;

public class BookViewer extends FlowPanel {

    private final BookContentViewport contentViewport;

    public BookViewer() {
        setHeight("450px");
        contentViewport = new BookContentViewport();
        add(contentViewport);
    }

    public void openBook(Book book) {

        final PackagingDescriptor descriptor = book.getPackagingDescriptor();

        for (int i = 0; i < Math.min(descriptor.getItemRefs().length(), 6); i++) {
            final int index = i;

            //logger.log(Level.SEVERE, "++++++++++++" + book.getContentItem(descriptor.getItemRefs().get(index).getIdref()));

            book.getContentItem(descriptor.getItemRefs().get(index).getIdref(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub
                    throw new Error(caught);
                }

                @Override
                public void onSuccess(String content) {

                    fillIframe(contentViewport.getPageHolder(index).getElement().<IFrameElement> cast(), content);

                    Document document = (contentViewport.getPageHolder(index).getElement().<FrameElement> cast()).getContentDocument();
                    document.getBody().getStyle().setProperty("columnWidth", "300px");
                    document.getBody().getStyle().setProperty("WebkitColumnWidth", "300px");
                    document.getBody().getStyle().setProperty("MozColumnWidth", "300px");
                    document.getBody().getStyle().setProperty("height", "400px");

                }
            });

        }

    }

    private static final native void fillIframe(IFrameElement iframe, String content) /*-{
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.writeln(content);
		doc.close();
    }-*/;
}
