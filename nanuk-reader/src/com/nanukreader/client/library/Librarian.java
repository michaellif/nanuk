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
 * Created on Feb 2, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsonUtils;

public class Librarian {

    private static Librarian instance;

    private final Storage storage;

    private Map<String, Record> records;

    private Librarian() {
        storage = new Storage();

        loadRecords();
    }

    public static Librarian instance() {
        if (instance == null) {
            instance = new Librarian();
        }
        return instance;
    }

    public Record getRecord(String packageId) {
        return records.get(packageId).deepCopy();
    }

    public void updateRecord(Record record) {
        records.put(record.getPackageId(), record);
        storage.updateRecord(JsonUtils.stringify(record));
    }

    public void addBook(Book book) {
        if (storage.addBook(book)) {
            throw new Error("Book already exist");
        }

    }

    private void loadRecords() {
        assert records == null;
        records = new HashMap<>();
        Collection<String> catalog = storage.getCatalog();
        if (catalog != null) {
            for (String packageId : catalog) {
                Record record = storage.getRecord(packageId);
                if (record == null) {
                    throw new Error("Record is missing");
                }
                records.put(packageId, record);
            }
        }
    }
}
