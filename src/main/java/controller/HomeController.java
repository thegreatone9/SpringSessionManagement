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
import sessionmanagement.HttpSessionWrapper;
import sessionmanagement.SessionManagerFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private String homeGetHandler(HttpServletRequest req) {
        String uiid = generateSessionId();
        req.setAttribute("uiid", uiid);

        HttpSession session = req.getSession();

        TestCommandObject commandObject = new TestCommandObject();

        commandObject.setOptions(dataDao.getOptions());

        session.setAttribute("commandObject", commandObject);

        return "index";
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private String homePostHandler(@RequestParam(required = false) Integer option,
                                   @RequestParam(required = false) String selection,
                                   HttpServletRequest req,
                                   Model model) {

        String uiid = (String) req.getParameter("uiid");
        HttpSession session = req.getSession(false);
        TestCommandObject commandObject = (TestCommandObject) session.getAttribute("commandObject");
        model.addAttribute("uiid", uiid);

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

            //session.invalidate();
        }

        model.addAttribute("commandObject", commandObject);

        return "index";
    }
}