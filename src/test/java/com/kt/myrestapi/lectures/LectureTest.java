package com.kt.myrestapi.lectures;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LectureTest {
    @Test
    void builder() {
        Lecture lecture = Lecture.builder()
                .name("Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(lecture).isNotNull();
    }

    @Test
    void javaBean() {
        // given
        String name = "Lecture";
        String description = "Spring";

        // when
        Lecture lecture = new Lecture();
        lecture.setName(name);
        lecture.setDescription(description);

        // then
        assertThat(lecture.getName()).isEqualTo("Lecture");
        assertThat(lecture.getDescription()).isEqualTo("Spring");
    }
}