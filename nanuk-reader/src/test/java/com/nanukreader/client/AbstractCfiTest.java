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

import com.nanukreader.client.cfi.CfiContentHandler;

public abstract class AbstractCfiTest extends AbstractNanukReaderTest {

    class CfiTestContentHandler implements CfiContentHandler {

        private final CfiParserValidator validator;

        public CfiTestContentHandler(CfiParserValidator validator) {
            this.validator = validator;
        }

        @Override
        public void step(int position, String assertion) {
            validator.validateStep(position, assertion);
        }

        @Override
        public void complete() {
            validator.setCompletion();
        }
    }

    class CfiParserValidator {

        Object[][] values;

        int stepCount = 0;

        boolean complete = false;

        public CfiParserValidator(Object[][] values) {
            this.values = values;
        }

        void validateStep(int position, String assertion) {
            assertEquals(values[stepCount][0], position);
            assertEquals(values[stepCount++][1], assertion);
        }

        public void setCompletion() {
            complete = true;
        }

        public void assertComplete() {
            assertTrue(complete);
            assertEquals(values.length, stepCount);
        }
    }

}
