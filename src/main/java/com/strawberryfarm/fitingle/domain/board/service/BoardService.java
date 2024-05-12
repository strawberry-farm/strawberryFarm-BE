package com.strawberryfarm.fitingle.domain.board.service;

import com.strawberryfarm.fitingle.annotation.Trace;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.board.dto.BoardDetailResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchNonUserDto;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchNonUserResponseDto;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.CommentDTO;
import com.strawberryfarm.fitingle.domain.board.dto.QnaDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepositoryCustom;
import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
import com.strawberryfarm.fitingle.domain.groups.service.GroupsService;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.tag.entity.Tag;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import com.strawberryfarm.fitingle.domain.wish.repository.WishRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.utils.S3Manager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final S3Manager s3Manager;

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final FieldRepository fieldRepository;

    private final WishRepository wishRepository;

    private final GroupsRepository groupsRepository;

    private final UsersRepository usersRepository;

    private final GroupsService groupsService;


    //BOARDS 등록
    @Transactional
    @Trace
    public ResultDto<BoardRegisterResponseDTO> boardRegister(BoardRegisterRequestDTO boardRegisterRequestDTO,
                                                             List<MultipartFile> images,
                                                             Long userId) {

        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<BoardRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        Board board = Board.builder()
                .user(userOptional.get())
                .title(boardRegisterRequestDTO.getTitle())
                .postStatus(PostStatus.Y)
                .contents(boardRegisterRequestDTO.getContents())
                .city(boardRegisterRequestDTO.getCity())
                .district(boardRegisterRequestDTO.getDistrict())
                .headCount(boardRegisterRequestDTO.getHeadcount())
                .BCode(boardRegisterRequestDTO.getB_code())
                .location(boardRegisterRequestDTO.getLocation())
                .latitude(boardRegisterRequestDTO.getLatitude())
                .longitude(boardRegisterRequestDTO.getLongitude())
                .question(boardRegisterRequestDTO.getQuestion())
                .days(Days.valueOf(boardRegisterRequestDTO.getDays()))
                .times(Times.valueOf(boardRegisterRequestDTO.getTimes()))
                .build();

        // 연관관계 세팅 (Tag)
        boardRegisterRequestDTO.getTags().forEach(tagName -> {
            Tag tag = Tag.builder()
                    .contents(tagName)
                    .build();
            board.addTag(tag);
        });

        // 이미지 s3 업로드 처리
        List<String> imageUrls = images.stream()
                .map(file -> s3Manager.uploadFileToS3(file, "boards/"))
                .collect(Collectors.toList());

        // 이미지 URL을 사용하여 Image 엔티티 생성 및 Board 엔티티에 추가
        imageUrls.forEach(url -> {
            Image image = Image.builder()
                    .imageUrl(url)
                    .build();
            board.addImage(image);
        });

        // 연관관계 세팅 (field)
        // 예) fieldId를 기반으로 Field 객체를 조회한 후, board에 설정
        Optional<Field> fieldOptional = fieldRepository.findById(boardRegisterRequestDTO.getFieldId());
        fieldOptional.ifPresent(field -> field.setBoard(board)); // addBoard 메소드는 Field 엔티티에 정의되어 있어야 합니다.


        if (!fieldOptional.isPresent()) {
            return ResultDto.<BoardRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_FIELD.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_FIELD.getCode())
                    .build();
        }

        Board savedBoard = boardRepository.save(board);

        // 1.Groups 생성 및 저장
        groupsService.groupsCreate(userOptional.get(), savedBoard);

        // 2.여기에 채팅 필요

        // BoardRegisterResponseDTO 객체 생성 및 필요한 정보 설정
        BoardRegisterResponseDTO responseDTO = BoardRegisterResponseDTO.builder()
                .boardsId(savedBoard.getId())
                .title(savedBoard.getTitle())
                .createdDate(savedBoard.getCreatedDate())
                .updateDate(savedBoard.getUpdateDate())
                .build();

        // ResultDto 객체 생성 및 반환
        return ResultDto.<BoardRegisterResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }

    //BOARDS 업데이트
    @Transactional
    public ResultDto<BoardUpdateResponseDTO> boardUpdate(Long boardsId,
                                                         BoardUpdateRequestDTO boardUpdateRequestDTO,
                                                         List<MultipartFile> updatedImages) {
        // 기존 Board 엔티티 찾기
        Optional<Board> boardOptional = boardRepository.findById(boardsId);
        if (!boardOptional.isPresent()) {
            return ResultDto.<BoardUpdateResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
                    .build();
        }

        //Board 엔티티의 필드 업데이트 (연관관계 엔티티 제외)
        boardOptional.get().updateBoard(boardUpdateRequestDTO);

        // 필드 찾기&업데이트
        Optional<Field> fieldOptional = fieldRepository.findById(boardUpdateRequestDTO.getFieldId());
        if (!fieldOptional.isPresent()) {
            return ResultDto.<BoardUpdateResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_FIELD.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_FIELD.getCode())
                    .build();
        }

        boardOptional.get().addField(fieldOptional.get());

        // 기존 태그 제거 및 새 태그 추가
        boardOptional.get().clearTags();// 기존 태그 제거
        boardUpdateRequestDTO.getTags().forEach(tagName -> {
            Tag tag = Tag.builder()
                    .contents(tagName)
                    .build();
            boardOptional.get().addTag(tag);
        });

        // 기존 이미지 삭제 및 새 이미지 업로드
        boardOptional.get().getImages().forEach(image -> s3Manager.deleteFileFromS3(image.getImageUrl()));
        boardOptional.get().clearImages(); // 기존 이미지 제거
        // 새 이미지 업로드
        List<String> imageUrls = updatedImages.stream()
                .map(file -> s3Manager.uploadFileToS3(file, "boards/"))
                .collect(Collectors.toList());
        imageUrls.forEach(url -> {
            Image image = Image.builder()
                    .imageUrl(url)
                    .build();
            boardOptional.get().addImage(image);
        });

        // 게시물 저장
        Board savedBoard = boardRepository.save(boardOptional.get());

        // 응답 DTO 생성 및 반환
        BoardUpdateResponseDTO responseDTO = BoardUpdateResponseDTO.builder()
                .boardsId(savedBoard.getId())
                .title(savedBoard.getTitle())
                .createdDate(savedBoard.getCreatedDate())
                .updateDate(savedBoard.getUpdateDate())
                .build();

        return ResultDto.<BoardUpdateResponseDTO>builder()
                .message(ErrorCode.SUCCESS.getMessage())
                .data(responseDTO)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .build();
    }

    //Boards 상세보기
    @Transactional(readOnly = true)
    public ResultDto<BoardDetailResponseDTO> boardDetail(Long boardsId, Long userId) {

        Optional<Board> boardOptional = boardRepository.findById(boardsId);
        if (!boardOptional.isPresent()) {
            return ResultDto.<BoardDetailResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
                    .build();
        }

        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<BoardDetailResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        Optional<Long> wishIdOptional = checkWish(userOptional.get().getId(), boardOptional.get().getId());
        boolean wishState = wishIdOptional.isPresent();
        Long wishId = wishIdOptional.orElse(null);


        boolean isOwner = boardOptional.get().getUser().getId().equals(userId);

        List<String> imageUrls = boardOptional.get().getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        List<String> tags = boardOptional.get().getTags().stream()
                .map(Tag::getContents)
                .collect(Collectors.toList());

        //todo 인원수가 다 찼을때 status를 N으로 변경 시켜야함. 여기서 하는게 아닐듯..?

        BoardDetailResponseDTO boardDetailResponsedto = BoardDetailResponseDTO.builder()
                .boardId((boardOptional.get().getId()))
                //.userId(userOptional.get().getId())
                .nickname(userOptional.get().getNickname())
                .status(boardOptional.get().getPostStatus().toString())
                .contents(boardOptional.get().getContents())
                .headcount(boardOptional.get().getHeadCount())
                .title(boardOptional.get().getTitle())
                .city(boardOptional.get().getCity())
                .district(boardOptional.get().getDistrict())
                .b_code(boardOptional.get().getBCode())
                .location(boardOptional.get().getLocation())
                .latitude(boardOptional.get().getLatitude())
                .longitude(boardOptional.get().getLongitude())
                .question(boardOptional.get().getQuestion())
                .days(boardOptional.get().getDays().toString())
                .times(boardOptional.get().getTimes().toString())
                .isOwner(isOwner)

                //Q&A , Comments
                .qnas(boardOptional.get().getQnas().stream()
                        .map(qna -> convertToQnaDto(qna, userId, isOwner))
                        .collect(Collectors.toList()))

                //field
                .fieldId(boardOptional.get().getField().getId())
                .fieldName(boardOptional.get().getField().getName())

                //images
                .images(imageUrls)

                //tags
                .tags(tags)
                .participantCount(checkParticipant(boardOptional.get().getId()))
                .wishState(wishState)
                .wishId(wishId)
                .build();

        return boardDetailResponsedto.doResultDto("success", "1111");
    }

    private Optional<Long> checkWish(Long userId, Long boardId) {
        Optional<Wish> wish = wishRepository.findByUserIdAndBoardId(userId, boardId);
        return wish.map(Wish::getId);
    }

    private int checkParticipant(Long boardId) {
        return groupsRepository.countByBoardId(boardId);
    }

    //qna & comment 해당 데이터 없어서 db로 더미 넣어서 확인함.
    private QnaDTO convertToQnaDto(Qna qna, Long userId, boolean isOwner) {
        boolean isAuthor = qna.getUser().getId().equals(userId); //문의 글 작성자
        boolean isPublicQna = qna.isStatus(); //공개 여부

        //문의 작성자 || 게시글 작성자 || 공개여부
        boolean canViewComment = isPublicQna || isAuthor || isOwner;
        CommentDTO commentDto = null;
        if (qna.getComment() != null) {
            commentDto = convertToCommentDto(qna.getComment(), canViewComment);
        }

        return QnaDTO.builder()
                //.userId(qna.getUser().getId())
                .qnaId(qna.getId())
                .profile(qna.getUser().getProfileImageUrl())
                .nickName(qna.getUser().getNickname())
                .contents(canViewComment ? qna.getContents() : "비공개 글 입니다.")
                .status(qna.isStatus())
                .comment(commentDto)
                .build();
    }

    private CommentDTO convertToCommentDto(Comment comment, boolean canViewComment) {
        Users user = comment.getUser();

        return CommentDTO.builder()
                //.userId(user.getId())
                .nickname(user.getNickname())
                .profile(user.getProfileImageUrl())
                .contents(canViewComment ? comment.getContents() : "비공개 글 입니다.")
                .build();
    }

//    //qna & comment 해당 데이터 없어서 db로 더미 넣어서 확인함.(수정건)
//    private QnaDTO convertToQnaDto(Qna qna, Long userId, boolean isOwner) {
//        boolean isPublicQna = qna.isStatus(); //공개 여부
//
//        //문의 작성자 || 게시글 작성자 || 공개여부
//        boolean canViewComment = isPublicQna || isOwner;
//        CommentDTO commentDto = null;
//        if (qna.getComment() != null) {
//            commentDto = convertToCommentDto(qna.getComment(), canViewComment);
//        }
//
//        return QnaDTO.builder()
//                //.userId(qna.getUser().getId())
//                .qnaId(qna.getId())
//                .profile(qna.getUser().getProfileImageUrl())
//                .nickName(qna.getUser().getNickname())
//                .contents(canViewComment ? qna.getContents() : "비공개 글 입니다.")
//                .status(qna.isStatus())
//                .comment(commentDto)
//                .build();
//    }
//
//    private CommentDTO convertToCommentDto(Comment comment, boolean canViewComment) {
//        Users user = comment.getUser();
//
//        return CommentDTO.builder()
//                //.userId(user.getId())
//                .nickname(user.getNickname())
//                .profile(user.getProfileImageUrl())
//                .contents(canViewComment ? comment.getContents() : "비공개 글 입니다.")
//                .build();
//    }

    public ResultDto<List<BoardSearchDTO>> boardSearch(Long userId, String keyword, Days days,
        Times times, int page, int size) {
        List<BoardSearchNonUserDto> boards = boardRepositoryCustom.boardSearchNonUser(keyword, days,
            times, page, size);
        long totalCount = boardRepositoryCustom.boardSearchNonUserTotalCount(keyword, days, times);

        BoardSearchNonUserResponseDto result = BoardSearchNonUserResponseDto.builder()
            .totalCount(totalCount)
            .boards(boards)
            .build();

        ResultDto response = ResultDto.builder()
            .message(String.valueOf(ErrorCode.SUCCESS))
            .data(result)
            .build();

        return response;
    }

    public ResultDto<List<BoardSearchNonUserResponseDto>> boardSearchNonUser(String keyword,
        Days days, Times times, int page, int size) {

        List<BoardSearchNonUserDto> boards = boardRepositoryCustom.boardSearchNonUser(keyword, days,
            times, page, size);
        long totalCount = boardRepositoryCustom.boardSearchNonUserTotalCount(keyword, days, times);

        BoardSearchNonUserResponseDto result = BoardSearchNonUserResponseDto.builder()
            .totalCount(totalCount)
            .boards(boards)
            .build();

        ResultDto response = ResultDto.builder()
            .message(String.valueOf(ErrorCode.SUCCESS))
            .data(result)
            .build();

        return response;
    }

    @Transactional
    public ResultDto addBoardTestData() {
        Field field = fieldRepository.findById(2L)
            .orElseThrow(() -> new NoSuchElementException("not found field"));

        for (int i = 0; i < 10; i++) {
            Users users = Users.builder()
                .email("user" + (i + 1) + "@" + "g.com")
                .nickname("user" + (i + 1))
                .password("$2a$10$uWnsKvXZOTxJaKYBxEeK1O7pQc7V5vvazkE1k8VSi.XEC/lRtacEi")
                .profileImageUrl("default")
                .roles("ROLE_USERS")
                .signUpType(SignUpType.FITINGLE)
                .status(UsersStatus.AUTHORIZED)
                .build();
            usersRepository.save(users);

            for (int j = 0; j < 30; j++) {
                Board board = Board.builder()
                    .user(users)
                    .field(field)
                    .postStatus(PostStatus.Y)
                    .title(users.getNickname() + "의 " + (j + 1) + "번째 게시물")
                    .contents("내용" + (j + 1))
                    .headCount(10L)
                    .city("city")
                    .district("district")
                    .BCode("BCode")
                    .location("location" + (j+1))
                    .latitude("latitude")
                    .longitude("longitude")
                    .question("question")
                    .days(Days.ANYDAY)
                    .times(Times.DAWN)
                    .build();
                boardRepository.save(board);
            }
        }

        ResultDto response = ResultDto.builder()
            .message(String.valueOf(ErrorCode.SUCCESS))
            .data(null)
            .build();

        return response;
    }
}



