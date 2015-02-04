package com.nanukreader.client.io;

import java.io.IOException;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.nanukreader.client.ByteUtils;

public class ByteArrayOutputStream extends OutputStream {

    protected Int8Array buf;

    protected int count;

    public ByteArrayOutputStream() {
        this(32);
    }

    public ByteArrayOutputStream(int size) {
        this.buf = Int8ArrayNative.create(size);
    }

    @Override
    public void write(int b) {
        ensureCapacity(this.count + 1);
        this.buf.set(this.count++, (byte) b);
    }

    @Override
    public void write(Int8Array buf, int off, int len) {
        ensureCapacity(this.count + len);
        ByteUtils.arraycopy(buf, off, this.buf, this.count, len);
        this.count += len;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    public void reset() {
        this.count = 0;
    }

    public Int8Array toByteArray() {
        Int8Array data = Int8ArrayNative.create(this.count);
        ByteUtils.arraycopy(this.buf, 0, data, 0, this.count);
        return data;
    }

    public int size() {
        return this.count;
    }

    private void ensureCapacity(int len) {
        if (len <= this.buf.length())
            return;
        len = Math.max(len, this.buf.length() * 2);
        Int8Array newbuf = Int8ArrayNative.create(len);
        ByteUtils.arraycopy(this.buf, 0, newbuf, 0, this.buf.length());
        this.buf = newbuf;
    }
}
