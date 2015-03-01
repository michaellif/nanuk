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
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.nanukreader.client.bookviewer.ContentTerminal.PageLayoutType;

class ContentViewport extends FlowPanel implements ProvidesResize, RequiresResize {

    private static final Logger logger = Logger.getLogger(ContentViewport.class.getName());

    private static final int COLUMN_GAP = 10;

    private final BookViewer bookViewer;

    private final PageEstimator pageEstimator;

    private final ContentTerminal[] terminalArray;

    private final boolean spreadEnabled = true;

    public ContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        getElement().getStyle().setPosition(Position.RELATIVE);

        pageEstimator = new PageEstimator(bookViewer);
        add(pageEstimator);

        terminalArray = new ContentTerminal[7];
        for (int i = 0; i < terminalArray.length; i++) {
            terminalArray[i] = new ContentTerminal(bookViewer, i);
            add(terminalArray[i]);
        }
    }

    @Override
    protected void onAttach() {
        layout();
        super.onAttach();
    }

    protected void layout() {
        int columnWidth = (int) Math.floor((getOffsetWidth() - getColumnGap()) / 2) - 1;
        for (int i = 0; i < terminalArray.length; i++) {
            terminalArray[i].getElement().getStyle().setPosition(Position.ABSOLUTE);
            terminalArray[i].getElement().getStyle().setTop(0, Unit.PX);
            switch (i) {
            case 0:
            case 1:
            case 2:
                terminalArray[i].setSize("50%", "100%");
                terminalArray[i].getElement().getStyle().setLeft(0, Unit.PX);
                terminalArray[i].setPageDimensions(PageLayoutType.leftSide, columnWidth);
                break;
            case 3:
                terminalArray[i].setSize("100%", "100%");
                terminalArray[i].getElement().getStyle().setLeft(0, Unit.PX);
                terminalArray[i].setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 4:
            case 5:
            case 6:
                terminalArray[i].setSize("50%", "100%");
                terminalArray[i].getElement().getStyle().setRight(0, Unit.PX);
                terminalArray[i].setPageDimensions(PageLayoutType.rightSide, columnWidth);
                break;
            default:
                break;
            }
            add(terminalArray[i]);

        }
        pageEstimator.setPageDimensions(PageLayoutType.leftSide, columnWidth);
        pageEstimator.setSize("50%", "100%");
        pageEstimator.getElement().getStyle().setLeft(0, Unit.PX);

        spread();
    }

    void showPage(final PageLocation pageLocation) {
        showPage(pageLocation, 3);
    }

    private void spread() {
        if (spreadEnabled) {
            for (int i = 0; i < terminalArray.length; i++) {
                switch (i) {
                case 0:
                    terminalArray[i]
                            .getElement()
                            .getStyle()
                            .setProperty("transform",
                                    "translate(" + (-getOffsetWidth() / 3 - 10) + "px, " + (-getOffsetHeight() / 3 - 10) + "px) scale(0.33, 0.33)");
                    break;
                case 1:
                    terminalArray[i].getElement().getStyle()
                            .setProperty("transform", "translate(" + (-getOffsetWidth() / 3 - 10) + "px, 0px) scale(0.33, 0.33)");
                    break;
                case 2:
                    terminalArray[i]
                            .getElement()
                            .getStyle()
                            .setProperty("transform",
                                    "translate(" + (-getOffsetWidth() / 3 - 10) + "px, " + (getOffsetHeight() / 3 + 10) + "px) scale(0.33, 0.33)");
                    break;
                case 4:
                    terminalArray[i]
                            .getElement()
                            .getStyle()
                            .setProperty("transform",
                                    "translate(" + (getOffsetWidth() / 3 + 10) + "px, " + (-getOffsetHeight() / 3 - 10) + "px) scale(0.33, 0.33)");
                    break;
                case 5:
                    terminalArray[i].getElement().getStyle()
                            .setProperty("transform", "translate(" + (getOffsetWidth() / 3 + 10) + "px, 0px) scale(0.33, 0.33)");
                    break;
                case 6:
                    terminalArray[i]
                            .getElement()
                            .getStyle()
                            .setProperty("transform",
                                    "translate(" + (getOffsetWidth() / 3 + 10) + "px, " + (getOffsetHeight() / 3 + 10) + "px) scale(0.33, 0.33)");
                    break;
                default:
                    break;
                }
            }

            pageEstimator.getElement().getStyle().setProperty("transform", "translate(" + (getOffsetWidth() * 1.3 + 10) + "px, 0px)");
        }
    }

    private void showPage(final PageLocation pageLocation, final int viewportNumber) {

//        logger.log(Level.SEVERE, "+++++++++++++ loadPageContent " + viewportNumber + " - "
//                + (pageLocation == null ? "NONE" : pageLocation.getItemId() + " - " + pageLocation.getPageNumber()));

        terminalArray[viewportNumber].show(pageLocation);

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

    public PageEstimator getPageEstimator() {
        return pageEstimator;
    }

    public int getColumnGap() {
        return COLUMN_GAP;
    }

    @Override
    public void onResize() {
        layout();
    }
}