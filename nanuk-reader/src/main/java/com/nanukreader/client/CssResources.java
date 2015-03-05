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
 * Created on Mar 2, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.Import;
import com.nanukreader.client.bookviewer.AccordionLayoutManagerCss;
import com.nanukreader.client.bookviewer.BaseLayoutManagerCss;
import com.nanukreader.client.bookviewer.FadeLayoutManagerCss;
import com.nanukreader.client.bookviewer.FlipLayoutManagerCss;
import com.nanukreader.client.bookviewer.LayerLayoutManagerCss;
import com.nanukreader.client.bookviewer.PanoramaLayoutManagerCss;
import com.nanukreader.client.bookviewer.ShiftLayoutManagerCss;
import com.nanukreader.client.bookviewer.SlideLayoutManagerCss;

public interface CssResources extends ClientBundle {

    public static final CssResources INSTANCE = GWT.create(CssResources.class);

    @Import(BaseLayoutManagerCss.class)
    @Source({ "flip_layout_manager.css", "base_layout_manager.css" })
    public FlipLayoutManagerCss flipLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "accordion_layout_manager.css", "base_layout_manager.css" })
    public AccordionLayoutManagerCss accordionLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "panorama_layout_manager.css", "base_layout_manager.css" })
    public PanoramaLayoutManagerCss panoramaLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "shift_layout_manager.css", "base_layout_manager.css" })
    public ShiftLayoutManagerCss shiftLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "fade_layout_manager.css", "base_layout_manager.css" })
    public FadeLayoutManagerCss fadeLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "slide_layout_manager.css", "base_layout_manager.css" })
    public SlideLayoutManagerCss slideLayoutManagerCss();

    @Import(BaseLayoutManagerCss.class)
    @Source({ "layer_layout_manager.css", "base_layout_manager.css" })
    public LayerLayoutManagerCss layerLayoutManagerCss();

}
