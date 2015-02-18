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
package com.nanukreader.client.bookviewer.dev;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nanukreader.client.bookviewer.IBookViewer;
import com.nanukreader.client.bookviewer.ItemPageLocation;
import com.nanukreader.client.bookviewer.PageContentViewport;
import com.nanukreader.client.bookviewer.PageEstimator;
import com.nanukreader.client.library.Book;

public class DevBookViewer extends FlowPanel implements IBookViewer {

    private static final Logger logger = Logger.getLogger(DevBookContentViewport.class.getName());

    private final DevBookContentViewport contentViewport;

    private final PageEstimator pageEstimator;

    private Book book;

    public DevBookViewer() {
        contentViewport = new DevBookContentViewport(this);
        add(contentViewport);

        pageEstimator = new PageEstimator(this);
        add(pageEstimator);

        PageContentViewport.setAllViewportSizes(300, 450);
    }

    @Override
    public PageEstimator getPageEstimator() {
        return pageEstimator;
    }

    @Override
    public void openBook(Book book) {
        this.book = book;

        //TODO getCFI from progress
        show("/6/20[xchapter_004]!/4/2[test6]/8");
    }

    @Override
    public Book getBook() {
        return book;
    }

    public void show(final String cfi) {
        pageEstimator.getPageLocation(cfi, new AsyncCallback<ItemPageLocation>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, "Can't convert CFI [" + cfi + "] to Page Loaction.");
            }

            @Override
            public void onSuccess(ItemPageLocation mainPageLocation) {
                contentViewport.loadPageContent(mainPageLocation, 2);
            }

        });

    }

}
