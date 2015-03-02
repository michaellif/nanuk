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

import com.nanukreader.client.library.Book;

public class PageLocation implements Comparable<PageLocation> {

    private static final Logger logger = Logger.getLogger(PageLocation.class.getName());

    private Book book;

    private final String itemId;

    private final int pageNumber;

    public PageLocation(Book book, String itemId, int pageNumber) {
        super();
        this.itemId = itemId;
        this.pageNumber = pageNumber;
    }

    public Book getBook() {
        return book;
    }

    public String getItemId() {
        return itemId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String toString() {
        return itemId + "(" + pageNumber + ")";
    }

    @Override
    public int compareTo(PageLocation other) {
        if (this == other || other == null) {
            return 1;
        } else if (itemId == other.itemId) {
            return pageNumber - other.pageNumber;
        } else {
            return book.getSpineItemIndex(itemId) - book.getSpineItemIndex(other.itemId);
        }
    };

}
