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
package com.nanukreader.client;

import com.google.gwt.typedarrays.shared.Int8Array;

public class ByteUtils {

    public static char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String toHexString(byte[] bytes) {
        char[] hexString = new char[2 * bytes.length];
        int j = 0;
        for (int i = 0; i < bytes.length; i++) {
            hexString[j++] = HEX_CHARS[(bytes[i] & 0xF0) >> 4];
            hexString[j++] = HEX_CHARS[bytes[i] & 0x0F];
        }
        return new String(hexString);
    }

    public static void print(Int8Array array) {
        for (int i = 0; i < array.byteLength(); i++) {
            System.out.print(ByteUtils.toHexString(new byte[] { array.get(i) }) + " ");
        }
        System.out.println("");
    }

    public static int toInt(Int8Array array) {
        int intVal = 0;
        for (int i = 0; i < 4; i++) {
            int b = array.get(i) & 0xFF;
            intVal |= b << (i * 8);
        }
        return intVal;
    }

    public static short toShort(Int8Array array) {
        short shortVal = 0;
        for (int i = 0; i < 2; i++) {
            int b = array.get(i);
            shortVal |= b << (i * 8);
        }
        return shortVal;
    }

    public static String toString(Int8Array array) {
        char[] chars = new char[array.byteLength() / 2];
        for (int i = 0; i < array.byteLength() / 2; i++) {
            chars[i] = (char) ((char) array.get(i) | (char) (array.get(i + 1) >> 8));
        }
        return String.valueOf(chars);
    }

    public static String toStringUtf(Int8Array array) {
        char[] chars = new char[array.byteLength()];
        for (int i = 0; i < array.byteLength(); i++) {
            chars[i] = (char) array.get(i);
        }
        return String.valueOf(chars);
    }
}
