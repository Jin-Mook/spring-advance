package com.kt.myrestapi.lectures;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class LectureResource extends RepresentationModel<LectureResource> {
    @JsonUnwrapped
    private LectureResDto lectureResDto;

    public LectureResource(LectureResDto lectureResDto) {
        this.lectureResDto = lectureResDto;
        add(WebMvcLinkBuilder.linkTo(LectureController.class).slash(lectureResDto.getId()).withSelfRel());
    }

    public LectureResDto getLectureResDto() {
        return lectureResDto;
    }
}
