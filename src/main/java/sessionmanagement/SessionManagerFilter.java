/**
 * Assume that every request to a servlet contains a parameter named uiid, which represents a user ID. The
 * requester has to keep track of sending a new ID everytime a link is clicked that opens a new window. In my case
 * this is sufficient, but feel free to use any other (maybe more secure) method here. Furthermore, I work with
 * Tomcat 7 or 8. You might need to extend other classes when working with different servlet containers, but the APIs
 * shouldn't change too much.
 *
 * In the following, the created sessions are referred to as subsessions, the original container managed session is
 * the parent session. The implementation consists of the following five classes:
 *
 * The SingleSessionManager keeps track of creation, distribution and cleanup of all subsessions. It does this by
 * acting as a servlet filter which replaces the ServletRequest with a wrapper that returns the appropriate
 * subsession. A scheduler periodically checks for expired subsessions ...and yes, it's a singleton.
 */

package sessionmanagement;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * A singleton class that manages multiple sessions on top of a regular container managed session.
 * See web.xml for information on how to enable this.
 */
public class SessionManagerFilter implements Filter {

    /**
     * The default session timeout in seconds to be used if no explicit timeout is provided.
     */
    public static final int DEFAULT_TIMEOUT = 900;

    /**
     * The default interval for session validation checks in seconds to be used if no explicit
     * timeout is provided.
     */
    public static final int DEFAULT_SESSION_INVALIDATION_CHECK = 15;

    private static SessionManagerFilter instance;

    private ScheduledExecutorService scheduler;
    protected int timeout;
    protected long sessionInvalidationCheck;

    private Map<SubSessionKey, HttpSessionWrapper> sessions = new ConcurrentHashMap<SubSessionKey,
            HttpSessionWrapper>();

    public SessionManagerFilter() {
        sessionInvalidationCheck = DEFAULT_SESSION_INVALIDATION_CHECK;
        timeout = DEFAULT_TIMEOUT;
    }

    public static SessionManagerFilter getInstance() {
        if (instance == null) {
            instance = new SessionManagerFilter();
        }

        return instance;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpRequestWrapper wrapper = new HttpRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        String timeout = cfg.getInitParameter("sessionTimeout");

        if (timeout != null && !timeout.trim().equals("")) {
            getInstance().timeout = Integer.parseInt(timeout) * 60;
        }

        String sessionInvalidationCheck = cfg.getInitParameter("sessionInvalidationCheck");

        if (sessionInvalidationCheck != null && !sessionInvalidationCheck.trim().equals("")) {
            getInstance().sessionInvalidationCheck = Long.parseLong(sessionInvalidationCheck);
        }

        getInstance().startSessionExpirationScheduler();
    }

    /**
     * Create a new session ID.
     *
     * @return A new unique session ID.
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    protected void startSessionExpirationScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
            final Runnable sessionInvalidator = new Runnable() {
                public void run() {
                    SessionManagerFilter.getInstance().destroyExpiredSessions();
                }
            };
            final ScheduledFuture<?> sessionInvalidatorHandle = scheduler.scheduleAtFixedRate(sessionInvalidator,
                    this.sessionInvalidationCheck, this.sessionInvalidationCheck, TimeUnit.SECONDS);
        }
    }

    /**
     * Get the timeout after which a session will be invalidated.
     *
     * @return The timeout of a session in seconds.
     */
    public int getSessionTimeout() {
        return timeout;
    }

    /**
     * Retrieve a session.
     *
     * @param uiid            The user id this session is to be associated with.
     * @param create          If <code>true</code> and no session exists for the given user id, a new session is
     *                        created and associated with the given user id. If <code>false</code> and no
     *                        session exists for the given user id, no new session will be created and this
     *                        method will return <code>null</code>.
     * @param originalSession The original backing session created and managed by the servlet container.
     * @return The session associated with the given user id if this session exists and/or create is
     * set to <code>true</code>, <code>null</code> otherwise.
     */
    public HttpSession getSession(String uiid, boolean create, HttpSession originalSession) {
        if (uiid != null && originalSession != null) {
            SubSessionKey key = new SubSessionKey(originalSession.getId(), uiid);
            synchronized (sessions)
            {
                HttpSessionWrapper session = sessions.get(key);

                if (session == null && create) {
                    session = new HttpSessionWrapper(uiid, originalSession);
                    sessions.put(key, session);
                }

                if (session != null) {
                    session.setLastAccessedTime(System.currentTimeMillis());
                }

                return session;
            }
        }

        return null;
    }

    public HttpSessionWrapper removeSession(SubSessionKey key) {
        return sessions.remove(key);
    }

    /**
     * Destroy a session, freeing all it's resources.
     *
     * @param session The session to be destroyed.
     */
    public void destroySession(HttpSessionWrapper session) {
        String uiid = ((HttpSessionWrapper) session).getUiid();
        SubSessionKey key = new SubSessionKey(session.getOriginalSession().getId(), uiid);
        HttpSessionWrapper w = getInstance().removeSession(key);

        if (w != null) {
            System.out.println("Session " + w.getId() + " with uiid " + uiid + " was destroyed.");

        } else {
            System.out.println("uiid " + uiid + " does not have a session.");
        }
    }

    /**
     * Destroy all session that are expired at the time of this method call.
     */
    public void destroyExpiredSessions() {
        List<HttpSessionWrapper> markedForDelete = new ArrayList<>();
        long time = System.currentTimeMillis() / 1000;

        for (HttpSessionWrapper session : sessions.values()) {
            if (time - (session.getLastAccessedTime() / 1000) >= session.getMaxInactiveInterval()) {
                markedForDelete.add(session);
            }
        }

        for (HttpSessionWrapper session : markedForDelete) {
            destroySession(session);
        }
    }

    /**
     * Remove all subsessions that were created from a given parent session.
     *
     * @param originalSession All subsessions created with this session as their parent session will be
     *                        invalidated.
     */
    public void clearAllSessions(HttpSession originalSession) {
        Iterator<HttpSessionWrapper> it = sessions.values().iterator();

        while (it.hasNext()) {
            HttpSessionWrapper w = it.next();

            if (w.getOriginalSession().getId().equals(originalSession.getId())) {
                destroySession(w);
            }
        }
    }

    public void setSessionTimeout(int timeout) {
        this.timeout = timeout;
    }
}