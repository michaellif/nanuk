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

    public static String toString(Int8Array array) {
        byte[] bytes = new byte[array.byteLength()];
        for (int i = 0; i < array.byteLength(); i++) {
            bytes[i] = array.get(i);
        }
        return new String(bytes);
    }

    public static String toStringUtf(Int8Array array) {
        char[] chars = new char[array.byteLength()];
        for (int i = 0; i < array.byteLength(); i++) {
            chars[i] = (char) array.get(i);
        }
        return String.valueOf(chars);
    }

    public static String toHexString(Int8Array bytes) {
        char[] hexString = new char[2 * bytes.length()];
        int j = 0;
        for (int i = 0; i < bytes.length(); i++) {
            hexString[j++] = HEX_CHARS[(bytes.get(i) & 0xF0) >> 4];
            hexString[j++] = HEX_CHARS[bytes.get(i) & 0x0F];
        }
        return new String(hexString);
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
            int b = array.get(i) & 0xFF;
            shortVal |= b << (i * 8);
        }
        return shortVal;
    }

    public static short toBase64(Int8Array array) {
        short shortVal = 0;
        for (int i = 0; i < 2; i++) {
            int b = array.get(i) & 0xFF;
            shortVal |= b << (i * 8);
        }
        return shortVal;
    }

    public static String toStringBinary(int i) {
        String b = Integer.toBinaryString(i);

        if (b.length() < 8) {
            b = "000000000".substring(0, 8 - b.length()).concat(b);
        } else {
            b = b.substring(b.length() - 8);
        }
        return b;
    }

    public static String mirror(String in) {

        StringBuilder builder = new StringBuilder();

        for (int i = in.length() - 1; i >= 0; i--) {
            builder.append(in.charAt(i));
        }

        return builder.toString();
    }

    public static void arraycopy(Int8Array src, int srcPos, Int8Array dest, int destPos, int length) {
        for (int i = 0; i < length; i++) {
            dest.set(i, src.get(srcPos + i));
        }
    }
}
