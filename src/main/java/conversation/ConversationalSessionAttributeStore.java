package conversation;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class handles how session scoped model attributes are stored and
 * retrieved from the HttpSession. This implementation uses a timestamp to
 * distinguish multiple command objects of the same type. This is needed for
 * users editing the same entity on multiple tabs of a browser.
 * <p>
 * Source:
 * http://forum.springsource.org/showthread.php?95016-Using-Session-Model
 * -Attributes-With-Multiple-Browser-Tabs-Patch
 * <p>
 * Note: Conversation management was on the agenda for 3.1 then got moved to 3.2
 * but doesn't seem like it will make it there.
 *
 * @author MJones
 * @author Nimo Naamani
 * http://duckranger.com
 * @version Sep 2, 2010
 * <p>
 * <p>
 * Note 2: This is a modification of the code in the URL because I
 * don't want to use a tag lib. Therefore - my implementation creates a
 * conversation map on the session - to allow each conversation to have
 * its own store without adding the cid on the object name. In this way
 * - the cid and the session attributes are kept separated.
 */
@Component
public class ConversationalSessionAttributeStore implements SessionAttributeStore, InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private int keepAliveConversations = 10;

    public final static String UIID_FIELD = "uiid";
    public final static String SESSION_MAP = "sessionConversationMap";

    @Override
    public void storeAttribute(WebRequest request, String attributeName, Object attributeValue) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");
        Assert.notNull(attributeValue, "Attribute value must not be null");

        String uiid = getConversationId(request);

        if (uiid == null || uiid.trim().length() == 0) {
            uiid = UUID.randomUUID().toString();
        }

        request.setAttribute(UIID_FIELD, uiid, WebRequest.SCOPE_REQUEST);
        System.out.println(request.getAttribute("uiid", WebRequest.SCOPE_REQUEST));
        System.out.println(("storeAttribute - storing bean reference for (" + attributeName + ")."));

        store(request, attributeName, attributeValue, uiid);
    }

    @Override
    public Object retrieveAttribute(WebRequest request, String attributeName) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");

        System.out.println("wooohoooo!!!");
        System.out.println(request.getAttribute("uiid", WebRequest.SCOPE_REQUEST));
        System.out.println(request.getParameter("uiid"));

        if (getConversationId(request) != null) {
            System.out.println("retrieveAttribute - retrieving bean reference for (" + attributeName + ") for " +
                    "conversation (" + getConversationId(request) + ").");

            return getConversationStore(request, getConversationId(request)).get(attributeName);

        } else {
            return null;
        }
    }

    @Override
    public void cleanupAttribute(WebRequest request, String attributeName) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");

        System.out.println("cleanupAttribute - removing bean reference for (" + attributeName + ") from conversation "
                + "(" + getConversationId(request) + ").");

        Map<String, Object> conversationStore = getConversationStore(request, getConversationId(request));
        conversationStore.remove(attributeName);

        // Delete the conversation store from the session if empty
        if (conversationStore.isEmpty()) {
            getSessionConversationsMap(request).remove(getConversationId(request));
        }
    }

    /**
     * Retrieve a specific conversation's map of objects from the session. Will
     * create the conversation map if it does not exist.
     * <p>
     * The conversation map is stored inside a session map - which is a map of
     * maps. If this does not exist yet- it will be created too.
     *
     * @param request        - the incoming request
     * @param conversationId - the conversation id we are dealing with
     * @return - the conversation's map
     */
    private Map<String, Object> getConversationStore(WebRequest request, String conversationId) {

        Map<String, Object> conversationMap = getSessionConversationsMap(request).get(conversationId);

        if (conversationId != null && conversationMap == null) {
            conversationMap = new HashMap<String, Object>();
            getSessionConversationsMap(request).put(conversationId, conversationMap);
        }

        return conversationMap;
    }

    /**
     * Get the session's conversations map.
     *
     * @param request - the request
     * @return - LinkedHashMap of all the conversations and their maps
     */
    private LinkedHashMap<String, Map<String, Object>> getSessionConversationsMap(WebRequest request) {
        @SuppressWarnings("unchecked") LinkedHashMap<String, Map<String, Object>> sessionMap = (LinkedHashMap<String,
                Map<String, Object>>) request.getAttribute(SESSION_MAP, WebRequest.SCOPE_SESSION);

        if (sessionMap == null) {
            sessionMap = new LinkedHashMap<String, Map<String, Object>>();
            request.setAttribute(SESSION_MAP, sessionMap, WebRequest.SCOPE_SESSION);

        } else {
            sessionMap.forEach((key, value) -> System.out.println(key + " " + value));
        }

        return sessionMap;
    }

    /**
     * Store an object on the session. If the configured maximum number of live
     * conversations to keep is reached - clear out the oldest conversation. (If
     * max number is configured as 0 - no removal will happen)
     *
     * @param request        - the web request
     * @param attributeName  - the name of the attribute (from @SessionAttributes)
     * @param attributeValue - the value to store
     */
    private void store(WebRequest request, String attributeName, Object attributeValue, String cId) {
        LinkedHashMap<String, Map<String, Object>> sessionConversationsMap = getSessionConversationsMap(request);

        if (keepAliveConversations > 0 && sessionConversationsMap.size() >= keepAliveConversations && !sessionConversationsMap.containsKey(cId)) {
            // clear oldest conversation
            String key = sessionConversationsMap.keySet().iterator().next();
            sessionConversationsMap.remove(key);
        }

        getConversationStore(request, cId).put(attributeName, attributeValue);
        sessionConversationsMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    public int getKeepAliveConversations() {
        return keepAliveConversations;
    }

    public void setKeepAliveConversations(int numConversationsToKeep) {
        keepAliveConversations = numConversationsToKeep;
    }

    /**
     * Helper method to get conversation id from the web request
     *
     * @param request - Incoming request
     * @return - the conversationId (note that this is a request parameter, and
     * only gets there on form submit)
     */
    private String getConversationId(WebRequest request) {
        String uiid = request.getParameter(UIID_FIELD);

        if (uiid == null) {
            uiid = (String) request.getAttribute(UIID_FIELD, WebRequest.SCOPE_REQUEST);
        }

        return uiid;
    }

    /**
     * Required for wiring the RequestMappingHandlerAdapter
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        requestMappingHandlerAdapter.setSessionAttributeStore(this);
    }
}