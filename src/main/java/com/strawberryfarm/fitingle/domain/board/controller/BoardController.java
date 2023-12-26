package com.strawberryfarm.fitingle.domain.board.controller;

import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.board.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/boards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> boardRegister(@RequestParam("images") List<MultipartFile> images,
                                           @ModelAttribute BoardRegisterRequestDTO boardRegisterRequestDto) {
        return ResponseEntity.ok(boardService.boardRegister(boardRegisterRequestDto,images));
    }
    @PutMapping(value = "/{boardsId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> boardUpdate(@PathVariable Long boardsId,
                                        @RequestParam("images") List<MultipartFile> images,
                                        @ModelAttribute BoardUpdateRequestDTO boardUpdateRequestDTO){
        return ResponseEntity.ok(boardService.boardUpdate(boardsId, boardUpdateRequestDTO, images));
    }
    @GetMapping("/{boardsId}")
    public ResponseEntity<?> boardDetail(@PathVariable Long boardsId){
        return ResponseEntity.ok(boardService.boardDetail(boardsId));
    }

}
