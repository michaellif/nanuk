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
 * Created on Mar 1, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractLayoutManager implements IBookLayoutManager {

    private static final Logger logger = Logger.getLogger(SevenTerminalsLayoutManager.class.getName());

    private ContentViewport contentViewport;

    @Override
    public void setContentViewport(ContentViewport contentViewport) {
        this.contentViewport = contentViewport;
    }

    public ContentViewport getContentViewport() {
        return contentViewport;
    }

    @Override
    public void showPage(final PageLocation pageLocation) {
        logger.log(Level.INFO, "+++++++++++++ showPage " + (pageLocation == null ? "NONE" : pageLocation.getItemId() + " - " + pageLocation.getPageNumber()));
    }

//    abstract void completePageTurnAnimation();
//
//    abstract void startPageTurnAnimation();
}
