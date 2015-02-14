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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.nanukreader.client.library.ItemPageLocation;
import com.nanukreader.client.loader.OcfBookLoader;

public class BookContentViewport extends ScrollPanel {

    private static final Logger logger = Logger.getLogger(BookContentViewport.class.getName());

    private final BookViewer bookViewer;

    private final Frame[] pageHolderArray;

    public BookContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        final HorizontalPanel contentViewer = new HorizontalPanel();
        contentViewer.setHeight("450px");
        setWidget(contentViewer);

        pageHolderArray = new Frame[6];
        for (int i = 0; i < pageHolderArray.length; i++) {
            pageHolderArray[i] = new Frame("javascript:''");
            pageHolderArray[i].setSize("300px", "450px");
            pageHolderArray[i].getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            contentViewer.add(pageHolderArray[i]);
        }
    }

    void loadPageContent(final ItemPageLocation pageLocation, final int holderNumber) {
        logger.log(Level.SEVERE, "++++++++++++TP1 " + pageLocation);
        bookViewer.getBook().getContentItem(pageLocation.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String content) {

                fillIframe(pageHolderArray[holderNumber].getElement().<IFrameElement> cast(), content);

                Document document = (pageHolderArray[holderNumber].getElement().<FrameElement> cast()).getContentDocument();
                document.getBody().getStyle().setProperty("columnWidth", "300px");
                document.getBody().getStyle().setProperty("WebkitColumnWidth", "300px");
                document.getBody().getStyle().setProperty("MozColumnWidth", "300px");
                document.getBody().getStyle().setProperty("height", "400px");

                if (holderNumber == 2 || holderNumber == 3 || holderNumber == 4) {
                    bookViewer.getNextPageLocation(pageLocation, new AsyncCallback<ItemPageLocation>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new Error(caught);
                        }

                        @Override
                        public void onSuccess(ItemPageLocation nextPageLocation) {
                            if (nextPageLocation != null) {
                                loadPageContent(nextPageLocation, holderNumber + 1);
                            }

                        }
                    });
                }

                if (holderNumber == 2 || holderNumber == 1) {
                    bookViewer.getPreviousPageLocation(pageLocation, new AsyncCallback<ItemPageLocation>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new Error(caught);
                        }

                        @Override
                        public void onSuccess(ItemPageLocation previousPageLocation) {
                            if (previousPageLocation != null) {
                                loadPageContent(previousPageLocation, holderNumber - 1);
                            }

                        }
                    });
                }

            }
        });

    }

    private static final native void fillIframe(IFrameElement iframe, String content) /*-{
		var doc = iframe.contentWindow.document;
		doc.open();
		doc.writeln(content);
		doc.close();
    }-*/;
}
