package com.pzl.program.toolkit.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 地址自动跳转swagger功能
 *
 * @author pzl
 */
@ApiIgnore
@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "redirect:swagger-ui.html";
    }

}
