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
 * Created on Feb 10, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import com.google.gwt.core.client.JavaScriptObject;

public final class ManifestItem extends JavaScriptObject {

    protected ManifestItem() {
    }

    public final native String getId() /*-{
		return this.bookId;
    }-*/;

    protected final native void setId(String id) /*-{
		this.id = id;
    }-*/;

    public final native String getHref() /*-{
		return this.href;
    }-*/;

    protected final native void setHref(String href) /*-{
		this.href = href;
    }-*/;

    public final native String getMediaType() /*-{
		return this.mediaType;
    }-*/;

    protected final native void setMediaType(String mediaType) /*-{
		this.mediaType = mediaType;
    }-*/;

    public final native String getProperties() /*-{
		return this.properties;
    }-*/;

    protected final native void setProperties(String properties) /*-{
		this.properties = properties;
    }-*/;

    public static final ManifestItem create(String id, String href, String mediaType, String properties) {
        ManifestItem item = (ManifestItem) JavaScriptObject.createObject().cast();
        item.setId(id);
        item.setHref(href);
        item.setMediaType(mediaType);
        item.setProperties(properties);
        return item;
    }

}
