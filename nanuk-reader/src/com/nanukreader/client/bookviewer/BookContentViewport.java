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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class BookContentViewport extends ScrollPanel {

    private final Frame[] pageHolderArray;

    public BookContentViewport() {

        final HorizontalPanel contentViewer = new HorizontalPanel();
        contentViewer.setHeight("450px");
        setWidget(contentViewer);

        pageHolderArray = new Frame[6];
        for (int i = 0; i < pageHolderArray.length; i++) {
            pageHolderArray[i] = new Frame("javascript:''");
            pageHolderArray[i].setSize("300px", "450px");
            pageHolderArray[i].getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            contentViewer.add(pageHolderArray[i]);
        }
    }

    protected Frame getPageHolder(int index) {
        return pageHolderArray[index];
    }
}
