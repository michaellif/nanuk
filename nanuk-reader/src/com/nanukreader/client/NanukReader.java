package com.nanukreader.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    private HTML viewer;

    @Override
    public void onModuleLoad() {
        FlowPanel contentPanel = new FlowPanel();
        RootPanel.get().add(contentPanel);

        viewer = new HTML();

        Button loadButton = new Button("Load", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String url = "http://127.0.0.1:8888/test.epub";
                openEpub(url, new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        System.err.println(caught.getMessage());
                        viewer.setHTML("ERROR!");
                    }

                    @Override
                    public void onSuccess(String result) {
                        viewer.setHTML(result);
                    }
                });
            }
        });
        contentPanel.add(loadButton);
        contentPanel.add(viewer);

    }

    private void openEpub(String url, final AsyncCallback<String> calback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    calback.onFailure(new Error("Couldn't retrieve file"));
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        calback.onSuccess(response.getText());
                        System.out.println(response.getText());
                    } else {
                        calback.onFailure(new Error("Couldn't retrieve file (" + response.getStatusText() + ")"));
                    }
                }
            });
        } catch (RequestException e) {
            calback.onFailure(new Error("Couldn't retrieve file"));
        }

    }
}
