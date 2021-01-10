package controller;

import dao.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author musa.khan
 * @since 08/01/2021
 */
@Controller
@RequestMapping("/test")
public class HomeController {

    @Autowired
    private Data dataDao;

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
    private String homeGetHandler(HttpServletRequest req, HttpSession session) {
        session.removeAttribute("meals");
        session.removeAttribute("customers");
        session.removeAttribute("options");
        session.invalidate();

        session = req.getSession();
        session.setAttribute("options", dataDao.getOptions());

        return "index";
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private String homePostHandler(@RequestParam(required = false) Integer option,
                                   @RequestParam(required = false) String selection,
                                   HttpSession session,
                                   Model model) {

        if (option != null && selection == null) {

            List<String> options = (List<String>) session.getAttribute("options");

            model.addAttribute("option", options.get(option));

            if (option == 0) {
                session.setAttribute("meals", dataDao.getMeals());

            } else if (option == 1) {
                session.setAttribute("customers", dataDao.getCustomers());
            }

        } else if (option == null && selection != null) {
            model.addAttribute("selection", selection);
        }

        return "index";
    }
}