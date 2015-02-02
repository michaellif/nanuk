package com.nanukreader.client.ocf;

import com.google.gwt.typedarrays.shared.Int8Array;
import com.nanukreader.client.ByteUtils;

interface ZipConstants {

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
            ByteUtils.print(array.subarray(0, 4));
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
        VERSION(4), BIT_FLAG(6), COMPRESSION_METHOD(8), TIME_DATE(10), CRC(14), COMPRESSED_SIZE(18), UNCOMPRESSED_SIZE(22), NAME_LENGTH(26), EXTRA_LENGTH(28);

        private final int offset;

        LocalFileFieldOffset(int offset) {
            this.offset = offset;
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
