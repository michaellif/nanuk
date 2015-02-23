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
import com.nanukreader.client.bookviewer.BookViewer.PageViewType;

class BookContentViewport extends FlowPanel {

    private static final Logger logger = Logger.getLogger(BookContentViewport.class.getName());

    private final BookViewer bookViewer;

    private final PageContentViewport[] viewportArray;

    public BookContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        final Grid contentViewer = new Grid(1, 7);
        add(contentViewer);

        viewportArray = new PageContentViewport[7];
        for (int i = 0; i < viewportArray.length; i++) {
            viewportArray[i] = new PageContentViewport(bookViewer, i);
            contentViewer.setWidget(0, i, viewportArray[i]);
        }
    }

    void showPage(final PageLocation pageLocation, final int viewportNumber) {

//        logger.log(Level.SEVERE, "+++++++++++++ loadPageContent " + viewportNumber + " - "
//                + (pageLocation == null ? "NONE" : pageLocation.getItemId() + " - " + pageLocation.getPageNumber()));

        viewportArray[viewportNumber].show(pageLocation);

        if (viewportNumber == 3 || viewportNumber == 4 || viewportNumber == 5) {
            bookViewer.getPageEstimator().getNextPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new Error(caught);
                }

                @Override
                public void onSuccess(PageLocation nextPageLocation) {
                    showPage(nextPageLocation, viewportNumber + 1);
                }
            });
        }

        if (viewportNumber == 3 || viewportNumber == 2 || viewportNumber == 1) {
            bookViewer.getPageEstimator().getPreviousPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new Error(caught);
                }

                @Override
                public void onSuccess(PageLocation previousPageLocation) {
                    showPage(previousPageLocation, viewportNumber - 1);
                }
            });
        }

    }

    public void clearView() {
        for (PageContentViewport viewport : viewportArray) {
            viewport.clearViewport();
        }

    }

    public boolean isSideBySide() {
        //TODO implement PageViewType.auto option
        return bookViewer.getPageViewType() == PageViewType.sideBySide;
    }

}
