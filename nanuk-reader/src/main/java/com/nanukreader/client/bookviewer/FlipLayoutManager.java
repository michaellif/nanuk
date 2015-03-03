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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.nanukreader.client.Callback;
import com.nanukreader.client.CssResources;
import com.nanukreader.client.bookviewer.ContentTerminal.PageLayoutType;

public class FlipLayoutManager extends SevenTerminalsLayoutManager {

    public FlipLayoutManager() {
        CssResources.INSTANCE.flipLayoutManagerCss().ensureInjected();
    }

    public static int getTransitionTime() {
        return 500;
    }

    @Override
    public void setContentViewport(ContentViewport contentViewport) {
        super.setContentViewport(contentViewport);
        if (contentViewport == null) {
            getContentViewport().removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().contentViewport());
            getContentViewport().getTerminalArray()[1].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal1Set());
            getContentViewport().getTerminalArray()[5].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal5Set());
        } else {
            getContentViewport().addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().contentViewport());
            getContentViewport().getTerminalArray()[1].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal1Set());
            getContentViewport().getTerminalArray()[5].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal5Set());
        }
    }

    @Override
    public void layout() {
        super.layout();
        int columnWidth = getContentViewport().getColumnWidth();
        for (int i = 0; i < getContentViewport().getTerminalArray().length; i++) {
            ContentTerminal terminal = getContentViewport().getTerminalArray()[i];
            terminal.getElement().getStyle().setPosition(Position.ABSOLUTE);
            terminal.getElement().getStyle().setTop(0, Unit.PX);
            switch (i) {
            case 0:
            case 2:
            case 5:
                terminal.setSize("50%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.leftSide, columnWidth);
                break;
            case 3:
                terminal.setSize("100%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 1:
            case 4:
            case 6:
                terminal.setSize("50%", "100%");
                terminal.getElement().getStyle().setRight(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.rightSide, columnWidth);
                break;
            default:
                break;
            }
        }

        PageEstimator pageEstimator = getContentViewport().getPageEstimator();
        pageEstimator.setPageDimensions(PageLayoutType.leftSide, columnWidth);
        pageEstimator.setSize("50%", "100%");
        pageEstimator.getElement().getStyle().setLeft(0, Unit.PX);

    }

    @Override
    public void startPageTurnAnimation(boolean isForward, final Callback<Void> callback) {
        if (isForward) {
            getContentViewport().getTerminalArray()[4].setZIndex(4);
            getContentViewport().getTerminalArray()[4].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal4Flip());

            getContentViewport().getTerminalArray()[5].setZIndex(3);
            getContentViewport().getTerminalArray()[5].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal5Flip());

            getContentViewport().getTerminalArray()[6].setZIndex(2);

        } else {
            getContentViewport().getTerminalArray()[2].setZIndex(4);
            getContentViewport().getTerminalArray()[2].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal2Flip());

            getContentViewport().getTerminalArray()[1].setZIndex(3);
            getContentViewport().getTerminalArray()[1].addStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal1Flip());

            getContentViewport().getTerminalArray()[0].setZIndex(2);

        }

        new Timer() {
            @Override
            public void run() {
                callback.onCall(null);
            }
        }.schedule(getTransitionTime());

    }

    @Override
    public void completePageTurnAnimation(boolean isForward, Callback<Void> callback) {
        if (isForward) {
            getContentViewport().getTerminalArray()[4].setZIndex(0);
            getContentViewport().getTerminalArray()[4].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal4Flip());

            getContentViewport().getTerminalArray()[5].setZIndex(0);
            getContentViewport().getTerminalArray()[5].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal5Flip());

            getContentViewport().getTerminalArray()[6].setZIndex(0);

        } else {
            getContentViewport().getTerminalArray()[2].setZIndex(0);
            getContentViewport().getTerminalArray()[2].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal2Flip());

            getContentViewport().getTerminalArray()[1].setZIndex(0);
            getContentViewport().getTerminalArray()[1].removeStyleName(CssResources.INSTANCE.flipLayoutManagerCss().terminal1Flip());

            getContentViewport().getTerminalArray()[0].setZIndex(0);

        }
        callback.onCall(null);
    }
}
