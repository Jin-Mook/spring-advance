package com.kt.myrestapi.runner;

import com.kt.myrestapi.lectures.Lecture;
import com.kt.myrestapi.lectures.LectureRepository;
import com.kt.myrestapi.lectures.LectureStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class LectureInsertRunner implements ApplicationRunner {

    private final LectureRepository lectureRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IntStream.range(0, 15).forEach(this::generateLecture);
    }

    private Lecture generateLecture(int index) {
        Lecture lecture = buildLecture(index);
        return this.lectureRepository.save(lecture);
    }

    private Lecture buildLecture(int index) {
        return Lecture.builder()
                .name(index + " event ")
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 24, 14, 21))
                .beginLectureDateTime(LocalDateTime.of(2022, 11, 25, 14, 21))
                .endLectureDateTime(LocalDateTime.of(2022, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location(index + " 강의장")
                .free(false)
                .offline(true)
                .lectureStatus(LectureStatus.DRAFT)
                .build();
    }
}
