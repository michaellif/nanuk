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
 * Created on Mar 1, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.nanukreader.client.Callback;

public abstract class AbstractLayoutManager implements IBookLayoutManager {

    private static final Logger logger = Logger.getLogger(SevenTerminalsLayoutManager.class.getName());

    private ContentViewport contentViewport;

    private PageLocation currentPage;

    @Override
    public void setContentViewport(ContentViewport contentViewport) {
        if (contentViewport == null) {
            if (getContentViewport() != null) {
                getContentViewport().removeStyleName(getLayoutManagerCss().contentViewport());
                getContentViewport().getTerminalArray()[0].removeStyleName(getLayoutManagerCss().terminal0());
                getContentViewport().getTerminalArray()[1].removeStyleName(getLayoutManagerCss().terminal1());
                getContentViewport().getTerminalArray()[2].removeStyleName(getLayoutManagerCss().terminal2());
                getContentViewport().getTerminalArray()[3].removeStyleName(getLayoutManagerCss().terminal3());
                getContentViewport().getTerminalArray()[4].removeStyleName(getLayoutManagerCss().terminal4());
                getContentViewport().getTerminalArray()[5].removeStyleName(getLayoutManagerCss().terminal5());
                getContentViewport().getTerminalArray()[6].removeStyleName(getLayoutManagerCss().terminal6());
            }
        } else {
            contentViewport.addStyleName(getLayoutManagerCss().contentViewport());
            contentViewport.getTerminalArray()[0].addStyleName(getLayoutManagerCss().terminal0());
            contentViewport.getTerminalArray()[1].addStyleName(getLayoutManagerCss().terminal1());
            contentViewport.getTerminalArray()[2].addStyleName(getLayoutManagerCss().terminal2());
            contentViewport.getTerminalArray()[3].addStyleName(getLayoutManagerCss().terminal3());
            contentViewport.getTerminalArray()[4].addStyleName(getLayoutManagerCss().terminal4());
            contentViewport.getTerminalArray()[5].addStyleName(getLayoutManagerCss().terminal5());
            contentViewport.getTerminalArray()[6].addStyleName(getLayoutManagerCss().terminal6());
        }
        this.contentViewport = contentViewport;
    }

    public ContentViewport getContentViewport() {
        return contentViewport;
    }

    @Override
    public void layout() {
        getContentViewport().getTerminalArray()[3].setZIndex(1);

        getContentViewport().getTerminalArray()[0].reset();
        getContentViewport().getTerminalArray()[1].reset();
        getContentViewport().getTerminalArray()[2].reset();
        getContentViewport().getTerminalArray()[4].reset();
        getContentViewport().getTerminalArray()[5].reset();
        getContentViewport().getTerminalArray()[6].reset();

    }

    @Override
    public void updateContent(final Callback<PageLocation> callback) {
        if (logger.isLoggable(Level.FINE)) {
            PageLocation pageLocation = getContentViewport().getBookViewer().getCurrentPageLocation();
            logger.log(Level.FINE, "LayoutManager showPage() called for [" + (pageLocation == null ? "NONE" : pageLocation) + "]");
        }

        final PageLocation pageLocation = getContentViewport().getBookViewer().getCurrentPageLocation();

        final boolean isForward = pageLocation != null && pageLocation.compareTo(getCurrentPage()) > 0;

        Callback<Void> preparationCollback = new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                startPageTurnAnimation(isForward, new Callback<Void>() {

                    @Override
                    public void onCall(Void result) {
                        getContentViewport().getTerminalArray()[3].show(pageLocation, new Callback<Void>() {

                            @Override
                            public void onCall(Void result) {
                                completePageTurnAnimation(isForward, new Callback<Void>() {

                                    @Override
                                    public void onCall(Void result) {
                                        setCurrentPage(pageLocation);
                                        callback.onCall(pageLocation);

                                        getContentViewport().getPageEstimator().getNextPageLocation(pageLocation, new Callback<PageLocation>() {

                                            @Override
                                            public void onCall(PageLocation nextPageLocation) {
                                                if (nextPageLocation != null) {
                                                    prepareBackwardTurn(nextPageLocation, null);
                                                }
                                            }
                                        });

                                        getContentViewport().getPageEstimator().getPreviousPageLocation(pageLocation, new Callback<PageLocation>() {

                                            @Override
                                            public void onCall(PageLocation previousPageLocation) {
                                                if (previousPageLocation != null) {
                                                    prepareBackwardTurn(previousPageLocation, null);
                                                }
                                            }
                                        });

                                    }
                                });
                            }
                        });

                    }
                });

            }

        };

        if (isForward) {
            prepareForwardTurn(pageLocation, preparationCollback);
        } else {
            prepareBackwardTurn(pageLocation, preparationCollback);
        }

    }

    protected abstract void prepareBackwardTurn(PageLocation pageLocation, Callback<Void> preparationCollback);

    protected abstract void prepareForwardTurn(PageLocation pageLocation, Callback<Void> preparationCollback);

    public PageLocation getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(PageLocation currentPage) {
        this.currentPage = currentPage;
    }

    protected abstract BaseLayoutManagerCss getLayoutManagerCss();

}
