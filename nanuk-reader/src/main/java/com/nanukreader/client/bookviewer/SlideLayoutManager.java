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

import com.google.gwt.user.client.Timer;
import com.nanukreader.client.Callback;
import com.nanukreader.client.CssResources;

public class SlideLayoutManager extends ThreeTerminalsLayoutManager {

    public SlideLayoutManager() {
        CssResources.INSTANCE.slideLayoutManagerCss().ensureInjected();
    }

    @Override
    protected ThreeTerminalsLayoutManagerCss getLayoutManagerCss() {
        return CssResources.INSTANCE.slideLayoutManagerCss();
    }

    @Override
    public void startPageTurnAnimation(boolean isForward, final Callback<Void> callback) {
        if (isForward) {
            getContentViewport().getTerminalArray()[4].setZIndex(1);
            getContentViewport().getTerminalArray()[4].addStyleName(getLayoutManagerCss().terminal4Shift());
            getContentViewport().getTerminalArray()[3].setZIndex(2);
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
            getContentViewport().getTerminalArray()[3].setZIndex(1);
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
}
