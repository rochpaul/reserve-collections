package unidue.rc.plugins.moodle.model;

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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by nils on 16.06.15.
 */
@Root(name = "get", strict = false)
public class GetCollectionsRequest implements Request {

    @Attribute
    private String username;

    @Attribute
    private String secret;

    @Attribute
    private String authtype;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAuthtype() {
        return authtype;
    }

    public void setAuthtype(String authtype) {
        this.authtype = authtype;
    }
}
