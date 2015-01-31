package com.nanukreader.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NanukReader implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final Label errorLabel = new Label("TEST");
        RootPanel.get().add(errorLabel);
    }
}
