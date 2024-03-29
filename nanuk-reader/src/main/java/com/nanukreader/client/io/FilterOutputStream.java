/*
 * Copyright (C) 2009 Archie L. Cobbs. All rights reserved.
 *
 * $Id: FilterOutputStream.java 2 2009-12-17 02:39:56Z archie.cobbs $
 */

package com.nanukreader.client.io;

import java.io.IOException;

import com.google.gwt.typedarrays.shared.Int8Array;

public class FilterOutputStream extends OutputStream {

    protected OutputStream out;

    public FilterOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void write(Int8Array buf) throws IOException {
        this.out.write(buf);
    }

    @Override
    public void write(Int8Array buf, int off, int len) throws IOException {
        this.out.write(buf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
