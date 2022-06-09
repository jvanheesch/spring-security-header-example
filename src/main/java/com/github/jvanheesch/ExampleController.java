package com.github.jvanheesch;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @Secured("ROLE_OK")
    @GetMapping("/ok")
    public String ok() {
        return "ok";
    }

    @Secured("ROLE_NOK")
    @GetMapping("/nok")
    public String nok() {
        return "nok";
    }
}
