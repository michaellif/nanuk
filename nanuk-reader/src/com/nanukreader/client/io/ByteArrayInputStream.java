/*
 * Copyright (C) 2009 Archie L. Cobbs. All rights reserved.
 *
 * $Id: ByteArrayInputStream.java 2 2009-12-17 02:39:56Z archie.cobbs $
 */

package com.nanukreader.client.io;

import com.google.gwt.typedarrays.shared.Int8Array;

public class ByteArrayInputStream extends InputStream {

    protected Int8Array buf;

    protected int pos;

    protected int mark;

    protected int count;

    public ByteArrayInputStream(Int8Array buf) {
        this(buf, 0, buf.length());
    }

    public ByteArrayInputStream(Int8Array buf, int off, int len) {
        this.buf = buf;
        this.pos = off;
        this.mark = off;
        this.count = off + len;
        if (this.count > buf.length())
            this.count = buf.length();
    }

    @Override
    public synchronized int read() {
        if (this.pos >= this.count)
            return -1;
        return this.buf.get(this.pos++) & 0xff;

    }

    @Override
    public synchronized int read(Int8Array buf, int off, int len) {
        if (this.pos >= this.count)
            return -1;
        len = Math.min(len, this.count - this.pos);
        System.arraycopy(this.buf, this.pos, buf, off, len);
        this.pos += len;
        return len;
    }

    @Override
    public synchronized long skip(long n) {
        n = Math.min(n, this.count - this.pos);
        this.pos += (int) n;
        return n;
    }

    @Override
    public synchronized int available() {
        return this.count - this.pos;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.mark = this.pos;
    }

    @Override
    public synchronized void reset() {
        this.pos = this.mark;
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
