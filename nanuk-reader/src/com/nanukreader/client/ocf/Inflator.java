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

        private final String name;

        private final int entryOffset;

        private int entryLength;

        private final int nameLength;

        private final int extraLength;

        private final short bitFlag;

        private final int compressedSize;

        public OcfEntry(Int8Array headerData, int entryOffset) {
            this.entryOffset = entryOffset;

            this.entryLength = ZipConstants.ZipHeader.LOC.getHeaderSize();

            int offset = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getOffset();
            int length = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getLength();
            this.entryLength += ByteUtils.toInt(headerData.subarray(offset, offset + length));

            offset = ZipConstants.LocalFileFieldOffset.NAME_LENGTH.getOffset();
            length = ZipConstants.LocalFileFieldOffset.NAME_LENGTH.getLength();
            nameLength = ByteUtils.toShort(headerData.subarray(offset, offset + length));
            this.entryLength += nameLength;

            offset = ZipConstants.LocalFileFieldOffset.EXTRA_LENGTH.getOffset();
            length = ZipConstants.LocalFileFieldOffset.EXTRA_LENGTH.getLength();
            extraLength = ByteUtils.toShort(headerData.subarray(offset, offset + length));
            this.entryLength += extraLength;

            offset = ZipConstants.LocalFileFieldOffset.BIT_FLAG.getOffset();
            length = ZipConstants.LocalFileFieldOffset.BIT_FLAG.getLength();
            bitFlag = ByteUtils.toShort(headerData.subarray(offset, offset + length));

            offset = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getOffset();
            length = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getLength();
            compressedSize = ByteUtils.toInt(headerData.subarray(offset, offset + length));

            offset = entryOffset + ZipConstants.ZipHeader.LOC.getHeaderSize();
            if ((bitFlag & ZipConstants.EFS) != 0) {
                name = ByteUtils.toStringUtf(compressed.subarray(offset, offset + nameLength));
            } else {
                name = ByteUtils.toString(compressed.subarray(offset, offset + nameLength));
            }

            System.out.println("+++++++++++++++++" + this);
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("bitFlag=").append(Integer.toBinaryString(0xFFFF & bitFlag)).append(", ");

            builder.append("name=").append(name).append(", ");
            builder.append("entryOffset=").append(entryOffset).append(", ");
            builder.append("entryLength=").append(entryLength).append(", ");
            builder.append("nameLength=").append(nameLength).append(", ");
            builder.append("extraLength=").append(extraLength).append(", ");
            builder.append("compressedSize=").append(compressedSize).append(", ");

            return builder.toString();
        }
    }
}
