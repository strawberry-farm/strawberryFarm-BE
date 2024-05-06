package com.strawberryfarm.fitingle.domain.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping(value = "/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Board API")
public class BoardController {

    private final BoardService boardService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> boardRegister(@RequestParam("images") List<MultipartFile> images,
//                                           @ModelAttribute BoardRegisterRequestDTO boardRegisterRequestDto,
//                                           @AuthenticationPrincipal UserDetails userDetails) {
//          Long userId = Long.parseLong(userDetails.getUsername());
//        return ResponseEntity.ok(boardService.boardRegister(boardRegisterRequestDto,images,userId));
//    }

    @PostMapping
    public ResponseEntity<?> boardRegister(@RequestParam(value = "images") List<MultipartFile> images,
                                           @RequestParam("data") String jsonData,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        ObjectMapper mapper = new ObjectMapper();
        BoardRegisterRequestDTO boardRegisterRequestDto;
        try {
            boardRegisterRequestDto = mapper.readValue(jsonData, BoardRegisterRequestDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data");
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(boardService.boardRegister(boardRegisterRequestDto,images,userId));
    }

//    @PutMapping(value = "/{boardsId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> boardUpdate(@PathVariable Long boardsId,
//                                         @RequestParam("images") List<MultipartFile> images,
//                                         //이미지 url 또 따로 받음.
//                                         @ModelAttribute BoardUpdateRequestDTO boardUpdateRequestDTO){
//
//        return ResponseEntity.ok(boardService.boardUpdate(boardsId, boardUpdateRequestDTO, images));
//    }

    @PutMapping(value = "/{boardsId}")
    public ResponseEntity<?> boardUpdate(@PathVariable Long boardsId,
                                         @RequestParam("images") List<MultipartFile> images,
                                         @RequestParam("data") String jsonData,
                                         @AuthenticationPrincipal UserDetails userDetails){

        ObjectMapper mapper = new ObjectMapper();
        BoardUpdateRequestDTO boardUpdateRequestDTO;
        try {
            boardUpdateRequestDTO = mapper.readValue(jsonData, BoardUpdateRequestDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data");
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(boardService.boardUpdate(boardsId, boardUpdateRequestDTO, images,userId));
    }

    @GetMapping("/{boardsId}")
    public ResponseEntity<?> boardDetail(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long boardsId){

        //회원이 아닌경우도 접근 가능하게 null 처리
        Long userId = Optional.ofNullable(userDetails)
                .map(UserDetails::getUsername)
                .map(Long::parseLong)
                .orElse(null);

        return ResponseEntity.ok(boardService.boardDetail(boardsId,userId));
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색", description = "게시물 검색 api")
    public ResponseEntity<?> boardSearch(@AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("keyword") String keyword, @RequestParam("page") int page,
        @RequestParam("size") int size) {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();

        Long userId = Long.parseLong(userDetails.getUsername());

//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());

        return ResponseEntity.ok(boardService.boardSearch(userId, keyword, page, size));
    }

    @GetMapping("/search/non-user")
    @Operation(summary = "게시물 검색", description = "게시물 검색 api")
    public ResponseEntity<?> boardSearchNonUser(@RequestParam("keyword") String keyword,
        @RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(boardService.boardSearchNonUser(keyword, page, size));
    }

    @PostMapping("/test")
    @Operation(summary = "검색 데이터 넣기", description = "데이터 넣는 api")
    public ResponseEntity<?> addBoardTestData() {
        return ResponseEntity.ok(boardService.addBoardTestData());
    }

    //trace, debug 안보
    @GetMapping("/test")
    public void test() {
        log.trace("TRACE!!");
        log.debug("DEBUG!!");
        log.info("INFO!!");
        log.warn("WARN!!");
        log.error("ERROR!!");
    }
}
