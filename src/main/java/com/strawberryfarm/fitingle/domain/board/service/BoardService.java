package com.strawberryfarm.fitingle.domain.board.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
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
import java.util.Collections;
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

    private final Gson gson = new Gson();


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

        List<String> questions = boardRegisterRequestDTO.getQuestion();

        String jsonQuestions = gson.toJson(questions);

        // bcode 처리: 입력받은 bcode가 5자리보다 길 경우, 앞에서 5자리만 사용
        String bcode = boardRegisterRequestDTO.getBcode();
        if (bcode != null && bcode.length() > 5) {
            bcode = bcode.substring(0, 5);
        }

        Board board = Board.builder()
                .user(userOptional.get())
                .title(boardRegisterRequestDTO.getTitle())
                .postStatus(PostStatus.Y)
                .contents(boardRegisterRequestDTO.getContents())
                .headCount(boardRegisterRequestDTO.getHeadcount())
                .BCode(bcode)
                .addr(boardRegisterRequestDTO.getAddr())
                .location(boardRegisterRequestDTO.getDetail())
                .latitude(boardRegisterRequestDTO.getLatitude())
                .longitude(boardRegisterRequestDTO.getLongitude())
                .question(jsonQuestions)
                .days(Days.fromLabel(boardRegisterRequestDTO.getDays()))
                .times(Times.fromLabel(boardRegisterRequestDTO.getTimes()))
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
        groupsCreate(userOptional.get(), savedBoard, GroupsStatus.HOST);

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
   /* @Transactional
    public ResultDto<BoardUpdateResponseDTO> boardUpdate(Long boardsId,
                                                         BoardUpdateRequestDTO boardUpdateRequestDTO,
                                                         List<MultipartFile> updatedImages,
                                                         Long userId) {

        //기존 회원 엔티티 찾기
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<BoardUpdateResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

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
    }*/
    @Transactional
    public ResultDto<BoardUpdateResponseDTO> boardUpdate(Long boardsId,
                                                         BoardUpdateRequestDTO boardUpdateRequestDTO,
                                                         List<MultipartFile> updatedImages,
                                                         Long userId) {

        //기존 회원 엔티티 찾기
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<BoardUpdateResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

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
        boardOptional.get().clearTags(); // 기존 태그 제거
        boardUpdateRequestDTO.getTags().forEach(tagName -> {
            Tag tag = Tag.builder()
                    .contents(tagName)
                    .build();
            boardOptional.get().addTag(tag);
        });

        List<String> urlsToKeep = Optional.ofNullable(boardUpdateRequestDTO.getImg()).orElse(Collections.emptyList());
        // S3에서 불필요한 이미지 파일 삭제
        boardOptional.get().getImages().forEach(image -> {
           // System.out.println("Checking image URL for deletion: " + image.getImageUrl());
            if (!urlsToKeep.contains(image.getImageUrl())) {
                s3Manager.deleteFileFromS3(image.getImageUrl());
            }
        });

        // 이미지 처리: 기존 이미지 중 DTO에서 제공된 리스트에 없는 이미지만 삭제
        boardOptional.get().getImages().removeIf(image -> !urlsToKeep.contains(image.getImageUrl()));




        // 새 이미지 업로드 및 이미지 추가
        if (updatedImages != null && !updatedImages.isEmpty()) { // 새 이미지가 제공되었는지 확인
            updatedImages.forEach(file -> {
                String uploadedUrl = s3Manager.uploadFileToS3(file, "boards/");
                if (!urlsToKeep.contains(uploadedUrl)) {
                    Image newImage = Image.builder()
                            .imageUrl(uploadedUrl)
                            .build();
                    boardOptional.get().addImage(newImage);
                }
            });
        }

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


        Board board = boardOptional.get();
        boolean isOwner = (userId != null && board.getUser() != null && userId.equals(board.getUser().getId()));

        String nickname = "Anonymous"; // 기본 닉네임 설정
        Optional<Users> userOptional = Optional.empty();
        if (userId != null) {
            userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                nickname = userOptional.get().getNickname();
            }
        }

        boolean wishState = false;
        Long wishId = null;
        if (userId != null) {
            Optional<Long> wishIdOptional = checkWish(userId, board.getId());
            wishState = wishIdOptional.isPresent();
            wishId = wishIdOptional.orElse(null);
        }

        List<String> imageUrls = board.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        List<String> tags = board.getTags().stream()
                .map(Tag::getContents)
                .collect(Collectors.toList());

        List<QnaDTO> qnas = board.getQnas().stream()
                .map(qna -> convertToQnaDto(qna, userId, isOwner))
                .collect(Collectors.toList());

        // JSON 문자열에서 List<String>으로 변환
        List<String> questions = gson.fromJson(board.getQuestion(), new TypeToken<List<String>>(){}.getType());


        BoardDetailResponseDTO boardDetailResponseDTO = BoardDetailResponseDTO.builder()
                .boardId(board.getId())
                .nickname(nickname)
                .status(board.getPostStatus().toString())
                .contents(board.getContents())
                .headcount(board.getHeadCount())
                .title(board.getTitle())
                .bcode(board.getBCode())
                .detail(board.getLocation())
                .latitude(board.getLatitude())
                .longitude(board.getLongitude())
                .question(questions)
                .days(board.getDays().toString())
                .times(board.getTimes().toString())
                .profile(board.getUser().getProfileImageUrl())
                .isOwner(isOwner)
                .addr(board.getAddr())
                .wishState(wishState)
                .wishId(wishId)
                .images(imageUrls)
                .tags(tags)
                .participantCount(checkParticipant(board.getId()))
                .qnas(qnas)
                .fieldId(board.getField().getId())
                .fieldName(board.getField().getName())
                .build();

        return ResultDto.<BoardDetailResponseDTO>builder()
                .message("success")
                .data(boardDetailResponseDTO)
                .build();
    }


    private Optional<Long> checkWish(Long userId, Long boardId) {
        Optional<Wish> wish = wishRepository.findByUserIdAndBoardId(userId, boardId);
        System.out.println("Wish: " + wish); // 로그 출력
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
                .userId(qna.getUser().getId())
                .profile(qna.getUser().getProfileImageUrl())
                .nickName(qna.getUser().getNickname())
                .contents(canViewComment ? qna.getContents() : "비공개 글 입니다.")
                .status(qna.isStatus())
                .comment(commentDto)
                .owner(isAuthor)
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
    @Transactional
    public ResultDto<?> boardDelete(Long boardId, Long userId) {

        Optional<Users> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResultDto.<BoardRegisterResponseDTO>builder()
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .data(null)
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .build();
        }

        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (!boardOptional.isPresent()) {
            return ResultDto.builder()
                    .message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
                    .errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
                    .build();
        }

        Board board = boardOptional.get();
        // 게시글의 주인이 맞는지 확인
        if (!board.getUser().getId().equals(userId)) {
            return ResultDto.builder()
                    .message(ErrorCode.NOT_OWNER_BOARDS.getMessage())
                    .errorCode(ErrorCode.NOT_OWNER_BOARDS.getCode())
                    .build();
        }

        // 게시글 삭제
        boardRepository.delete(board);
        return ResultDto.builder()
                .message("success")
                .data(null)
                .errorCode("1111")
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

    public ResultDto<List<BoardSearchDTO>> boardSearch(Long userId, String keyword, String BCode,
        Days days, Times times, int page, int size) {
        List<BoardSearchNonUserDto> boards = boardRepositoryCustom.boardSearchNonUser(keyword,
            BCode, days, times, page, size);
        long totalCount = boardRepositoryCustom.boardSearchNonUserTotalCount(keyword, BCode, days,
            times);

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
        String BCode, Days days, Times times, int page, int size) {

        List<BoardSearchNonUserDto> boards = boardRepositoryCustom.boardSearchNonUser(keyword,
            BCode, days, times, page, size);
        long totalCount = boardRepositoryCustom.boardSearchNonUserTotalCount(keyword, BCode, days,
            times);

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
//                    .city("city")
//                    .district("district")
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
    public void groupsCreate(Users user, Board board, GroupsStatus groupsStatus) {
        Groups groups = Groups.builder()
                .user(user)
                .board(board)
                .status(groupsStatus)
                .build();

        // 연관관계 편의 메서드 호출
        user.addGroup(groups); // Users 엔티티에 Groups 인스턴스를 추가
        board.addGroup(groups); // Board 엔티티에 Groups 인스턴스를 추가

        groupsRepository.save(groups);
    }


}



