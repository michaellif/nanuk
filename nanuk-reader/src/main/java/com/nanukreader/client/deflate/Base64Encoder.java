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
 * Created on Feb 4, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.deflate;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.Int8Array;

public class Base64Encoder {

    private static final byte[] ENCODE_TABLE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    protected static final byte padding = '=';

    public static Int8Array encode(Int8Array data) {
        int modulus = data.length() % 3;
        int dataLength = (data.length() - modulus);

        int encodedLength = (dataLength / 3) * 4 + ((modulus == 0) ? 0 : 4);
        Int8Array out = Int8ArrayNative.create(encodedLength);

        int a1, a2, a3;

        int j = 0;
        for (int i = 0; i < dataLength; i += 3) {
            a1 = data.get(i) & 0xff;
            a2 = data.get(i + 1) & 0xff;
            a3 = data.get(i + 2) & 0xff;

            out.set(j++, ENCODE_TABLE[(a1 >>> 2) & 0x3f]);
            out.set(j++, ENCODE_TABLE[((a1 << 4) | (a2 >>> 4)) & 0x3f]);
            out.set(j++, ENCODE_TABLE[((a2 << 2) | (a3 >>> 6)) & 0x3f]);
            out.set(j++, ENCODE_TABLE[a3 & 0x3f]);
        }

        /*
         * process the tail end.
         */
        int b1, b2, b3;
        int d1, d2;

        switch (modulus) {
        case 0: /* nothing left to do */
            break;
        case 1:
            d1 = data.get(dataLength) & 0xff;
            b1 = (d1 >>> 2) & 0x3f;
            b2 = (d1 << 4) & 0x3f;

            out.set(j++, ENCODE_TABLE[b1]);
            out.set(j++, ENCODE_TABLE[b2]);
            out.set(j++, padding);
            out.set(j++, padding);
            break;
        case 2:
            d1 = data.get(dataLength) & 0xff;
            d2 = data.get(dataLength + 1) & 0xff;

            b1 = (d1 >>> 2) & 0x3f;
            b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
            b3 = (d2 << 2) & 0x3f;

            out.set(j++, ENCODE_TABLE[b1]);
            out.set(j++, ENCODE_TABLE[b2]);
            out.set(j++, ENCODE_TABLE[b3]);
            out.set(j++, padding);
            break;
        }

        return out;
    }
}
