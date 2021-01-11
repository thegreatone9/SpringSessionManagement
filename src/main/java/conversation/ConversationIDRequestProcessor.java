package conversation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * RequestDataValueProcessor:
 * Provides a contract for inspecting and potentially modifying request data values such as URL query parameters or
 * form field values before they are rendered by a view or before a redirect.
 * Implementations may use this contract for example as part of a solution to provide data integrity,
 * confidentiality, protection against cross-site request forgery (CSRF), and others or for other tasks such as
 * automatically adding a hidden field to all forms and URLs.
 *
 * This processor is used to add the conversation id as a hidden field on the
 * form If the conversation id exists on the request.
 *
 * @author Nimo Naamani
 * http://duckranger.com
 *
 */
@Component("requestDataValueProcessor")
public class ConversationIDRequestProcessor implements RequestDataValueProcessor {

    @Override
    public String processAction(HttpServletRequest httpServletRequest, String action, String httpMethod) {
        return action;
    }

    @Override
    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        return value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        Map<String, String> hiddenFields = new HashMap<>();

        if (request.getAttribute(ConversationalSessionAttributeStore.UIID_FIELD) != null) {
            hiddenFields.put(ConversationalSessionAttributeStore.UIID_FIELD,
                    request.getAttribute(ConversationalSessionAttributeStore.UIID_FIELD).toString());
        }

        return hiddenFields;
    }

    @Override
    public String processUrl(HttpServletRequest request, String url) {
        return url;
    }
}