package unidue.rc.model;

/*
 * #%L
 * Semesterapparate
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Universitaet Duisburg Essen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.cayenne.ExtendedEnumeration;

/**
 * Created by nils on 24.06.15.
 */
public enum MailNodeType implements ExtendedEnumeration {
    RECIPIENT(1),
    REPLY_TO(2),
    CC(3),
    BCC(4);

    private final Integer value;

    MailNodeType(Integer value) {
        this.value = value;
    }

    @Override
    public Object getDatabaseValue() {
        return value;
    }
}
