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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nanukreader.client.bookviewer.BookViewer.PageOrientation;
import com.nanukreader.client.bookviewer.BookViewer.PageTurnEffectType;
import com.nanukreader.client.bookviewer.BookViewer.PageViewType;
import com.nanukreader.client.bookviewer.PageContentViewport.PageLayoutType;

class BookContentViewport extends FlowPanel {

    private static final Logger logger = Logger.getLogger(BookContentViewport.class.getName());

    private static final int COLUMN_GAP = 10;

    private final BookViewer bookViewer;

    private final PageEstimator pageEstimator;

    private final PageContentViewport[] viewportArray;

    private final boolean spreadEnabled = true;

    public BookContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        getElement().getStyle().setPosition(Position.RELATIVE);

        pageEstimator = new PageEstimator(bookViewer);
        pageEstimator.setSize("50%", "100%");
        pageEstimator.getElement().getStyle().setLeft(0, Unit.PX);
        add(pageEstimator);

        viewportArray = new PageContentViewport[7];
        for (int i = 0; i < viewportArray.length; i++) {
            viewportArray[i] = new PageContentViewport(bookViewer, i);
            viewportArray[i].getElement().getStyle().setPosition(Position.ABSOLUTE);
            viewportArray[i].getElement().getStyle().setTop(0, Unit.PX);
            switch (i) {
            case 0:
            case 1:
            case 2:
                viewportArray[i].setSize("50%", "100%");
                viewportArray[i].getElement().getStyle().setLeft(0, Unit.PX);
                break;
            case 3:
                viewportArray[i].setSize("100%", "100%");
                viewportArray[i].getElement().getStyle().setLeft(0, Unit.PX);
                break;
            case 4:
            case 5:
            case 6:
                viewportArray[i].setSize("50%", "100%");
                viewportArray[i].getElement().getStyle().setRight(0, Unit.PX);
                break;
            default:
                break;
            }
            add(viewportArray[i]);

        }

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        int columnWidth = (int) Math.floor((getOffsetWidth() - getColumnGap()) / 2) - 1;
        for (int i = 0; i < viewportArray.length; i++) {
            switch (i) {
            case 0:
            case 1:
            case 2:
                viewportArray[i].setPageDimensions(PageLayoutType.leftSide, columnWidth);
                break;
            case 3:
                viewportArray[i].setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 4:
            case 5:
            case 6:
                viewportArray[i].setPageDimensions(PageLayoutType.rightSide, columnWidth);
                break;
            default:
                break;
            }
            add(viewportArray[i]);

        }
        pageEstimator.setPageDimensions(PageLayoutType.leftSide, columnWidth);
        spread();
    }

    void showPage(final PageLocation pageLocation) {
        showPage(pageLocation, 3);
    }

    private void spread() {
        if (spreadEnabled) {
            for (int i = 0; i < viewportArray.length; i++) {
                switch (i) {
                case 0:
                case 1:
                case 2:
                    viewportArray[i].getElement().getStyle().setLeft((getOffsetWidth() + 10) * (i - 3) / 2, Unit.PX);
                    break;
                case 4:
                case 5:
                case 6:
                    viewportArray[i].getElement().getStyle().setRight((getOffsetWidth() + 10) * (3 - i) / 2, Unit.PX);
                    break;
                default:
                    break;
                }
            }
        }
    }

    private void showPage(final PageLocation pageLocation, final int viewportNumber) {

//        logger.log(Level.SEVERE, "+++++++++++++ loadPageContent " + viewportNumber + " - "
//                + (pageLocation == null ? "NONE" : pageLocation.getItemId() + " - " + pageLocation.getPageNumber()));

        viewportArray[viewportNumber].show(pageLocation);

        if (viewportNumber == 3 || viewportNumber == 4 || viewportNumber == 5) {
            pageEstimator.getNextPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

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
            pageEstimator.getPreviousPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

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

    public void clearPageContentViewports() {
        for (PageContentViewport viewport : viewportArray) {
            viewport.clearViewport();
        }
    }

    PageViewType getPageViewType() {
        return bookViewer.getUserPreferences().getPageViewType();
    }

    PageTurnEffectType getPageTurnEffectType() {
        return bookViewer.getUserPreferences().getPageTurnEffectType();
    }

    PageOrientation getPageOrientation() {
        return bookViewer.getUserPreferences().getPageOrientation();
    }

    public PageEstimator getPageEstimator() {
        return pageEstimator;
    }

    public int getColumnGap() {
        return COLUMN_GAP;
    }
}
