/**
 * Copyright (C) 2014 - 2016 Universitaet Duisburg-Essen (semapp|uni-due.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unidue.rc.system;


import miless.model.User;
import org.apache.cayenne.di.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unidue.rc.model.Entry;
import unidue.rc.model.ReserveCollection;
import unidue.rc.model.Resource;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by nils on 13.07.15.
 */
public class BaseURLServiceImpl implements BaseURLService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseURLServiceImpl.class);

    private static final String URL_PATH_DIVIDER = "/";

    @Inject
    private SystemConfigurationService config;

    @Override
    public String getBaseURL() {

        return String.format("%s://%s%s",
                getProtocol(),
                getHostname(),
                getPort());
    }

    @Override
    public String getApplicationURL() {
        return String.format("%s://%s%s%s",
                getProtocol(),
                getHostname(),
                getPort(),
                getApplicationPath());
    }

    @Override
    public String getViewCollectionURL(ReserveCollection collection) {
        return String.format("%s/collection/view/%d",
                getApplicationURL(),
                collection.getId());
    }

    @Override
    public String getEntryLink(Entry entry) {
        return String.format("%s#%d", getViewCollectionURL(entry.getReserveCollection()), entry.getId());
    }

    @Override
    public String getEditUserLink(User user) {
        return String.format("%s/user/index/%d",
                getApplicationURL(),
                user.getId());
    }

    @Override
    public String getDownloadLink(Resource resource) throws URISyntaxException {
        return getDownloadLink(resource, DownloadMethod.Attachment);
    }

    @Override
    public String getDownloadLink(Resource resource, DownloadMethod method) throws URISyntaxException {
        try {
            URIBuilder builder = new URIBuilder(getApplicationURL());

            switch (method) {
                case Inline:
                        builder.setPath(String.format("%s/entry/download/%d/inline/%s",
                                builder.getPath(),
                                resource.getId(),
                                resource.getFileName()));
                    break;
                case Attachment:
                default:
                    builder.setPath(String.format("%s/entry/download/%d/attachment",
                            builder.getPath(),
                            resource.getId()));
                    break;
            }
            URI uri = builder.build();
            return uri.toString();
        } catch (URISyntaxException e) {
            LOG.error("could not build download link for resource " + resource, e);
            throw e;
        }
    }

    @Override
    public String getProlongLink(ReserveCollection collection) {
        return String.format("%s/collection/prolong/%d/%s",
                getApplicationURL(),
                collection.getId(),
                collection.getProlongCode());
    }

    /**
     * Retrieves the application path which is used by the web container to address the web application
     * if one is needed, an empty string otherwise.
     */
    private String getApplicationPath() {
        String webApplicationPath = config.getString("web.application.path");
        if (StringUtils.isEmpty(webApplicationPath))
            return StringUtils.EMPTY;

        StringBuilder resultPath = new StringBuilder(webApplicationPath);
        if (!webApplicationPath.startsWith(URL_PATH_DIVIDER))
            resultPath.insert(0, URL_PATH_DIVIDER);

        if (webApplicationPath.endsWith("/"))
            resultPath.deleteCharAt(resultPath.length() - 1);

        return resultPath.toString();
    }

    /**
     * Retrieves the port suffix for this base url. The suffix should be empty if the port is 80 or 443 as these
     * are by default interpreted by the browser.
     *
     * @return the port
     */
    protected String getPort() {
        int port = config.getInt("server.port");
        return port == 80 || port == 443
               ? ""
               : ":" + port;
    }

    /**
     * Returns the host name which can be used to address the application for example by a web browser.
     *
     * @return the hostname
     */
    protected String getHostname() {
        return config.getString("server.name");
    }

    /**
     * Returns the protocol which can be used to address the application for example by a web browser.
     *
     * @return the protocol
     */
    protected String getProtocol() {
        return config.getString("server.protocol");
    }
}
