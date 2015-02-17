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
 * Created on Feb 16, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.nanukreader.client;

import com.nanukreader.client.cfi.CfiParser;

public class CfiParserTest extends AbstractCfiTest {

    public void testItemIdExtractionFromCfi() {
        String itemId = CfiParser.getItemIdFromCfi("/6/20[xchapter_004]!/4/2[test6]/6");
        assertEquals("xchapter_004", itemId);
    }

    public void testParseCfi_1() {
        CfiParserValidator validator = new CfiParserValidator(new Object[][] { { 4, null }, { 2, "[test6]" }, { 6, null } });
        new CfiParser(new CfiTestContentHandler(validator), null).parse("/4/2[test6]/6");
        validator.assertComplete();
    }

    public void testParseCfi_2() {
        CfiParserValidator validator = new CfiParserValidator(new Object[][] { { 4, null }, { 2, "[test6]" }, { 8, null }, { 20, null } });
        new CfiParser(new CfiTestContentHandler(validator), null).parse("/4/2[test6]/8/20");
        validator.assertComplete();
    }
}