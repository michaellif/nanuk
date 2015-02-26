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
 * Created on Feb 17, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;

public class CfiMarker extends ComplexPanel {

    public static final String CFI_MARKER_ATTR = "data-nanuk-cfimarker";

    private final String markerId;

    public CfiMarker() {

        markerId = Document.get().createUniqueId();

        Element marker = DOM.createDiv();
        marker.getStyle().setDisplay(Display.INLINE_BLOCK);
        marker.getStyle().setPosition(Position.RELATIVE);
        marker.setAttribute("id", markerId);
        marker.setAttribute(CFI_MARKER_ATTR, "true");

        //TODO don't add pointer in production
        Element pointer = DOM.createDiv();
        pointer.getStyle().setWidth(14, Unit.PX);
        pointer.getStyle().setHeight(4, Unit.PX);
        pointer.getStyle().setPosition(Position.ABSOLUTE);
        pointer.getStyle().setBackgroundColor("red");
        DOM.appendChild(marker, pointer);

        setElement(marker);

    }

    public String getId() {
        return markerId;
    }

}
