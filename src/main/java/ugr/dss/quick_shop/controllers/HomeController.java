package ugr.dss.quick_shop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping({ "", "/", "/index" })
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

}
