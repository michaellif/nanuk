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

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class BookGrabber {

    public void grab(String url, final AsyncCallback<Int8Array> callback) {
        XMLHttpRequest binxhr = XMLHttpRequest.create();
        binxhr.open("GET", url);
        binxhr.setResponseType("arraybuffer");
        binxhr.send();

        binxhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
            @Override
            public void onReadyStateChange(XMLHttpRequest xhr) {
                XMLHttpRequest binxhr = xhr;
                if (binxhr.getReadyState() == XMLHttpRequest.DONE) {
                    binxhr.clearOnReadyStateChange();
                    ArrayBuffer buffer = binxhr.getResponseArrayBuffer();
                    Int8Array bufferView = Int8ArrayNative.create(buffer);

                    callback.onSuccess(bufferView);
                } else {
                    callback.onFailure(new Error("Failed to read book"));
                }
            }
        });
    }

}
