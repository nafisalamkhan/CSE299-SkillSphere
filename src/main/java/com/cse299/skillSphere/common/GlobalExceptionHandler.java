package com.cse299.skillSphere.common;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Model model, Exception ex) {
        model.addAttribute("message", ex.getMessage());
        System.out.println("exception occurred:");
        ex.printStackTrace();
        return "error";
    }
}
