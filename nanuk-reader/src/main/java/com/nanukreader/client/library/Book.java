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
 * Created on Feb 2, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nanukreader.client.loader.IBookLoader;

public class Book {

    private final IBookLoader bookLoader;

    private final PackagingDescriptor packagingDescriptor;

    /**
     * key - itemId, value - content
     */
    private final Map<String, String> loadedContentItems;

    /**
     * key - itemId, value - completion callback
     */
    private final Map<String, AsyncCallback<String>> requestedContentItems;

    private final List<String> manifestItemIdList;

    private final List<String> spineItemIdList;

    private ManifestItem navItem;

    private ManifestItem coverImageItem;

    public Book(PackagingDescriptor packagingDescriptor, IBookLoader bookLoader) {
        this.packagingDescriptor = packagingDescriptor;

        manifestItemIdList = new ArrayList<>();
        for (int i = 0; i < packagingDescriptor.getManifestItems().length(); i++) {
            ManifestItem item = packagingDescriptor.getManifestItems().get(i);
            if (item.getProperties() != null) {
                for (int j = 0; j < item.getProperties().length(); j++) {
                    switch (item.getProperties().get(j)) {
                    case "nav":
                        navItem = item;
                        break;
                    case "cover-image":
                        coverImageItem = item;
                        break;
                    default:
                        break;
                    }
                }
            }
            manifestItemIdList.add(item.getId());
        }

        spineItemIdList = new ArrayList<>();
        for (int i = 0; i < packagingDescriptor.getSpineItems().length(); i++) {
            spineItemIdList.add(packagingDescriptor.getSpineItems().get(i).getIdref());
        }

        this.bookLoader = bookLoader;
        loadedContentItems = new HashMap<>();
        requestedContentItems = new HashMap<>();
    }

    public String getBookId() {
        return packagingDescriptor.getBookId();
    }

    public String getTitle() {
        return packagingDescriptor.getTitle();
    }

    public PackagingDescriptor getPackagingDescriptor() {
        return packagingDescriptor.deepCopy();
    }

    public void addContentItem(String itemId, String content) {
        loadedContentItems.put(itemId, content);
        AsyncCallback<String> callback = requestedContentItems.get(itemId);
        if (callback != null) {
            callback.onSuccess(content);
        }
    }

    public void getContentItem(String itemId, AsyncCallback<String> callback) {
        String contentItem = loadedContentItems.get(itemId);
        if (contentItem != null) {
            callback.onSuccess(contentItem);
        } else if (verifyItemId(itemId)) { // itemId is correct but content is not loaded yet
            requestedContentItems.put(itemId, callback);
            bookLoader.addRequestedContentItem(itemId);
        } else {
            throw new Error("[" + itemId + "] is not found");
        }
    }

    public boolean verifyItemId(String itemId) {
        return manifestItemIdList.contains(itemId);
    }

    public String getPreviousSpineItemId(String itemId) {
        int itemIndex = spineItemIdList.indexOf(itemId);
        if (itemIndex == -1) {
            throw new Error("The item [" + itemId + "] is not found in spine");
        } else if (itemIndex == 0) {
            return null;
        } else {
            return spineItemIdList.get(itemIndex - 1);
        }
    }

    public String getNextSpineItemId(String itemId) {
        int itemIndex = spineItemIdList.indexOf(itemId);
        if (itemIndex == -1) {
            throw new Error("The item [" + itemId + "] is not found in spine");
        } else if (itemIndex == spineItemIdList.size() - 1) {
            return null;
        } else {
            return spineItemIdList.get(itemIndex + 1);
        }
    }

    public int getSpineItemIndex(String itemId) {
        return spineItemIdList.indexOf(itemId);
    }

    public ManifestItem getNavItem() {
        return navItem;
    }

    public ManifestItem getCoverImageItem() {
        return coverImageItem;
    }
}
