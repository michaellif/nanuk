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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nanukreader.client.Callback;
import com.nanukreader.client.CssResources;

class ContentViewport extends FlowPanel {

    public static enum UpdateStatus {
        updated, requested, processing
    }

    private static final Logger logger = Logger.getLogger(ContentViewport.class.getName());

    private static final String CONTENT_VIEWPORT_CLASS_PREFIX = "ContentViewport";

    private final BookViewer bookViewer;

    private final PageEstimator pageEstimator;

    private final ContentTerminal[] terminalArray;

    private IBookLayoutManager layoutManager;

    private int columnWidth;

    private UpdateStatus updateStatus = UpdateStatus.updated;

    public ContentViewport(BookViewer bookViewer) {
        this.bookViewer = bookViewer;

        CssResources.INSTANCE.contentViewportCss().ensureInjected();
        setStyleName(CssResources.INSTANCE.contentViewportCss().contentViewport());

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
        layout();
    }

    void layout() {
        columnWidth = (int) Math.floor((getOffsetWidth() - bookViewer.getColumnGap()) / 2) - 1;
        layoutManager.layout();
    }

    void updateContent() {
        if (updateStatus != UpdateStatus.requested) {
            if (updateStatus == UpdateStatus.processing) {
                updateStatus = UpdateStatus.requested;
                return;
            } else {
                updateStatus = UpdateStatus.requested;
            }
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    updateStatus = UpdateStatus.processing;
                    if (layoutManager != null) {
                        layoutManager.updateContent(new Callback<PageLocation>() {

                            @Override
                            public void onCall(PageLocation result) {
                                if (updateStatus == UpdateStatus.requested) {
                                    updateStatus = UpdateStatus.updated;
                                    updateContent();
                                } else {
                                    updateStatus = UpdateStatus.updated;
                                }
                            }
                        });
                    }
                }
            });

        }
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

    public int getColumnWidth() {
        return columnWidth;
    }

}
