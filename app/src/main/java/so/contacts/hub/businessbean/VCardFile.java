package so.contacts.hub.businessbean;

public class VCardFile {
    private final String mName;

    private final String mCanonicalPath;

    private final long mLastModified;

    public VCardFile(String name, String canonicalPath, long lastModified) {
        mName = name;
        mCanonicalPath = canonicalPath;
        mLastModified = lastModified;
    }

    public String getName() {
        return mName;
    }

    public String getCanonicalPath() {
        return mCanonicalPath;
    }

    public long getLastModified() {
        return mLastModified;
    }
}
