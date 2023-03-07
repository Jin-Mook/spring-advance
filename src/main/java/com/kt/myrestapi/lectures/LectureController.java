package com.kt.myrestapi.lectures;

import com.kt.myrestapi.common.ErrorsResource;
import com.kt.myrestapi.lectures.dto.LectureReqDto;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequestMapping(value = "/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class LectureController {

    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    @PutMapping("/{id}")
    public ResponseEntity updateLecture(@PathVariable Integer id,
                                        @RequestBody @Validated LectureReqDto lectureReqDto,
                                        BindingResult errors) {
        Optional<Lecture> optionalLecture = lectureRepository.findById(id);
        if (optionalLecture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " Lecture Not Found");
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Lecture existingLecture = optionalLecture.get();
        log.info("existingLecture before modelMapper = {}", existingLecture);
        modelMapper.map(lectureReqDto, existingLecture);
        log.info("existingLecture after modelMapper = {}", existingLecture);
        Lecture savedLecture = lectureRepository.save(existingLecture);
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
//        LectureResDto lectureResDto = modelMapper.map(existingLecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLecture(@PathVariable Integer id) {
        Optional<Lecture> optionalLecture = lectureRepository.findById(id);
        if (optionalLecture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " Lecture Not Found");
        }
        Lecture lecture = optionalLecture.get();
        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping
    public ResponseEntity queryLectures(Pageable pageable, PagedResourcesAssembler<LectureResDto> assembler) {
        Page<Lecture> lecturePage = lectureRepository.findAll(pageable);
        Page<LectureResDto> lectureResDtoPage = lecturePage.map(lecture -> modelMapper.map(lecture, LectureResDto.class));
//        PagedModel<EntityModel<LectureResDto>> pagedResources = assembler.toModel(lectureResDtoPage);

        PagedModel<LectureResource> pagedResources = assembler.toModel(lectureResDtoPage, LectureResource::new);

        return ResponseEntity.ok(pagedResources);
    }

    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Validated LectureReqDto lectureReqDto, BindingResult errors) {
        // 입력항목 체크
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // 입력항목의 biz logic 체크
        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        // free, offline 값을 갱신
        lecture.update();

        Lecture savedLecture = lectureRepository.save(lecture);

        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(LectureController.class).slash(lecture.getId());
        URI createUri = selfLinkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lecture"));
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));

        log.info("lectureResource = {}", lectureResource);
        log.info("lectureResource lectureResDto = {}", lectureResource.getLectureResDto());

        return ResponseEntity.created(createUri).body(lectureResource);
    }

    private static ResponseEntity<ErrorsResource> badRequest(BindingResult errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
