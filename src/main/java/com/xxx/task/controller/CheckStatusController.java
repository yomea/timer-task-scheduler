package com.xxx.task.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequestMapping("checkstatus")
public class CheckStatusController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String checkStatus() {
        return "success";
    }

}
