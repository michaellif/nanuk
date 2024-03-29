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
 * Created on Feb 7, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.core.client.JsonUtils;

/**
 * Local storage structure
 * 
 * CATALOG - {packageId1}|{packageId2}
 * 
 * {packageId1} - {book1Record}
 * {packageId1}/mimetype
 * {packageId1}/EPUB/book1-content.xhtml
 * {packageId1}/EPUB/book1-cover.jpg
 * {packageId1}/EPUB/book1-nav.xhtml
 * {packageId1}/EPUB/book1-night.css
 * {packageId1}/EPUB/book1.css
 * {packageId1}/EPUB/book1.ncx
 * {packageId1}/EPUB/book1.opf
 * {packageId1}/META-INF/container.xml
 * 
 * {packageId2} - {book2Record}
 * {packageId2}/mimetype
 * {packageId2}/EPUB/book2-content.xhtml
 * {packageId2}/EPUB/book2-cover.jpg
 * {packageId2}/EPUB/book2-nav.xhtml
 * {packageId2}/EPUB/book2-night.css
 * {packageId2}/EPUB/book2.css
 * {packageId2}/EPUB/book2.ncx
 * {packageId2}/EPUB/book2.opf
 * {packageId2}/META-INF/container.xml
 * 
 * @author michaellif
 *
 */
public final class Storage {

    private final String CATALOG = "CATALOG";

    private final char CATALOG_SEPARATOR = '|';

    Storage() {

    }

    /**
     * Add book to local storage
     * 
     * @return false if an identical book already exists in local storage
     */
    boolean addBook(Book book) {

        com.google.gwt.storage.client.Storage localStorage = com.google.gwt.storage.client.Storage.getLocalStorageIfSupported();

        if (getCatalog() != null && getCatalog().contains(book.getBookId())) {
            return false;
        }

        String catalog = localStorage.getItem(CATALOG);

        localStorage.setItem(CATALOG, book.getBookId() + (catalog == null ? "" : (CATALOG_SEPARATOR + catalog)));

        Record record = Record.create();
        record.setBookId(book.getBookId());
        localStorage.setItem(book.getBookId(), JsonUtils.stringify(record));
        return true;
    }

    /**
     * Delete book from local storage
     * 
     * @return false if no book with the given packageId found in local storage
     */
    public boolean deleteBook(String packageId) {
        return false;
    }

    /**
     * Returns Book with the given packageId in local storage
     * 
     * @param packageId
     * @return
     */
    public Book getBook(String packageId) {
        return null;
    }

    Record getRecord(String packageId) {
        com.google.gwt.storage.client.Storage localStorage = com.google.gwt.storage.client.Storage.getLocalStorageIfSupported();
        if (localStorage != null) {
            String recordJson = localStorage.getItem(packageId);
            if (recordJson != null) {
                return JsonUtils.safeEval(recordJson);
            }
        }
        return null;
    }

    public Collection<String> getCatalog() {
        com.google.gwt.storage.client.Storage localStorage = com.google.gwt.storage.client.Storage.getLocalStorageIfSupported();
        String catalogString = null;
        Collection<String> packageIds = null;
        if (localStorage != null) {
            catalogString = localStorage.getItem(CATALOG);
            if (catalogString != null) {
                String[] split = catalogString.split("\\" + CATALOG_SEPARATOR);
                if (split.length > 0) {
                    packageIds = Arrays.asList(split);
                }
            }
        }
        return packageIds;
    }
}
