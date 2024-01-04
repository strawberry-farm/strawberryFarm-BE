package com.strawberryfarm.fitingle.domain.board.service;

import com.strawberryfarm.fitingle.domain.board.dto.BoardDetailResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.CommentDTO;
import com.strawberryfarm.fitingle.domain.board.dto.QnaDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import com.strawberryfarm.fitingle.domain.field.dto.FieldsResponseDTO;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.tag.entity.Tag;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import com.strawberryfarm.fitingle.domain.wish.repository.WishRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.utils.S3Manager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final S3Manager s3Manager;

    private final BoardRepository boardRepository;
    private final FieldRepository fieldRepository;

    private final WishRepository wishRepository;

    private final GroupsRepository groupsRepository;

    private final UsersRepository usersRepository;

    //BOARDS 등록
    @Transactional
    public ResultDto<BoardRegisterResponseDTO> boardRegister(BoardRegisterRequestDTO boardRegisterRequestDTO,
                                                             List<MultipartFile> images) {
        try {
            Users user = usersRepository.findById(boardRegisterRequestDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Board board = Board.builder()
                    .user(user)
                    .titleContents(boardRegisterRequestDTO.getTitle())
                    .postStatus(PostStatus.Y)
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
            Field field = fieldRepository.findById(boardRegisterRequestDTO.getFieldId()).orElse(null);
            board.addField(field);

            //여기에 채팅

            Board savedBoard = boardRepository.save(board);

            // BoardRegisterResponseDTO 객체 생성 및 필요한 정보 설정
            BoardRegisterResponseDTO responseDTO = BoardRegisterResponseDTO.builder()
                    .boardsId(savedBoard.getId())
                    .title(savedBoard.getTitleContents())
                    .createdDate(savedBoard.getCreatedDate())
                    .updateDate(savedBoard.getUpdateDate())
                    .build();

            // ResultDto 객체 생성 및 반환
            return responseDTO.doResultDto("success", "1111");
        } catch (Exception e) {
            return new BoardRegisterResponseDTO().doResultDto("fail", "3000");
        }
    }

    //BOARDS 업데이트
    @Transactional
    public ResultDto<BoardUpdateResponseDTO> boardUpdate(Long boardsId,
                                                         BoardUpdateRequestDTO boardUpdateRequestDTO,
                                                         List<MultipartFile> updatedImages) {
        try {
        // 기존 Board 엔티티 찾기
        Board board = boardRepository.findById(boardsId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        //Board 엔티티의 필드 업데이트 (연관관계 엔티티 제외)
        board.updateBoard(boardUpdateRequestDTO);

        // 필드 업데이트
        Field field = fieldRepository.findById(boardUpdateRequestDTO.getFieldId())
                .orElseThrow(() -> new EntityNotFoundException("Field not found"));
        board.addField(field);

        // 기존 태그 제거 및 새 태그 추가
        board.clearTags();// 기존 태그 제거
        boardUpdateRequestDTO.getTags().forEach(tagName -> {
            Tag tag = Tag.builder()
                    .contents(tagName)
                    .build();
            board.addTag(tag);
        });

        // 기존 이미지 삭제 및 새 이미지 업로드
        board.getImages().forEach(image -> s3Manager.deleteFileFromS3(image.getImageUrl()));
        board.clearImages(); // 기존 이미지 제거
        // 새 이미지 업로드
        List<String> imageUrls = updatedImages.stream()
                .map(file -> s3Manager.uploadFileToS3(file, "boards/"))
                .collect(Collectors.toList());
        imageUrls.forEach(url -> {
            Image image = Image.builder()
                    .imageUrl(url)
                    .build();
            board.addImage(image);
        });

        // 게시물 저장
        Board savedBoard = boardRepository.save(board);
        // 응답 DTO 생성 및 반환
        BoardUpdateResponseDTO responseDTO = BoardUpdateResponseDTO.builder()
                .boardsId(savedBoard.getId())
                .title(savedBoard.getTitleContents())
                .createdDate(savedBoard.getCreatedDate())
                .updateDate(savedBoard.getUpdateDate())
                .build();

        return responseDTO.doResultDto("success", "1111");

        } catch (Exception e) {
            return new BoardUpdateResponseDTO().doResultDto("fail", "3001");
        }
    }

    //Boards 상세보기
    @Transactional(readOnly = true)
    public ResultDto<BoardDetailResponseDTO> boardDetail(Long boardsId) {

        Board board = boardRepository.findById(boardsId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        List<String> imageUrls = board.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        List<String> tags = board.getTags().stream()
                .map(Tag::getContents)
                .collect(Collectors.toList());

        Users user = board.getUser();


        BoardDetailResponseDTO boardDetailResponsedto = BoardDetailResponseDTO.builder()
                .boardId((board.getId()))
                .userId(user.getId())
                .nickname(user.getNickname())
                .status(board.getPostStatus().toString())
                .headcount(board.getHeadCount())
                .title(board.getTitleContents())
                .city(board.getCity())
                .district(board.getDistrict())
                .b_code(board.getBCode())
                .location(board.getLocation())
                .latitude(board.getLatitude())
                .longitude(board.getLongitude())
                .question(board.getQuestion())
                .days(board.getDays().toString())
                .times(board.getTimes().toString())

                //Q&A , Comments
                .qnas(board.getQnas().stream()
                         .map(this::convertToQnaDto)
                         .collect(Collectors.toList()))
                //field
                .fieldId(board.getField().getId())
                .fieldName(board.getField().getName())

                //images
                .images(imageUrls)

                //tags
                .tags(tags)
                .participantCount(checkParticipant(board.getId()))
                .wish(checkWish(user.getId(),board.getId()))
                .build();

        return boardDetailResponsedto.doResultDto("success","1111");
    }

    private boolean checkWish(Long userId,Long boardId) {
        Optional<Wish> wish = wishRepository.findByUserIdAndBoardId(userId, boardId);
        return wish.isPresent();
    }

    private int checkParticipant(Long boardId) {
        return groupsRepository.countByBoardId(boardId);
    }

    //해당 데이터 없어서
    private QnaDTO convertToQnaDto(Qna qna) {
        Users user = qna.getUser();
        Comment comment = qna.getComment();
        CommentDTO commentDto = null;
        if (comment != null) {
            commentDto = convertToCommentDto(comment);
        }
        return QnaDTO.builder()
                .userId(user.getId())
                .qnaId(qna.getId())
                .profile(user.getProfileImageUrl())
                .nickName(user.getNickname())
                .contents(qna.getContents())
                .status(qna.isStatus())
                .comment(commentDto)
                .build();
    }
    private CommentDTO convertToCommentDto(Comment comment) {
        Users user = comment.getUser();
        return CommentDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profile(user.getProfileImageUrl())
                .contents(comment.getContents())
                .build();
    }
}



