package com.nanukreader.client.loader;

import com.google.gwt.typedarrays.shared.Int8Array;

public interface ZipConstants {

    static final int EFS = 0x800;

    static final String MIMETYPE_FILE = "mimetype";

    static enum ZipHeader {
        LOC(0x04034b50L, 30), EXT(0x08074b50L, 16), CEN(0x02014b50L, 46), END(0x06054b50L, 22);

        private final long signature;

        private final int headerSize;

        ZipHeader(long signature, int headerSize) {
            this.signature = signature;
            this.headerSize = headerSize;
        }

        public long getSignature() {
            return signature;
        }

        public boolean checkSignature(Int8Array array) {
            for (int i = 0; i < 4; i++) {
                if ((byte) ((signature >> i * 8) & 0xff) != array.get(i)) {
                    return false;
                }
            }
            return true;
        }

        public int getHeaderSize() {
            return headerSize;
        }
    }

    static enum LocalFileFieldOffset {
        SIGNATURE(0, 4), //
        VERSION(4, 2), //
        BIT_FLAG(6, 2), //
        COMPRESSION_METHOD(8, 2), //       
        TIME(10, 2), //
        DATE(12, 2), //
        CRC(14, 4), //
        COMPRESSED_SIZE(18, 4), //
        UNCOMPRESSED_SIZE(22, 4), //
        NAME_LENGTH(26, 2), //
        EXTRA_LENGTH(28, 2);

        private final int length;

        private final int offset;

        LocalFileFieldOffset(int offset, int length) {
            this.offset = offset;
            this.length = length;

        }

        public int getLength() {
            return length;
        }

        public int getOffset() {
            return offset;
        }

    }

    static enum DataDescriptorFieldOffset {
        CRC(4), COMPRESSED_SIZE(8), UNCOMPRESSED_SIZE(12);

        private final int offset;

        DataDescriptorFieldOffset(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }
    }

}
