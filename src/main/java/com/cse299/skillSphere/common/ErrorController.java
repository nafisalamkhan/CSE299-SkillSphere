package com.cse299.skillSphere.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping(value = "/403")
    public String accessDenied() {
        return "403";
    }
}