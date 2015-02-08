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

public final class Storage {

    private final String SYSTEM_SUFFIX = "@";

    private final String CATALOG = "@CATALOG";

    Storage() {

    }

    /**
     * Add book to local storage
     * 
     * @return false if an identical book already exists in local storage
     */
    public boolean addBook(Book book) {
        return false;
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

    void updateRecord(String jsonRecord) {
        // TODO Auto-generated method stub

    }

    Record getRecord(String packageId) {
        com.google.gwt.storage.client.Storage localStorage = com.google.gwt.storage.client.Storage.getLocalStorageIfSupported();
        if (localStorage != null) {
            return JsonUtils.safeEval(localStorage.getItem(packageId + SYSTEM_SUFFIX));
        } else {
            return null;
        }
    }

    public Collection<String> getCatalog() {
        com.google.gwt.storage.client.Storage localStorage = com.google.gwt.storage.client.Storage.getLocalStorageIfSupported();
        String catalogString = null;
        Collection<String> packageIds = null;
        if (localStorage != null) {
            catalogString = localStorage.getItem(CATALOG);
            if (catalogString != null) {
                String[] split = catalogString.split("|");
                if (split.length > 0) {
                    packageIds = Arrays.asList(split);
                }
            }
        }
        return packageIds;
    }

}
