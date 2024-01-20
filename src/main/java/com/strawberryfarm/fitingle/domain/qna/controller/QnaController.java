package com.strawberryfarm.fitingle.domain.qna.controller;


import com.strawberryfarm.fitingle.domain.qna.dto.QnaRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/boards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @PostMapping(value = "/qna")
    public ResponseEntity<?> qnaRegister(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody QnaRegisterRequestDTO qnaRegisterRequestDTO){
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(qnaService.qnaRegister(qnaRegisterRequestDTO,userId));
    }

    //todo 1.삭제, 2.수정
//    @DeleteMapping(value = "/qna/{qnaId}")
//    public ResponseEntity<?> deleteQna(@AuthenticationPrincipal UserDetails userDetails,
//                                       @PathVariable Long qnaId) {
//        // QnA 삭제 로직
//        Long userId = Long.parseLong(userDetails.getUsername());
//        qnaService.deleteQna(qnaId, userId);
//        return ResponseEntity.ok().build(); // 적절한 응답 반환
//    }
//
//    @PutMapping(value = "/qna/{qnaId}")
//    public ResponseEntity<?> updateQna(@AuthenticationPrincipal UserDetails userDetails,
//                                       @PathVariable Long qnaId,
//                                       @RequestBody QnaUpdateRequestDTO qnaUpdateRequestDTO) {
//        // QnA 수정 로직
//        Long userId = Long.parseLong(userDetails.getUsername());
//        qnaService.updateQna(qnaId, qnaUpdateRequestDTO, userId);
//        return ResponseEntity.ok().build(); // 적절한 응답 반환
//    }

    //패스워드 할 경우
//@PostMapping("/qna/{qnaId}/verify")
//public ResponseEntity<?> verifyQnaPassword(@PathVariable Long qnaId, @RequestBody VerifyPasswordRequestDTO request) {
//       //QnA 조회 및 비밀번호 검증 로직
//        // 비밀번호 일치 시, QnA 내용 반환
//        // 비밀번호 불일치 시, 접근 거부 메시지 반환
//        return ResponseEntity.ok().build();
//    }
}
