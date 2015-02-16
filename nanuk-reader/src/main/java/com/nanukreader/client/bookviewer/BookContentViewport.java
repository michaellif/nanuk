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

import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;

public class BookContentViewport extends FlowPanel {

    private static final Logger logger = Logger.getLogger(BookContentViewport.class.getName());

    private final BookViewer bookViewer;

    private final PageContentViewport[] pageHolderArray;

    public BookContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        final Grid contentViewer = new Grid(1, 6);
        add(contentViewer);

        pageHolderArray = new PageContentViewport[6];
        for (int i = 0; i < pageHolderArray.length; i++) {
            pageHolderArray[i] = new PageContentViewport();
            contentViewer.setWidget(0, i, pageHolderArray[i]);
        }
    }

    void loadPageContent(final ItemPageLocation pageLocation, final int holderNumber) {
        bookViewer.getBook().getContentItem(pageLocation.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                throw new Error(caught);
            }

            @Override
            public void onSuccess(String content) {

                pageHolderArray[holderNumber].fillIframe(content);

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

}