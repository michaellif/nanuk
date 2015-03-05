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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.nanukreader.client.Callback;
import com.nanukreader.client.bookviewer.ContentTerminal.PageLayoutType;

public abstract class ThreeTerminalsLayoutManager extends AbstractLayoutManager {

    private static final Logger logger = Logger.getLogger(ThreeTerminalsLayoutManager.class.getName());

    public static int getTransitionTime() {
        return 600;
    }

    @Override
    public void setContentViewport(ContentViewport contentViewport) {
        if (contentViewport == null) {
            getContentViewport().removeStyleName(getLayoutManagerCss().contentViewport());
            getContentViewport().getTerminalArray()[2].removeStyleName(getLayoutManagerCss().terminal2Set());
            getContentViewport().getTerminalArray()[3].removeStyleName(getLayoutManagerCss().terminal3Set());
            getContentViewport().getTerminalArray()[4].removeStyleName(getLayoutManagerCss().terminal4Set());
        } else {
            contentViewport.addStyleName(getLayoutManagerCss().contentViewport());
            contentViewport.getTerminalArray()[2].addStyleName(getLayoutManagerCss().terminal2Set());
            contentViewport.getTerminalArray()[3].addStyleName(getLayoutManagerCss().terminal3Set());
            contentViewport.getTerminalArray()[4].addStyleName(getLayoutManagerCss().terminal4Set());
        }
        super.setContentViewport(contentViewport);
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
            case 2:
                terminal.setSize("100%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 3:
                terminal.setSize("100%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 4:
                terminal.setSize("100%", "100%");
                terminal.getElement().getStyle().setRight(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.sideBySide, columnWidth);
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
            getContentViewport().getTerminalArray()[4].setZIndex(2);
            getContentViewport().getTerminalArray()[4].addStyleName(getLayoutManagerCss().terminal4Shift());
            getContentViewport().getTerminalArray()[3].addStyleName(getLayoutManagerCss().terminal3HeadShift());
        } else {
            getContentViewport().getTerminalArray()[2].setZIndex(2);
            getContentViewport().getTerminalArray()[2].addStyleName(getLayoutManagerCss().terminal2Shift());
            getContentViewport().getTerminalArray()[3].addStyleName(getLayoutManagerCss().terminal3TailShift());
        }

        new Timer() {
            @Override
            public void run() {
                callback.onCall(null);
            }
        }.schedule(getTransitionTime());

    }

    @Override
    public void completePageTurnAnimation(boolean isForward, final Callback<Void> callback) {
        if (isForward) {
            getContentViewport().getTerminalArray()[4].setZIndex(0);
            getContentViewport().getTerminalArray()[4].removeStyleName(getLayoutManagerCss().terminal4Shift());
            getContentViewport().getTerminalArray()[3].removeStyleName(getLayoutManagerCss().terminal3HeadShift());
        } else {
            getContentViewport().getTerminalArray()[2].setZIndex(0);
            getContentViewport().getTerminalArray()[2].removeStyleName(getLayoutManagerCss().terminal2Shift());
            getContentViewport().getTerminalArray()[3].removeStyleName(getLayoutManagerCss().terminal3TailShift());
        }

        //Give time for CSS to apply before next update is happening (Chrome flicker without that delay)
        new Timer() {
            @Override
            public void run() {
                callback.onCall(null);
            }
        }.schedule(100);

    }

    @Override
    protected void prepareForwardTurn(final PageLocation newPageLocation, final Callback<Void> collback) {

        getContentViewport().getTerminalArray()[4].show(newPageLocation, new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                if (collback != null) {
                    collback.onCall(result);
                }
            }
        });

    }

    @Override
    protected void prepareBackwardTurn(final PageLocation newPageLocation, final Callback<Void> collback) {
        getContentViewport().getTerminalArray()[2].show(newPageLocation, new Callback<Void>() {

            @Override
            public void onCall(Void result) {
                if (collback != null) {
                    collback.onCall(result);
                }
            }
        });
    }

    @Override
    protected abstract ThreeTerminalsLayoutManagerCss getLayoutManagerCss();

}
