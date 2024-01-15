package com.strawberryfarm.fitingle.domain.comment.controller;

import com.strawberryfarm.fitingle.domain.comment.dto.CommentRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.comment.repository.CommentRepository;
import com.strawberryfarm.fitingle.domain.comment.service.CommentService;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/boards", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/qna/comment")
    public ResponseEntity<?> qnaRegister(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody CommentRegisterRequestDTO commentRegisterRequestDTO){
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(commentService.commentRegister(commentRegisterRequestDTO,userId));
    }

    //todo 1.삭제, 2.수정
}
