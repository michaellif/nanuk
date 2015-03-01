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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SevenTerminalsLayoutManager extends AbstractLayoutManager {

    private static final Logger logger = Logger.getLogger(SevenTerminalsLayoutManager.class.getName());

    @Override
    public void layout() {
        getContentViewport().getTerminalArray()[3].setZIndex(1);
    }

    @Override
    public void showPage(final PageLocation pageLocation) {
        super.showPage(pageLocation);

        //   startPageTurnAnimation();

        getContentViewport().getTerminalArray()[3].show(pageLocation);

        //  setUpTerminalView(pageLocation, 3);
        //   completePageTurnAnimation();
    }

    private void setUpTerminalView(final PageLocation pageLocation, final int terminalNumber) {

//        logger.log(Level.INFO, "+++++++++++++ loadPageContent " + terminalNumber + " - "
//                + (pageLocation == null ? "NONE" : pageLocation.getItemId() + " - " + pageLocation.getPageNumber()));

        if (terminalNumber == 3 || terminalNumber == 4 || terminalNumber == 5) {
            getContentViewport().getPageEstimator().getNextPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new Error(caught);
                }

                @Override
                public void onSuccess(PageLocation nextPageLocation) {
                    setUpTerminalView(nextPageLocation, terminalNumber + 1);
                }
            });
        }

        if (terminalNumber == 3 || terminalNumber == 2 || terminalNumber == 1) {
            getContentViewport().getPageEstimator().getPreviousPageLocation(pageLocation, new AsyncCallback<PageLocation>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new Error(caught);
                }

                @Override
                public void onSuccess(PageLocation previousPageLocation) {
                    setUpTerminalView(previousPageLocation, terminalNumber - 1);
                }
            });
        }

    }
}
