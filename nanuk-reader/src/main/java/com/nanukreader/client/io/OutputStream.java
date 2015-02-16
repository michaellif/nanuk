/*
 * Copyright (C) 2009 Archie L. Cobbs. All rights reserved.
 *
 * $Id: OutputStream.java 2 2009-12-17 02:39:56Z archie.cobbs $
 */

package com.nanukreader.client.io;

import java.io.IOException;

import com.google.gwt.typedarrays.shared.Int8Array;

public abstract class OutputStream {

    public abstract void write(int b) throws IOException;

    public void write(Int8Array buf) throws IOException {
        write(buf, 0, buf.length());
    }

    public void write(Int8Array buf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++)
            write(buf.get(off + i));
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}
