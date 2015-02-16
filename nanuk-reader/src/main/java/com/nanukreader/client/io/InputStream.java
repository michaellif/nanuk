/*
 * Copyright (C) 2009 Archie L. Cobbs. All rights reserved.
 *
 * $Id: InputStream.java 2 2009-12-17 02:39:56Z archie.cobbs $
 */

package com.nanukreader.client.io;

import java.io.IOException;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.Int8Array;

public abstract class InputStream {

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * <p>
     * A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     * @exception IOException
     *                if an I/O error occurs.
     */
    public abstract int read() throws IOException;

    public int read(Int8Array buf) throws IOException {
        return read(buf, 0, buf.length());
    }

    public int read(Int8Array buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len < 0 || off + len > buf.length())
            throw new IndexOutOfBoundsException();
        if (len == 0)
            return 0;
        int x = read();
        if (x == -1)
            return -1;
        buf.set(off, (byte) x);
        int total = 1;
        while (total < len) {
            try {
                x = read();
            } catch (IOException e) {
                break;
            }
            if (x == -1)
                break;
            buf.set(off + total++, (byte) x);
        }
        return total;
    }

    public long skip(long n) throws IOException {
        Int8Array buf = Int8ArrayNative.create(1024);
        long total = 0;
        while (n > 0) {
            int r = read(buf, 0, Math.min((int) n, buf.length()));
            if (r <= 0)
                break;
            total += r;
            n -= r;
        }
        return total;
    }

    public int available() throws IOException {
        return 0;
    }

    public void close() throws IOException {
    }

    public void mark(int readlimit) {
    }

    public void reset() throws IOException {
        throw new IOException();
    }

    public boolean markSupported() {
        return false;
    }
}
