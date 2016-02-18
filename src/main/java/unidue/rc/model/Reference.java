package unidue.rc.model;

/*
 * #%L
 * Semesterapparate
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 Universitaet Duisburg Essen
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

import org.apache.commons.lang3.StringUtils;
import unidue.rc.model.auto._Reference;

public class Reference extends _Reference implements EntryValue, IntPrimaryKey, ResourceContainer, CollectionVisitable {

    @Override
    public void accept(CollectionVisitor visitor) {
        visitor.visit(this);

        Resource resource = getResource();
        if (resource != null)
            resource.accept(visitor);

        visitor.didVisit(this);
    }

    @Override
    public Integer getId() {
        return CayenneUtils.getID(this, ID_PK_COLUMN);
    }

    public String getYearOfPublicationAsString() {
        return getYearOfPublication() != null
                ? getYearOfPublication().toString()
                : StringUtils.EMPTY;
    }
}
