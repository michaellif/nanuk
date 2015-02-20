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
import java.util.logging.Level;
import java.util.logging.Logger;

import name.pehl.totoe.xml.client.Document;
import name.pehl.totoe.xml.client.HasText;
import name.pehl.totoe.xml.client.Node;
import name.pehl.totoe.xml.client.XmlParser;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.nanukreader.client.ByteUtils;
import com.nanukreader.client.deflate.Base64Encoder;
import com.nanukreader.client.deflate.Inflator;
import com.nanukreader.client.io.BitInputStream;
import com.nanukreader.client.io.ByteArrayInputStream;
import com.nanukreader.client.library.Book;
import com.nanukreader.client.library.SpineItem;
import com.nanukreader.client.library.ManifestItem;
import com.nanukreader.client.library.PackagingDescriptor;

public class OcfBookLoader implements IBookLoader {

    private static final Logger logger = Logger.getLogger(OcfBookLoader.class.getName());

    public static final String CONTAINER_LOCATION = "META-INF/container.xml";

    public static final String CONTAINER_ROOTFILE_TAG = "rootfile";

    private final Int8Array compressed;

    private List<LocalFileHeader> entiries;

    private Book book;

    private CompletionStatus completionStatus;

    public OcfBookLoader(Int8Array compressed) {
        this.compressed = compressed;
        completionStatus = CompletionStatus.notStarted;
    }

    @Override
    public Book load() {
        if (completionStatus != CompletionStatus.notStarted) {
            throw new Error("Loader already called");
        }
        completionStatus = CompletionStatus.loading;

        entiries = new ArrayList<>();

        LocalFileHeader mimetype = readLocalFileHeader();
        if (validateMimetype(mimetype)) {
            entiries.add(mimetype);
        } else {
            throw new Error("Unexpected MIME type");
        }

        for (;;) {
            LocalFileHeader header = readLocalFileHeader();
            if (header != null) {
                entiries.add(header);
            } else {
                break;
            }
        }

        final PackagingDescriptor packagingDescriptor = createPackagingDescriptor();

        if (packagingDescriptor.getManifestItems().length() == 0) {
            throw new Error("Packaging Descriptor doesn't specify any item in manifest.");
        }

        book = new Book(packagingDescriptor, this);
        Scheduler.get().scheduleIncremental(new RepeatingCommand() {

            private int counter = -1;

            @Override
            public boolean execute() {
                ManifestItem item = packagingDescriptor.getManifestItems().get(++counter);
                try {
                    book.addContentItem(item.getId(), inflateContent(item, packagingDescriptor.getPackageDirectory()));
                    logger.log(Level.INFO, "Content Item [" + item.getId() + "] inflated and added to a book.");
                } catch (Throwable t) {
                    completionStatus = CompletionStatus.failed;
                    logger.log(Level.SEVERE, "Content Item [" + item.getId() + "] failed to inflate.", t);
                    return false;
                }
                if (counter == (packagingDescriptor.getManifestItems().length() - 1)) {
                    completionStatus = CompletionStatus.completed;
                    return false;
                } else {
                    return true;
                }
            }
        });

        return book;
    }

    @Override
    public void addRequestedContentItem(String itemId) {
        // TODO Define sequence of inflate calls according to the sequence of requests to Book 

    }

    private PackagingDescriptor createPackagingDescriptor() {

        PackagingDescriptor packagingDescriptor = PackagingDescriptor.create();

        String packagingDescriptorFileName = extractPackagingDescriptorFileName();
        packagingDescriptor.setPackageDirectory(packagingDescriptorFileName.substring(0, packagingDescriptorFileName.lastIndexOf('/') + 1));

        String packagingDescriptorXml = inflatePackagingDescriptor(packagingDescriptorFileName);

        Document document = new XmlParser().parse(packagingDescriptorXml,
                "xmlns:dns=\"http://www.idpf.org/2007/opf\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");

        //========== metadata =============//

        String pubIdMeta = ((HasText) document.selectNode("/dns:package/@unique-identifier")).getText();
        String pubId = ((HasText) document.selectNode("/dns:package/dns:metadata/dc:identifier[@id=\"" + pubIdMeta + "\"]/text()")).getText();
        String modified = ((HasText) document.selectNode("/dns:package/dns:metadata/dns:meta[@property=\"dcterms:modified\"]/text()")).getText();
        packagingDescriptor.setBookId(pubId, modified, System.currentTimeMillis() + "");

        String title = ((HasText) document.selectNodes("/dns:package/dns:metadata/dc:title/text()").get(0)).getText();
        packagingDescriptor.setTitle(title);

        //========== manifest =============//

        List<Node> itemNodes = document.selectNodes("/dns:package/dns:manifest/dns:item");

        JsArray<ManifestItem> manifestItems = JsArray.createArray().<JsArray<ManifestItem>> cast();
        for (Node node : itemNodes) {

            JsArrayString properties = JsArrayString.createArray().cast();
            String propertiesString = node.selectNode("@properties") == null ? null : ((HasText) node.selectNode("@properties")).getText();
            if (propertiesString != null) {
                String[] tokens = propertiesString.split("\\s+");
                for (String string : tokens) {
                    properties.push(string);
                }
            }

            ManifestItem item = ManifestItem.create( //
                    ((HasText) node.selectNode("@id")).getText(), //
                    ((HasText) node.selectNode("@href")).getText(), //
                    node.selectNode("@media-type") == null ? null : ((HasText) node.selectNode("@media-type")).getText(), //
                    properties);
            manifestItems.push(item);

            for (int i = 0; i < properties.length(); i++) {
                switch (properties.get(i)) {
                case "nav":
                    packagingDescriptor.setNavItem(item);
                    break;
                case "cover-image":
                    packagingDescriptor.setCoverImageItem(item);
                    break;
                default:
                    break;
                }
            }

        }
        packagingDescriptor.setManifestItems(manifestItems);

        //========== spine =============//

        List<Node> refNodes = document.selectNodes("/dns:package/dns:spine/dns:itemref");

        JsArray<SpineItem> itemRefs = JsArray.createArray().<JsArray<SpineItem>> cast();
        for (Node node : refNodes) {
            itemRefs.push(SpineItem.create( //
                    ((HasText) node.selectNode("@idref")).getText()));
        }
        packagingDescriptor.setSpineItems(itemRefs);

        return packagingDescriptor;
    }

    private String extractPackagingDescriptorFileName() {
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if (CONTAINER_LOCATION.equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Container Descriptor is not found");
        }
        String xml = ByteUtils.toString(inflateLocalFile(header));

        Document document = new XmlParser().parse(xml, "xmlns:dns=\"urn:oasis:names:tc:opendocument:xmlns:container\"");

        return ((HasText) document.selectNode("/dns:container/dns:rootfiles/dns:rootfile/@full-path")).getText();
    }

    private String inflatePackagingDescriptor(String packagingDescriptorLocation) {
        if (packagingDescriptorLocation == null) {
            throw new Error("Package Descriptor location is null");
        }
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if (packagingDescriptorLocation.equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Packaging Descriptor is not found at [" + packagingDescriptorLocation + "]");
        }
        return ByteUtils.toString(inflateLocalFile(header));
    }

    private String inflateContent(ManifestItem item, String bookDirectory) {
        String path = bookDirectory + item.getHref();
        LocalFileHeader header = null;
        for (LocalFileHeader h : entiries) {
            if (path.equals(h.name)) {
                header = h;
                break;
            }
        }
        if (header == null) {
            throw new Error("Contant is not found");
        }

        Int8Array data = inflateLocalFile(header);
        switch (item.getMediaType()) {
        case "application/xhtml+xml":
        case "application/x-dtbncx+xml":
        case "text/css":
            return ByteUtils.toString(data);

        case "image/jpeg":
        case "application/font-woff":
        case "application/vnd.ms-opentype":
            return ByteUtils.toString(Base64Encoder.encode(data));
        default:
            throw new Error("Unsupported media type [" + item.getMediaType() + "]");
        }
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

    @Override
    public CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

}
