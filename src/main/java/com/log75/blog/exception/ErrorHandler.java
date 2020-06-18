package com.log75.blog.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alireza on 4/30/20.
 */
@Controller
public class ErrorHandler implements org.springframework.boot.web.servlet.error.ErrorController {
    @GetMapping(value = "/error")
    public ModelAndView handleErrors(HttpServletRequest httpServletRequest) {
        int errorCode = (Integer) httpServletRequest.getAttribute("javax.servlet.error.status_code");
        String msg = "";
        switch (errorCode) {
            case 400: {
                msg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                msg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 403: {
                msg = "Http Error Code: 403. Forbidden";
                break;
            }
            case 404: {
                msg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                msg = "Http Error Code: 500. Internal Server Error";
                break;
            }
            default:
                msg = "error";
        }
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("Nmsg", msg);
        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
