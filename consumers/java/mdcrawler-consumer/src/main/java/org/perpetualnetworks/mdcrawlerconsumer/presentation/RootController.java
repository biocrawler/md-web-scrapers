package org.perpetualnetworks.mdcrawlerconsumer.presentation;

import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    public String swaggerUi() {
        return "redirect:" + Constants.Swagger.URL;
    }
}
