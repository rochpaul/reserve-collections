package unidue.rc.model.auto;

import org.apache.cayenne.CayenneDataObject;

import unidue.rc.model.Entry;

/**
 * Class _Html was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Html extends CayenneDataObject {

    public static final String TEXT_PROPERTY = "text";
    public static final String ENTRY_PROPERTY = "entry";

    public static final String ID_PK_COLUMN = "id";

    public void setText(String text) {
        writeProperty(TEXT_PROPERTY, text);
    }
    public String getText() {
        return (String)readProperty(TEXT_PROPERTY);
    }

    public void setEntry(Entry entry) {
        setToOneTarget(ENTRY_PROPERTY, entry, true);
    }

    public Entry getEntry() {
        return (Entry)readProperty(ENTRY_PROPERTY);
    }


}
