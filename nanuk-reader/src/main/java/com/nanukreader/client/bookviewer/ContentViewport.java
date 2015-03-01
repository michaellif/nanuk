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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

class ContentViewport extends FlowPanel {

    private static final Logger logger = Logger.getLogger(ContentViewport.class.getName());

    private final BookViewer bookViewer;

    private final PageEstimator pageEstimator;

    private final ContentTerminal[] terminalArray;

    private IBookLayoutManager layoutManager;

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

    void setLayoutManager(IBookLayoutManager layoutManager) {
        if (this.layoutManager != null) {
            this.layoutManager.setContentViewport(null);
        }
        this.layoutManager = layoutManager;
        layoutManager.setContentViewport(this);
        layoutManager.layout();
    }

    void showPage(final PageLocation pageLocation) {
        assert layoutManager != null;
        layoutManager.showPage(pageLocation);
    }

    /**
     * Use to bubble terminal up for debugging
     * 
     * @param terminalNumber
     */
    void revealTerminal(int terminalNumber) {
        terminalArray[terminalNumber].getElement().getStyle().setZIndex(100);
    }

    /**
     * Sink terminal back to it's original layer
     * 
     * @param terminalNumber
     */
    void concealTerminal(int terminalNumber) {
        terminalArray[terminalNumber].getElement().getStyle().setZIndex(terminalArray[terminalNumber].getZIndex());
    }

    public BookViewer getBookViewer() {
        return bookViewer;
    }

    ContentTerminal[] getTerminalArray() {
        return terminalArray;
    }

    public PageEstimator getPageEstimator() {
        return pageEstimator;
    }
}
