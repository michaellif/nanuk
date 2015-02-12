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
 * Created on Feb 8, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class PackagingDescriptor extends JavaScriptObject {

    public static final String PACKAGE_ID_SEPARATOR = "@";

    protected PackagingDescriptor() {
    }

    public final native String getBookId() /*-{
		return this.bookId;
    }-*/;

    protected final native void setBookId(String bookId) /*-{
		this.bookId = bookId;
    }-*/;

    public final native String getPackageDirectory() /*-{
		return this.packageDirectory;
    }-*/;

    public final native void setPackageDirectory(String packageDirectory)/*-{
		this.packageDirectory = packageDirectory;
    }-*/;

    public final native String getTitle() /*-{
		return this.title;
    }-*/;

    public final native void setTitle(String title) /*-{
		this.title = title;
    }-*/;

    public void setBookId(String uuid, String modifyedTimestamp, String addedTimestamp) {
        setBookId(uuid + PACKAGE_ID_SEPARATOR + modifyedTimestamp + PACKAGE_ID_SEPARATOR + addedTimestamp);
    }

    public native PackagingDescriptor deepCopy()/*-{
		return JSON.parse(JSON.stringify(this));
    }-*/;

    public static final PackagingDescriptor create() {
        return (PackagingDescriptor) JavaScriptObject.createObject().cast();
    }

    public final native void setManifestItems(JsArray<ManifestItem> manifestItems) /*-{
		this.manifestItems = manifestItems;
    }-*/;

    public final native JsArray<ManifestItem> getManifestItems() /*-{
		return this.manifestItems;
    }-*/;

    public final native void setItemRefs(JsArray<ItemRef> itemRefs) /*-{
		this.itemRefs = itemRefs;
    }-*/;

    public final native JsArray<ItemRef> getItemRefs() /*-{
		return this.itemRefs;
    }-*/;

    public final native void setNavItem(ManifestItem navItem) /*-{
		this.navItem = navItem;
    }-*/;

    public final native ManifestItem getNavItem() /*-{
		return this.navItem;
    }-*/;

    public final native void setCoverImageItem(ManifestItem coverImageItem) /*-{
		this.coverImageItem = coverImageItem;
    }-*/;

    public final native ManifestItem getCoverImageItem() /*-{
		return this.coverImageItem;
    }-*/;
}
