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
 * Created on Feb 14, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.bookviewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.nanukreader.client.Callback;
import com.nanukreader.client.library.Book;

public class BookViewer extends FlowPanel {

    private static final Logger logger = Logger.getLogger(ContentViewport.class.getName());

    private static final int COLUMN_GAP = 10;

    public static enum PageViewType {

        single, sideBySide, auto;

    }

    public static enum PageOrientation {

        portrait, landscape, auto;

    }

    public static enum PageTurnEffectType {

        flip, slide, shift, fade, none;

    }

    private final ContentViewport contentViewport;

    private final SimplePanel coverViewer;

    private final ScrollPanel navViewer;

    private PageLocation currentPageLocation;

    private final UserPreferences userPreferences;

    private Book book;

    public BookViewer() {

        userPreferences = new UserPreferences();

        contentViewport = new ContentViewport(this);
        contentViewport.setPixelSize(610, 450);
        contentViewport.getElement().getStyle().setProperty("margin", "50px 10%");
        add(contentViewport);

        HorizontalPanel bottomPanel = new HorizontalPanel();
        add(bottomPanel);

        coverViewer = new SimplePanel();
        coverViewer.setPixelSize(300, 450);
        bottomPanel.add(coverViewer);

        navViewer = new ScrollPanel();
        navViewer.setPixelSize(300, 450);
        bottomPanel.add(navViewer);

        HorizontalPanel toolbar = new HorizontalPanel();
        bottomPanel.add(toolbar);

        toolbar.add(new Button("Back", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                previousPage();
            }
        }));

        toolbar.add(new Button("Next", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                nextPage();
            }
        }));

        HorizontalPanel terminalToolbar = new HorizontalPanel();
        if (false) {
            bottomPanel.add(terminalToolbar);
        }

        for (int i = 0; i < 7; i++) {
            final int terminalNumber = i;
            final ToggleButton button = new ToggleButton(i + "");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (button.isDown()) {
                        contentViewport.revealTerminal(terminalNumber);
                    } else {
                        contentViewport.concealTerminal(terminalNumber);
                    }
                }
            });
            terminalToolbar.add(button);

            button.getElement().getStyle().setBackgroundColor("hsl(" + terminalNumber * 40 + ", 50%, 50%)");
            button.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    contentViewport.revealTerminal(terminalNumber);
                }
            });
            button.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    if (!button.isDown()) {
                        contentViewport.concealTerminal(terminalNumber);
                    }
                }
            });
        }

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        contentViewport.setLayoutManager(new SlideLayoutManager());
    }

    public void openBook(Book book, final String progressCfi) {
        this.book = book;

        logger.log(Level.INFO, "Page [" + progressCfi + "] is loading");
        contentViewport.getPageEstimator().getPageLocation(progressCfi, new AsyncCallback<PageLocation>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, "Can't convert CFI [" + progressCfi + "] to Page Loaction.");
            }

            @Override
            public void onSuccess(PageLocation mainPageLocation) {
                showPage(mainPageLocation);
                logger.log(Level.INFO, "Page [" + mainPageLocation + "] loaded successfully");
            }

        });

        if (book.getCoverImageItem() == null) {
            //TODO generate cover from title
            coverViewer.setWidget(new HTML("No Image"));
        } else {
            book.getContentItem(book.getCoverImageItem().getId(), new Callback<String>() {

                @Override
                public void onCall(String content) {
                    Image image = new Image("data:image/png;base64," + content);
                    image.getElement().getStyle().setProperty("maxHeight", "100%");
                    image.getElement().getStyle().setProperty("maxWidth", "100%");
                    coverViewer.setWidget(image);
                }
            });
        }

        if (book.getNavItem() == null) {
            throw new Error("Book Nav not found");
        } else {
            book.getContentItem(book.getNavItem().getId(), new Callback<String>() {

                @Override
                public void onCall(String content) {
                    navViewer.setWidget(new HTML(content));
                }
            });
        }
    }

    public void nextPage() {
        contentViewport.getPageEstimator().getNextPageLocation(contentViewport.getBookViewer().getCurrentPageLocation(), new Callback<PageLocation>() {

            @Override
            public void onCall(PageLocation nextPageLocation) {
                if (nextPageLocation != null) {
                    showPage(nextPageLocation);
                }
            }
        });
    }

    public void previousPage() {
        contentViewport.getPageEstimator().getPreviousPageLocation(contentViewport.getBookViewer().getCurrentPageLocation(), new Callback<PageLocation>() {

            @Override
            public void onCall(PageLocation previousPageLocation) {
                if (previousPageLocation != null) {
                    showPage(previousPageLocation);
                }
            }
        });
    }

    public Book getBook() {
        return book;
    }

    PageLocation getCurrentPageLocation() {
        return currentPageLocation;
    }

    private void showPage(PageLocation currentPageLocation) {
        if (this.currentPageLocation == currentPageLocation) {
            return;
        }
        this.currentPageLocation = currentPageLocation;
        contentViewport.updateContent();
    }

    public ContentViewport getContentViewport() {
        return contentViewport;
    }

    PageViewType getPageViewType() {
        return userPreferences.getPageViewType();
    }

    PageTurnEffectType getPageTurnEffectType() {
        return userPreferences.getPageTurnEffectType();
    }

    PageOrientation getPageOrientation() {
        return userPreferences.getPageOrientation();
    }

    public int getColumnGap() {
        return COLUMN_GAP;
    }
}
