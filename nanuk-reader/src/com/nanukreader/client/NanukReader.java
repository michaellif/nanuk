package com.nanukreader.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    private HTML viewer;

    @Override
    public void onModuleLoad() {
        viewer = new HTML();
        RootPanel.get().add(viewer);

        getEpub("http://localhost:8888/test.epub");

    }

    private void getEpub(String url) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    System.err.println("Couldn't retrieve file");
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        viewer.setText(response.getText());
                        System.out.println(response.getText());
                    } else {
                        System.err.println("Couldn't retrieve file (" + response.getStatusText() + ")");
                    }
                }
            });
        } catch (RequestException e) {
            System.err.println("Couldn't retrieve file");
        }

    }
}
