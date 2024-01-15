package com.strawberryfarm.fitingle.domain.qna.service;


import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.comment.dto.CommentRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaDeleteResponseDTO;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.qna.dto.QnaUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.qna.repository.QnaRepository;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;

    private final BoardRepository boardRepository;

    private final UsersRepository usersRepository;

    @Transactional
    public ResultDto<QnaRegisterResponseDTO> qnaRegister(QnaRegisterRequestDTO qnaRegisterRequestDTO, Long userId) {

        Optional<Board> board = boardRepository.findById(qnaRegisterRequestDTO.getBoardsId());

        //게시문 존재 확인
        if(!board.isPresent()){
            return ResultDto.<QnaRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
                    .build();
        }

        //유저 아이디 존재 확인
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<QnaRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        Qna qna = Qna.builder()
                .status(qnaRegisterRequestDTO.isStatus())
                .contents(qnaRegisterRequestDTO.getContents())
                //.password(qnaRegisterRequestDTO.getPassword())
                .build();

        // User에 Qna 추가
        userOptional.get().addQna(qna);
        // Board에 Qna 추가
        board.get().addQna(qna);

        Qna savedQna = qnaRepository.save(qna);

        QnaRegisterResponseDTO responseDTO = QnaRegisterResponseDTO.builder()
                .qnaId(savedQna.getId())
                .build();

        return ResultDto.<QnaRegisterResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }

}
