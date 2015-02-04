package com.nanukreader.client.deflate;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.Int8Array;

final class CircularDictionary {

    private final Int8Array data;

    private int index;

    private int mask;

    public CircularDictionary(int size) {
        data = Int8ArrayNative.create(size);
        index = 0;

        if (size > 0 && (size & (size - 1)) == 0) // Test if size is a power of 2
            mask = size - 1;
        else
            mask = 0;
    }

    public void append(int b) {
        data.set(index, (byte) b);

        if (mask != 0) {
            index = (index + 1) & mask;
        } else {
            index = (index + 1) % data.length();
        }
    }

    public void copy(int dist, int len, OutputStream out) throws IOException {
        if (len < 0 || dist < 1 || dist > data.length())
            throw new IllegalArgumentException();

        if (mask != 0) {
            int readIndex = (index - dist + data.length()) & mask;
            for (int i = 0; i < len; i++) {
                out.write(data.get(readIndex));
                data.set(index, data.get(readIndex));
                readIndex = (readIndex + 1) & mask;
                index = (index + 1) & mask;
            }
        } else {
            int readIndex = (index - dist + data.length()) % data.length();
            for (int i = 0; i < len; i++) {
                out.write(data.get(readIndex));
                data.set(index, data.get(readIndex));
                readIndex = (readIndex + 1) % data.length();
                index = (index + 1) % data.length();
            }
        }
    }

}
