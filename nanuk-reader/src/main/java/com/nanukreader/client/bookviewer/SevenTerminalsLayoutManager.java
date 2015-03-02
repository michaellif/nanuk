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
 * Created on Feb 28, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import java.util.logging.Logger;

import com.nanukreader.client.Callback;

/**
 * Dual Page only
 * 
 * @author michaellif
 *
 */
public abstract class SevenTerminalsLayoutManager extends AbstractLayoutManager {

    private static final Logger logger = Logger.getLogger(SevenTerminalsLayoutManager.class.getName());

    @Override
    public void layout() {
        getContentViewport().getTerminalArray()[3].setZIndex(1);
    }

    @Override
    public void showPage(final PageLocation pageLocation) {
        super.showPage(pageLocation);

        boolean isNext = pageLocation != null && pageLocation.compareTo(getContentViewport().getBookViewer().getCurrentPageLocation()) > 0;

        Callback<Void> preparationCollback = new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                //   startPageTurnAnimation();
                getContentViewport().getTerminalArray()[3].show(pageLocation);
                //   completePageTurnAnimation();

                //TODO prepareNextTurn and  preparePreviousTurn anticipating next turn
            }

        };

        if (isNext) {
            prepareNextTurn(getContentViewport().getBookViewer().getCurrentPageLocation(), pageLocation, preparationCollback);
        } else {
            preparePreviousTurn(getContentViewport().getBookViewer().getCurrentPageLocation(), pageLocation, preparationCollback);
        }

    }

    private void prepareNextTurn(final PageLocation currentPageLocation, final PageLocation newPageLocation, final Callback<Void> collback) {
        getContentViewport().getTerminalArray()[4].show(currentPageLocation == null ? null : new PageLocation(currentPageLocation.getBook(),
                currentPageLocation.getItemId(), currentPageLocation.getPageNumber() + 1), new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                getContentViewport().getTerminalArray()[5].show(newPageLocation, new Callback<Void>() {

                    @Override
                    public void onCall(Void result) {
                        getContentViewport().getTerminalArray()[6].show(newPageLocation == null ? null : new PageLocation(newPageLocation.getBook(),
                                newPageLocation.getItemId(), newPageLocation.getPageNumber() + 1), new Callback<Void>() {

                            @Override
                            public void onCall(Void result) {
                                collback.onCall(result);
                            }
                        });
                    }
                });
            }
        });
    }

    private void preparePreviousTurn(final PageLocation currentPageLocation, final PageLocation newPageLocation, final Callback<Void> collback) {
        getContentViewport().getTerminalArray()[2].show(currentPageLocation, new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                getContentViewport().getTerminalArray()[1].show(
                        newPageLocation == null ? null : new PageLocation(newPageLocation.getBook(), newPageLocation.getItemId(), newPageLocation
                                .getPageNumber() + 1), new Callback<Void>() {

                            @Override
                            public void onCall(Void result) {
                                getContentViewport().getTerminalArray()[0].show(newPageLocation, new Callback<Void>() {

                                    @Override
                                    public void onCall(Void result) {
                                        collback.onCall(result);
                                    }
                                });
                            }
                        });
            }
        });

    }

}
