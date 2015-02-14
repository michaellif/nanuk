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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.CFI;
import com.nanukreader.client.library.ItemPageLocation;

public class BookViewer extends FlowPanel {

    private static final Logger logger = Logger.getLogger(BookContentViewport.class.getName());

    private final BookContentViewport contentViewport;

    private Book book;

    public BookViewer() {
        setHeight("450px");
        contentViewport = new BookContentViewport(this);
        add(contentViewport);
    }

    public void openBook(Book book) {
        this.book = book;

        //TODO getCFI from progress
        CFI cfi = CFI.create("xchapter_004");
        show(cfi);
    }

    public Book getBook() {
        return book;
    }

    public void show(final CFI cfi) {
        getPageLocation(cfi, new AsyncCallback<ItemPageLocation>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.WARNING, "Can't find item [" + cfi.getItemId() + "]");
                show(null);
            }

            @Override
            public void onSuccess(ItemPageLocation mainPageLocation) {
                contentViewport.loadPageContent(mainPageLocation, 2);
            }

        });

    }

    public void getPreviousPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_003", 1, 1));
    }

    public void getNextPageLocation(ItemPageLocation pageLocation, AsyncCallback<ItemPageLocation> callback) {
        callback.onSuccess(new ItemPageLocation("xchapter_005", 1, 1));
    }

    private void getPageLocation(final CFI cfi, final AsyncCallback<ItemPageLocation> callback) {
        book.getContentItem(cfi.getItemId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String result) {
                callback.onSuccess(getPageLocation(cfi, result));
            }

        });
    }

    private ItemPageLocation getPageLocation(CFI cfi, String content) {
        // TODO make real
        return new ItemPageLocation(cfi.getItemId(), 1, 1);
    }
}
