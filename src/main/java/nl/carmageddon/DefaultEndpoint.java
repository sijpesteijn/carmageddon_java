package nl.carmageddon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Gijs Sijpesteijn
 */
@Controller
public class DefaultEndpoint {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("steerconnected", "true");
        return "index";
    }

}
