package controller;

import commandobject.TestCommandObject;
import dao.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author musa.khan
 * @since 08/01/2021
 */
@Controller
@SessionAttributes(names = {"commandObject"})
@RequestMapping("/test")
public class HomeController {

    @Autowired
    private Data dataDao;

    private String generateRandomId() {
        return "ABC";
    }

    private void eraseCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            System.out.println("cookies not null, about to loop.");
            for (Cookie cookie : cookies) {
                System.out.println("cookie to erase: " + cookie.getName());
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        }
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private String homeGetHandler(Model model, HttpSession session) {
        Map<String, Object> sessionMap = (Map<String, Object>) session.getAttribute("sessionMap");
        TestCommandObject commandObject = new TestCommandObject();
        String sessionId = generateRandomId();
        sessionMap.put(sessionId, commandObject);
        session.setAttribute("sessionId", sessionId);

        commandObject.setOptions(dataDao.getOptions());

        return "index";
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private String homePostHandler(@RequestParam(required = false) Integer option,
                                   @RequestParam(required = false) String selection,
                                   @RequestParam String sessionId,
                                   HttpSession session,
                                   Model model) {

        Map<String, Object> sessionMap = (Map<String, Object>) session.getAttribute("sessionMap");
        TestCommandObject commandObject = (TestCommandObject) sessionMap.get(sessionId);
        model.addAttribute("sessionId", sessionId);

        if (option != null && selection == null) {

            List<String> options = commandObject.getOptions();

            commandObject.setOption(options.get(option));

            if (option == 0) {
                commandObject.setMeals(dataDao.getMeals());

            } else if (option == 1) {
                commandObject.setCustomers(dataDao.getCustomers());
            }

        } else if (option == null && selection != null) {
            commandObject.setSelection(selection);
            session.removeAttribute(sessionId);
        }

        model.addAttribute("commandObject", commandObject);

        return "index";
    }
}