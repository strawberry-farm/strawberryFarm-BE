package com.strawberryfarm.fitingle.domain.wish.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.wish.dto.WishDeleteResponseDTO;
import com.strawberryfarm.fitingle.domain.wish.dto.WishGetListResponseDTO;
import com.strawberryfarm.fitingle.domain.wish.dto.WishRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.wish.dto.WishRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import com.strawberryfarm.fitingle.domain.wish.repository.WishRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishService {

    private final UsersRepository usersRepository;

    private final BoardRepository boardRepository;

    private final WishRepository wishRepository;


    @Transactional
    public ResultDto<WishRegisterResponseDTO> wishRegister(WishRegisterRequestDTO wishRegisterRequestDto, Long userId) {

        //1.boardId 존재 하는지 확인
        Optional<Board> boardOptional = boardRepository.findById(wishRegisterRequestDto.getBoardsId());
        if (!boardOptional.isPresent()) {
            return ResultDto.<WishRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
                    .build();
        }


        //2.userId 존재 하는지 확인
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<WishRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        // 3. 이미 '위시'가 등록되었는지 확인
        Optional<Wish> wishOptional = wishRepository.findByUserIdAndBoardId(userOptional.get().getId(), boardOptional.get().getId());
        if(wishOptional.isPresent()){
            return ResultDto.<WishRegisterResponseDTO>builder()
                    .message(ErrorCode.DUPLICATE_WISH.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.DUPLICATE_WISH.getCode())
                    .build();
        }

        //4.wish (연관관계 매핑)
        Wish saveWish = Wish.builder()
                .user(userOptional.get())  // Users 엔티티 연결
                .board(boardOptional.get())  // Board 엔티티 연결
                .build();

        userOptional.get().addWish(saveWish);  // Users 엔티티에 Wish 추가
        boardOptional.get().addWish(saveWish); // Board 엔티티에 Wish 추가

        //5.wish (등록하기)
        Wish savedWish = wishRepository.save(saveWish);

        WishRegisterResponseDTO responseDTO = WishRegisterResponseDTO.builder()
                .boardsId(savedWish.getBoard().getId())
                .wishState(true)
                .build();

        return ResultDto.<WishRegisterResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }

    @Transactional
    public ResultDto<WishDeleteResponseDTO> wishDelete(Long wishId,Long userId) {

        //1.userId 존재 하는지 확인
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<WishDeleteResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        //2.wishId에 해당하는 Wish 엔티티 확인.
        Optional<Wish> wishOptional = wishRepository.findById(wishId);
        if(!wishOptional.isPresent()){
            return ResultDto.<WishDeleteResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_WISH.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_WISH.getCode())
                    .build();
        }

        //3.Wish 엔티티가 요청을 보낸 사용자에게 속하는지 확인.(Wish의 사용자 ID와 요청한 사용자의 ID를 비교)
        if(wishOptional.get().getUser().getId() != userId){
            return ResultDto.<WishDeleteResponseDTO>builder()
                    .message(ErrorCode.WISH_PERMISSION_DENIED.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.WISH_PERMISSION_DENIED.getCode())
                    .build();
        }

        //4.Wish 삭제
        wishRepository.deleteById(wishId);

        WishDeleteResponseDTO responseDTO = WishDeleteResponseDTO.builder()
                .boardsId(wishOptional.get().getBoard().getId())
                .wishId(wishId)
                .wishState(false)
                .build();

        return ResultDto.<WishDeleteResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }

    public ResultDto<List<WishGetListResponseDTO>> wishGetList(Long userId) {

        //1.userId 존재 하는지 확인
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<List<WishGetListResponseDTO>>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }
        //2.userId로 전체 위시 가져오기
        List<Wish> wishList = wishRepository.findByUserId(userId);

        //3.dto로 담기
        List<WishGetListResponseDTO> responseList = wishList.stream()
                .map(wish -> {
                    Board board = wish.getBoard();
                    return WishGetListResponseDTO.builder()
                            .boardsId(board.getId())
                            .title(board.getTitle())
                            .location(board.getLocation())
                            .field(board.getField().getName())
                            .build();
                })
                .collect(Collectors.toList());

        return ResultDto.<List<WishGetListResponseDTO>>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseList)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }
}
