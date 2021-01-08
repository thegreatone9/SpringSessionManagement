package sessionmanagement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author musa.khan
 * @since 08/01/2021
 */
public abstract class RequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest request;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    public void storeCustomSession() {

    }
}