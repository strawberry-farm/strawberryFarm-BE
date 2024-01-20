package com.strawberryfarm.fitingle.domain.comment.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.comment.dto.CommentRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.comment.dto.CommentRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import com.strawberryfarm.fitingle.domain.comment.repository.CommentRepository;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.qna.repository.QnaRepository;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;

    private final QnaRepository qnaRepository;

    @Transactional
    public ResultDto<CommentRegisterResponseDTO> commentRegister(CommentRegisterRequestDTO commentRegisterRequestDTO, Long userId) {

        //유저 아이디 존재 확인
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<CommentRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        //Qna 존재 확인
        Optional<Qna> qnaOptional = qnaRepository.findById(commentRegisterRequestDTO.getQnaId());
        if (!qnaOptional.isPresent()) {
            return ResultDto.<CommentRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_QNA.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_QNA.getCode())
                    .build();
        }

        //Qna에 이미 Comment가 존재하는지 검사
        Qna qna = qnaOptional.get();
        if (qna.getComment() != null) {
            return ResultDto.<CommentRegisterResponseDTO>builder()
                    .message(ErrorCode.COMMENT_ALREADY_EXISTS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.COMMENT_ALREADY_EXISTS.getCode())
                    .build();
        }

        Board board = qnaOptional.get().getBoard(); // Qna 인스턴스에서 Board 인스턴스를 가져옴
        Users boardUser = board.getUser(); // Board 인스턴스에서 User 객체를 가져옴
        Long boardUserId = boardUser.getId(); // Board 인스턴스에서 UserId 가져옴

        if (!boardUserId.equals(userOptional.get().getId())) {
            return ResultDto.<CommentRegisterResponseDTO>builder()
                    .message(ErrorCode.COMMENT_PERMISSION_DENIED.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.COMMENT_PERMISSION_DENIED.getCode())
                    .build();
        }

        Comment comment = Comment.builder()
                .user(userOptional.get())
                .qna(qnaOptional.get())
                .contents(commentRegisterRequestDTO.getContents())
                .build();

        Comment savedComment = commentRepository.save(comment);

        CommentRegisterResponseDTO responseDTO =CommentRegisterResponseDTO.builder()
                .qndId(savedComment.getId())
                .build();

        return ResultDto.<CommentRegisterResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }
}
