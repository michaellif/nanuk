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
import com.nanukreader.client.bookviewer.ContentTerminal.PageLayoutType;

public class FlipLayoutManager extends SevenTerminalsLayoutManager {

    @Override
    public void layout() {
        super.layout();
        ContentViewport contentViewport = getContentViewport();
        BookViewer bookViewer = contentViewport.getBookViewer();
        int columnWidth = (int) Math.floor((contentViewport.getOffsetWidth() - bookViewer.getColumnGap()) / 2) - 1;
        for (int i = 0; i < contentViewport.getTerminalArray().length; i++) {
            ContentTerminal terminal = contentViewport.getTerminalArray()[i];
            terminal.getElement().getStyle().setPosition(Position.ABSOLUTE);
            terminal.getElement().getStyle().setTop(0, Unit.PX);
            switch (i) {
            case 0:
            case 1:
            case 2:
                terminal.setSize("50%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.leftSide, columnWidth);
                break;
            case 3:
                terminal.setSize("100%", "100%");
                terminal.getElement().getStyle().setLeft(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.sideBySide, columnWidth);
                break;
            case 4:
            case 5:
            case 6:
                terminal.setSize("50%", "100%");
                terminal.getElement().getStyle().setRight(0, Unit.PX);
                terminal.setPageDimensions(PageLayoutType.rightSide, columnWidth);
                break;
            default:
                break;
            }
        }

        PageEstimator pageEstimator = contentViewport.getPageEstimator();
        pageEstimator.setPageDimensions(PageLayoutType.leftSide, columnWidth);
        pageEstimator.setSize("50%", "100%");
        pageEstimator.getElement().getStyle().setLeft(0, Unit.PX);

    }

}
