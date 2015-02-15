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
 * Created on Feb 15, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client.cfi;

import java.util.logging.Logger;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class CfiParser {

    private static final Logger logger = Logger.getLogger(CfiParser.class.getName());

    private static final RegExp LOCAL_PATH_PATTERN = RegExp.compile(".*!(.*)");

    private static final RegExp STEP_PATTERN = RegExp.compile("(\\/(\\d+)(\\[.*\\])?)", "g");

    private static final RegExp ITEM_ID_PATTERN = RegExp.compile(".*\\[(.*)\\]!.*");

    private final CfiContentHandler contentHandler;

    private final CfiErrorHandler errorHandler;

    public CfiParser(CfiContentHandler contentHandler, CfiErrorHandler errorHandler) {
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
    }

    public void parse(String cfi) {
        MatchResult localPathMatcher = LOCAL_PATH_PATTERN.exec(cfi);

        String localPath = localPathMatcher.getGroup(1);

        for (MatchResult stepMatcher = STEP_PATTERN.exec(localPath); stepMatcher != null; stepMatcher = STEP_PATTERN.exec(localPath)) {
            String position = stepMatcher.getGroup(2);
            String assertion = stepMatcher.getGroup(3);
            contentHandler.step(Integer.valueOf(position), assertion);
        }

        //logger.log(Level.SEVERE, "++++++++++++++ TP2 " + position + " " + assertion);

        contentHandler.complete();
    }

    public static String getItemIdFromCfi(String cfi) {
        MatchResult matcher = ITEM_ID_PATTERN.exec(cfi);
        return matcher.getGroup(1);
    }

}
