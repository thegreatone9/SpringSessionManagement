package controller;

import commandobject.TestCommandObject;
import dao.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
@SessionAttributes("commandObject")
@RequestMapping("/test")
public class HomeController {

    @Autowired
    private Data dataDao;

    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private String homeGetHandler(HttpServletRequest req, Model model) {
        TestCommandObject commandObject = new TestCommandObject();

        commandObject.setOptions(dataDao.getOptions());

        model.addAttribute("commandObject", commandObject);

        return "index";
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private String homePostHandler(@ModelAttribute("commandObject") TestCommandObject commandObject, Model model) {
        String option = commandObject.getOption();
        String selection = commandObject.getSelection();

        if (option != null && selection == null) {

            if (option.equals("meals")) {
                commandObject.setMeals(dataDao.getMeals());

            } else if (option.equals("customers")) {
                commandObject.setCustomers(dataDao.getCustomers());
            }

        } else if (option == null && selection != null) {
            commandObject.setSelection(selection);
        }

        model.addAttribute("commandObject", commandObject);

        return "index";
    }
}