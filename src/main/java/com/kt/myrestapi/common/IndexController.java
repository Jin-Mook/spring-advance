package com.kt.myrestapi.common;

import com.kt.myrestapi.lectures.LectureController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index() {
        var index = new RepresentationModel<>();
        index.add(WebMvcLinkBuilder.linkTo(LectureController.class).withRel("lectures"));
        return index;
    }
}
