package sessionmanagement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author musa.khan
 * @since 08/01/2021
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Wrapper class that wraps a {@link HttpServletRequest} object. All methods are redirected to the
 * wrapped request except for the <code>getSession</code> which will return an
 * {@link HttpSessionWrapper} depending on the user id in this request's parameters.
 *
 */
public class RequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private HttpServletRequest req;

    public RequestWrapper(HttpServletRequest req) {
        super(req);
        this.req = req;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        String[] uiid = getParameterMap().get("uiid");
        if (uiid != null && uiid.length >= 1) {
            return SessionManagerFilter.getInstance().getSession(uiid[0], create, req.getSession(create));
        }
        return req.getSession(create);
    }
}