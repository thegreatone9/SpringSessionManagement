package sessionmanagement;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author musa.khan
 * @since 10/01/2021
 */
/**
 * Implementation of a HttpSession. Each instance of this class is created around a container
 * managed parent session with it's lifetime linked to it's parent's.
 *
 */
@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession {

    private Map<String, Object> attributes;
    private Map<String, Object> values;
    private long creationTime;
    private String id;
    private String uiid;
    private boolean isNew;
    private long lastAccessedTime;
    private HttpSession originalSession;

    public HttpSessionWrapper(String uiid, HttpSession originalSession) {
        creationTime = System.currentTimeMillis();
        lastAccessedTime = creationTime;
        id = SessionManagerFilter.getInstance().generateSessionId();
        isNew = true;
        attributes = new HashMap<String, Object>();
        Enumeration<String> names = originalSession.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            attributes.put(name, originalSession.getAttribute(name));
        }
        values = new HashMap<String, Object>();
        for (String name : originalSession.getValueNames()) {
            values.put(name, originalSession.getValue(name));
        }
        this.uiid = uiid;
        this.originalSession = originalSession;
    }

    public String getUiid() {
        return uiid;
    }

    public void setNew(boolean b) {
        isNew = b;
    }

    public void setLastAccessedTime(long time) {
        lastAccessedTime = time;
    }

    @Override
    public Object getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        return SessionManagerFilter.getInstance().getSessionTimeout();
    }

    @Override
    public ServletContext getServletContext() {
        return originalSession.getServletContext();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return new HttpSessionContext() {

            @Override
            public Enumeration<String> getIds() {
                return Collections.enumeration(new HashSet<String>());
            }

            @Override
            public HttpSession getSession(String arg0) {
                return null;
            }

        };
    }

    @Override
    public Object getValue(String arg0) {
        return values.get(arg0);
    }

    @Override
    public String[] getValueNames() {
        return values.keySet().toArray(new String[values.size()]);
    }

    @Override
    public void invalidate() {
        SessionManagerFilter.getInstance().destroySession(this);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void putValue(String arg0, Object arg1) {
        values.put(arg0, arg1);
    }

    @Override
    public void removeAttribute(String arg0) {
        attributes.remove(arg0);
    }

    @Override
    public void removeValue(String arg0) {
        values.remove(arg0);
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        attributes.put(arg0, arg1);
    }

    @Override
    public void setMaxInactiveInterval(int arg0) {
        SessionManagerFilter.getInstance().setSessionTimeout(arg0);
    }

    public HttpSession getOriginalSession() {
        return originalSession;
    }

}