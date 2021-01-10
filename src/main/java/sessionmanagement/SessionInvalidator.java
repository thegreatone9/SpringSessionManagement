package sessionmanagement;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Session listener that listens for the destruction of a container managed session and takes care
 * of destroying all it's subsessions.
 * <p>
 * Normally this listener won't have much to do since subsessions usually have a shorter lifetime
 * than their parent session and therefore will timeout long before this method is called. This
 * listener will only be important in case of an explicit invalidation of a parent session.
 * </p>
 */
public class SessionInvalidator implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        SessionManagerFilter.getInstance().clearAllSessions(arg0.getSession());
    }
}