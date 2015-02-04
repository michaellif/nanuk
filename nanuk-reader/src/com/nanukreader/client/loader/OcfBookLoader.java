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
package com.nanukreader.client.loader;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.typedarrays.shared.Int8Array;
import com.nanukreader.client.ByteUtils;
import com.nanukreader.client.deflate.Base64Encoder;
import com.nanukreader.client.deflate.Inflator;
import com.nanukreader.client.io.BitInputStream;
import com.nanukreader.client.io.ByteArrayInputStream;
import com.nanukreader.client.library.Book;

public class OcfBookLoader implements IBookLoader {

    private final Int8Array compressed;

    private List<LocalFileHeader> entiries;

    private Book book;

    public OcfBookLoader(Int8Array compressed) {
        this.compressed = compressed;
    }

    public Book load() {
        if (book != null) {
            throw new Error("Book already loaded");
        }

        entiries = new ArrayList<>();

        LocalFileHeader mimetype = readLocalFileHeader();
        System.out.println(mimetype);
        if (validateMimetype(mimetype)) {
            entiries.add(mimetype);
        } else {
            throw new Error("Unexpected MIME type");
        }

        book = new Book(this);

        for (;;) {
            LocalFileHeader header = readLocalFileHeader();
            if (header != null) {
                System.out.println(header);
                entiries.add(header);
            } else {
                break;
            }
        }

        book.setContainerDescriptor(inflateContainerDescriptor());

        book.setPackagingDescriptor(inflatePackagingDescriptor());

        book.setContent(inflateContent());

        book.setCoverImage(inflateCoverImage());

        return book;
    }

    private String inflateContainerDescriptor() {
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if ("META-INF/container.xml".equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Container Descriptor is not found");
        }
        return ByteUtils.toString(inflateLocalFile(header));
    }

    private String inflatePackagingDescriptor() {
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if ("EPUB/wasteland.opf".equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Container Descriptor is not found");
        }
        return ByteUtils.toString(inflateLocalFile(header));
    }

    private String inflateContent() {
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if ("EPUB/wasteland-content.xhtml".equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Contant is not found");
        }
        return ByteUtils.toString(inflateLocalFile(header));
    }

    private String inflateCoverImage() {
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if ("EPUB/wasteland-cover.jpg".equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Cover is not found");
        }
        return ByteUtils.toString(Base64Encoder.encode(inflateLocalFile(header)));
    }

    private Int8Array inflateLocalFile(LocalFileHeader header) {
        Int8Array compressedData = compressed.subarray(header.dataOffset, header.dataOffset + header.compressedSize);
        BitInputStream in = new BitInputStream(new ByteArrayInputStream(compressedData));
        return Inflator.inflate(in);
    }

    private boolean validateMimetype(LocalFileHeader mimetype) {
        int offset = mimetype.entryOffset + ZipConstants.ZipHeader.LOC.getHeaderSize() + mimetype.nameLength;
        String mimetypeContent = ByteUtils.toStringUtf(compressed.subarray(offset, offset + mimetype.compressedSize));
        return "application/epub+zip".equals(mimetypeContent);
    }

    private LocalFileHeader readLocalFileHeader() {
        int offset = 0;
        if (entiries != null && entiries.size() > 0) {
            LocalFileHeader lastEntry = entiries.get(entiries.size() - 1);
            offset += lastEntry.entryOffset + lastEntry.entryLength;
        }

        Int8Array headerData = compressed.subarray(offset, offset + ZipConstants.ZipHeader.LOC.getHeaderSize());
        if (headerData.length() == 0 || ZipConstants.ZipHeader.CEN.checkSignature(headerData.subarray(0, 4))) {
            return null;
        }

        if (headerData.length() != ZipConstants.ZipHeader.LOC.getHeaderSize() || !ZipConstants.ZipHeader.LOC.checkSignature(headerData.subarray(0, 4))) {
            throw new Error("Can't read Entry");
        }

        return new LocalFileHeader(headerData, offset);
    }

    public class LocalFileHeader {

        private final String name;

        private final int entryOffset;

        private final int dataOffset;

        private int entryLength;

        private final int nameLength;

        private final int extraLength;

        private final short bitFlag;

        private final short compressionMethod;

        private final int compressedSize;

        public LocalFileHeader(Int8Array headerData, final int entryOffset) {
            this.entryOffset = entryOffset;

            entryLength = ZipConstants.ZipHeader.LOC.getHeaderSize();

            int offset = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getOffset();
            int length = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getLength();
            entryLength += ByteUtils.toInt(headerData.subarray(offset, offset + length));

            offset = ZipConstants.LocalFileFieldOffset.NAME_LENGTH.getOffset();
            length = ZipConstants.LocalFileFieldOffset.NAME_LENGTH.getLength();
            nameLength = ByteUtils.toShort(headerData.subarray(offset, offset + length));
            entryLength += nameLength;

            offset = ZipConstants.LocalFileFieldOffset.EXTRA_LENGTH.getOffset();
            length = ZipConstants.LocalFileFieldOffset.EXTRA_LENGTH.getLength();
            extraLength = ByteUtils.toShort(headerData.subarray(offset, offset + length));
            entryLength += extraLength;

            dataOffset = ZipConstants.ZipHeader.LOC.getHeaderSize() + entryOffset + nameLength + extraLength;

            offset = ZipConstants.LocalFileFieldOffset.BIT_FLAG.getOffset();
            length = ZipConstants.LocalFileFieldOffset.BIT_FLAG.getLength();
            bitFlag = ByteUtils.toShort(headerData.subarray(offset, offset + length));

            offset = ZipConstants.LocalFileFieldOffset.COMPRESSION_METHOD.getOffset();
            length = ZipConstants.LocalFileFieldOffset.COMPRESSION_METHOD.getLength();
            compressionMethod = ByteUtils.toShort(headerData.subarray(offset, offset + length));

            offset = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getOffset();
            length = ZipConstants.LocalFileFieldOffset.COMPRESSED_SIZE.getLength();
            compressedSize = ByteUtils.toInt(headerData.subarray(offset, offset + length));

            offset = entryOffset + ZipConstants.ZipHeader.LOC.getHeaderSize();
            if ((bitFlag & ZipConstants.EFS) != 0) {
                name = ByteUtils.toStringUtf(compressed.subarray(offset, offset + nameLength));
            } else {
                name = ByteUtils.toString(compressed.subarray(offset, offset + nameLength));
            }

        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("bitFlag=").append(Integer.toBinaryString(0xFFFF & bitFlag)).append(", ");
            builder.append("compressionMethod=").append(Integer.toBinaryString(0xFFFF & compressionMethod)).append(", ");
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
