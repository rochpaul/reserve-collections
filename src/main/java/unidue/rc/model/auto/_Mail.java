package unidue.rc.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import unidue.rc.model.MailNode;

/**
 * Class _Mail was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Mail extends CayenneDataObject {

    public static final String FROM_PROPERTY = "from";
    public static final String MAIL_BODY_PROPERTY = "mailBody";
    public static final String NUM_TRIES_PROPERTY = "numTries";
    public static final String SEND_PROPERTY = "send";
    public static final String SEND_DATE_PROPERTY = "sendDate";
    public static final String SUBJECT_PROPERTY = "subject";
    public static final String TEXT_MAIL_BODY_PROPERTY = "textMailBody";
    public static final String NODES_PROPERTY = "nodes";

    public static final String ID_PK_COLUMN = "id";

    public void setFrom(String from) {
        writeProperty(FROM_PROPERTY, from);
    }
    public String getFrom() {
        return (String)readProperty(FROM_PROPERTY);
    }

    public void setMailBody(String mailBody) {
        writeProperty(MAIL_BODY_PROPERTY, mailBody);
    }
    public String getMailBody() {
        return (String)readProperty(MAIL_BODY_PROPERTY);
    }

    public void setNumTries(int numTries) {
        writeProperty(NUM_TRIES_PROPERTY, numTries);
    }
    public int getNumTries() {
        Object value = readProperty(NUM_TRIES_PROPERTY);
        return (value != null) ? (Integer) value : 0;
    }

    public void setSend(boolean send) {
        writeProperty(SEND_PROPERTY, send);
    }
	public boolean isSend() {
        Boolean value = (Boolean)readProperty(SEND_PROPERTY);
        return (value != null) ? value.booleanValue() : false;
    }

    public void setSendDate(Date sendDate) {
        writeProperty(SEND_DATE_PROPERTY, sendDate);
    }
    public Date getSendDate() {
        return (Date)readProperty(SEND_DATE_PROPERTY);
    }

    public void setSubject(String subject) {
        writeProperty(SUBJECT_PROPERTY, subject);
    }
    public String getSubject() {
        return (String)readProperty(SUBJECT_PROPERTY);
    }

    public void setTextMailBody(String textMailBody) {
        writeProperty(TEXT_MAIL_BODY_PROPERTY, textMailBody);
    }
    public String getTextMailBody() {
        return (String)readProperty(TEXT_MAIL_BODY_PROPERTY);
    }

    public void addToNodes(MailNode obj) {
        addToManyTarget(NODES_PROPERTY, obj, true);
    }
    public void removeFromNodes(MailNode obj) {
        removeToManyTarget(NODES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<MailNode> getNodes() {
        return (List<MailNode>)readProperty(NODES_PROPERTY);
    }


}
