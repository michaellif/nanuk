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
 * Created on Feb 7, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import com.google.gwt.core.client.JavaScriptObject;

public final class Record extends JavaScriptObject {

    protected Record() {
    }

    public final native String getBookId() /*-{
		return this.bookId;
    }-*/;

    public final native void setBookId(String bookId) /*-{
		this.bookId = bookId;
    }-*/;

    public native Record deepCopy()/*-{
		return JSON.parse(JSON.stringify(this));
    }-*/;

    public static final Record create() {
        return (Record) JavaScriptObject.createObject().cast();
    }

}
