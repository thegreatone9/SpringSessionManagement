package sessionmanagement;

/**
 * @author musa.khan
 * @since 10/01/2021
 */
/**
 * Key object for identifying a subsession.
 *
 */
public class SubSessionKey {

    private String sessionId;
    private String uiid;

    /**
     * Create a new instance of {@link SubSessionKey}.
     *
     * @param sessionId
     *            The session id of the parent session.
     * @param uiid
     *            The users's id this session is associated with.
     */
    public SubSessionKey(String sessionId, String uiid) {
        super();
        this.sessionId = sessionId;
        this.uiid = uiid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((uiid == null) ? 0 : uiid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SubSessionKey other = (SubSessionKey) obj;
        if (sessionId == null) {
            if (other.sessionId != null)
                return false;
        } else if (!sessionId.equals(other.sessionId))
            return false;
        if (uiid == null) {
            if (other.uiid != null)
                return false;
        } else if (!uiid.equals(other.uiid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SubSessionKey [sessionId=" + sessionId + ", uiid=" + uiid + "]";
    }

}