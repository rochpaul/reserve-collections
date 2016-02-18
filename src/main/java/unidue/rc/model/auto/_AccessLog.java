package unidue.rc.model.auto;

import org.apache.cayenne.CayenneDataObject;

/**
 * Class _AccessLog was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _AccessLog extends CayenneDataObject {

    public static final String ACTION_PROPERTY = "action";
    public static final String REMOTE_HOST_PROPERTY = "remoteHost";
    public static final String RESOURCE_PROPERTY = "resource";
    public static final String RESOURCE_ID_PROPERTY = "resourceID";
    public static final String TIMESTAMP_PROPERTY = "timestamp";
    public static final String USER_AGENT_PROPERTY = "userAgent";

    public static final String ID_PK_COLUMN = "id";

    public void setAction(String action) {
        writeProperty(ACTION_PROPERTY, action);
    }
    public String getAction() {
        return (String)readProperty(ACTION_PROPERTY);
    }

    public void setRemoteHost(String remoteHost) {
        writeProperty(REMOTE_HOST_PROPERTY, remoteHost);
    }
    public String getRemoteHost() {
        return (String)readProperty(REMOTE_HOST_PROPERTY);
    }

    public void setResource(String resource) {
        writeProperty(RESOURCE_PROPERTY, resource);
    }
    public String getResource() {
        return (String)readProperty(RESOURCE_PROPERTY);
    }

    public void setResourceID(Integer resourceID) {
        writeProperty(RESOURCE_ID_PROPERTY, resourceID);
    }
    public Integer getResourceID() {
        return (Integer)readProperty(RESOURCE_ID_PROPERTY);
    }

    public void setTimestamp(Long timestamp) {
        writeProperty(TIMESTAMP_PROPERTY, timestamp);
    }
    public Long getTimestamp() {
        return (Long)readProperty(TIMESTAMP_PROPERTY);
    }

    public void setUserAgent(String userAgent) {
        writeProperty(USER_AGENT_PROPERTY, userAgent);
    }
    public String getUserAgent() {
        return (String)readProperty(USER_AGENT_PROPERTY);
    }

}
