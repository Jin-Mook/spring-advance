package com.kt.myrestapi;

import com.kt.myrestapi.common.RequestLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @PostMapping("/login")
    public String login(@RequestBody RequestLogin requestLogin) {
        log.info("requestLogin = {}", requestLogin);
        return "login";
    }
}
