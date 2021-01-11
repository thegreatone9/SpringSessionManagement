package controller;

import commandobject.TestCommandObject;
import dao.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
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

    public String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private String homeGetHandler(HttpServletRequest req, Model model) {
        String uiid = generateRandomId();
        req.setAttribute("uiid", uiid);

        //HttpSession session = req.getSession();
        req.setAttribute("uiid", req.getSession().getId());

        TestCommandObject commandObject = new TestCommandObject();

        commandObject.setOptions(dataDao.getOptions());

        //session.setAttribute("commandObject", commandObject);
        model.addAttribute("commandObject", commandObject);

        return "index";
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private String homePostHandler(@ModelAttribute("commandObject") TestCommandObject commandObject,
                                   HttpServletRequest req,
                                   Model model) {

        String uiid = req.getParameter("uiid");
        HttpSession session = req.getSession(false);
        model.addAttribute("uiid", uiid);

        Integer optionId = commandObject.getOptionId();
        String selection = commandObject.getSelection();

        if (optionId != null && selection == null) {

            List<String> options = commandObject.getOptions();

            commandObject.setOption(options.get(optionId));

            if (optionId == 0) {
                commandObject.setMeals(dataDao.getMeals());

            } else if (optionId == 1) {
                commandObject.setCustomers(dataDao.getCustomers());
            }

        } else if (optionId == null && selection != null) {
            commandObject.setSelection(selection);

            session.invalidate();
        }

        model.addAttribute("commandObject", commandObject);

        return "index";
    }
}