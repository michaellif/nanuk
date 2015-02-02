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
 * Created on Feb 1, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.ocf;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.typedarrays.shared.Int8Array;
import com.nanukreader.client.ByteUtils;

public class Inflator {

    private final Int8Array compressed;

    private List<OcfEntry> entiries;

    public Inflator(Int8Array compressed) {
        this.compressed = compressed;
    }

    public void inflate() {

        ByteUtils.print(compressed);

        if (entiries != null) {
            throw new Error("Already inflated");
        }
        entiries = new ArrayList<>();

        for (;;) {
            OcfEntry entry = readEntry();
            if (entry != null) {
                entiries.add(entry);
            } else {
                break;
            }
        }

    }

    private OcfEntry readEntry() {
        int offset = 0;
        if (entiries != null && entiries.size() > 0) {
            OcfEntry lastEntry = entiries.get(entiries.size() - 1);
            offset += lastEntry.entryOffset + lastEntry.entryLength;
        }

        Int8Array headerData = compressed.subarray(offset, offset + ZipConstants.ZipHeader.LOC.getHeaderSize());
        if (headerData.length() == 0) {
            return null;
        }

        if (headerData.length() != ZipConstants.ZipHeader.LOC.getHeaderSize() || !ZipConstants.ZipHeader.LOC.checkSignature(headerData.subarray(0, 4))) {
            throw new Error("Can't read Entry");
        }

        return new OcfEntry(headerData, offset);
    }

    public class OcfEntry {

        private String name;

        private final int entryOffset;

        private final int entryLength;

        public OcfEntry(Int8Array headerData, int entryOffset) {
            this.entryOffset = entryOffset;
            entryLength = ZipConstants.ZipHeader.LOC.getHeaderSize();
        }

        public String getName() {
            return name;
        }

    }
}
