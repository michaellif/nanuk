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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Catalog {

    private Map<String, Record> localRecords;

    private final Storage storage;

    public Catalog(Storage storage) {
        this.storage = storage;
        loadLocalCatalog();
    }

    public void loadLocalCatalog() {
        assert localRecords == null;
        localRecords = new HashMap<>();
        Collection<String> catalog = storage.getCatalog();
        if (catalog != null) {
            for (String packageId : catalog) {
                Record record = storage.getRecord(packageId);
                if (record == null) {
                    throw new Error("Record is missing");
                }
                localRecords.put(packageId, record);
            }
        }
    }

    public Record getRecord(String packageId) {
        return localRecords.get(packageId).deepCopy();
    }
}
