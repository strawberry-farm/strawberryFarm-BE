package com.strawberryfarm.fitingle.domain.groups.service;


import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.apply.entity.Apply;
import com.strawberryfarm.fitingle.domain.apply.repository.ApplyRepository;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.groups.dto.GroupsGetMyGroupsResponseDto;
import com.strawberryfarm.fitingle.domain.groups.dto.PostDetailDto;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import com.strawberryfarm.fitingle.domain.wish.repository.WishRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupsRepository groupsRepository;
    private final UsersRepository usersRepository;

    private final BoardRepository boardRepository;

    private final ApplyRepository applyRepository;

    private final WishRepository wishRepository;

    public ResultDto<?> getMyGroups(Long userId, GroupsStatus status) {
        log.info("getMyGroups Service Start");
        System.out.println(status);

        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                    .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                    .data(null)
                    .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                    .build();
        }

        if (status == GroupsStatus.GUEST) {
            List<Board> boardByUser = applyRepository.findByUserId(userId).stream()
                    .map(Apply::getBoard)
                    .collect(Collectors.toList());

            List<PostDetailDto> boards = boardByUser.stream().map(board -> PostDetailDto.builder()
                            .boardId(board.getId())
                            .title(board.getTitle())
                            .wish(checkUserWishOnBoard(userId, board.getId())) // 가정: 사용자의 찜 상태 확인
                            .location(board.getLocation())
                            .times(board.getTimes())
                            .days(board.getDays())
                            .headCount(board.getHeadCount())
                            .participantCount(checkParticipant(board.getId()))
                            .status(GroupsStatus.GUEST)
                            .fieldName(board.getField().getName())
                            .imagesUrl(board.getImages().stream().map(Image::getImageUrl)
                                    .collect(Collectors.toList())) // 이미지 URL 추출
                            .build())
                    .collect(Collectors.toList());

            return ResultDto.<GroupsGetMyGroupsResponseDto>builder()
                    .message(ErrorCode.SUCCESS.getMessage())
                    .data(GroupsGetMyGroupsResponseDto.builder().boards(boards).build())
                    .errorCode(ErrorCode.SUCCESS.getCode())
                    .build();
        } else if (status == GroupsStatus.HOST) {
            List<Board> boardsHostedByUser = boardRepository.findByUserId(userId);

            List<PostDetailDto> boards = boardsHostedByUser.stream().map(board -> PostDetailDto.builder()
                            .boardId(board.getId())
                            .title(board.getTitle())
                            .wish(checkUserWishOnBoard(userId, board.getId())) // 가정: 사용자의 찜 상태 확인
                            .location(board.getLocation())
                            .times(board.getTimes())
                            .days(board.getDays())
                            .fieldName(board.getField().getName())
                            .headCount(board.getHeadCount())
                            .participantCount(checkParticipant(board.getId())) // 참가자 수 계산
                            .status(GroupsStatus.HOST)
                            .imagesUrl(board.getImages().stream().map(Image::getImageUrl)
                                    .collect(Collectors.toList())) // 이미지 URL 추출
                            .build())
                    .collect(Collectors.toList());

            return ResultDto.<GroupsGetMyGroupsResponseDto>builder()
                    .message(ErrorCode.SUCCESS.getMessage())
                    .data(GroupsGetMyGroupsResponseDto.builder().boards(boards).build())
                    .errorCode(ErrorCode.SUCCESS.getCode())
                    .build();
        }
        return ResultDto.builder()
                .errorCode(ErrorCode.NOT_EXIST_GROUP.getCode())
                .data(null)
                .message(ErrorCode.NOT_EXIST_GROUP.getMessage())
                .build();
    }

    private int checkParticipant(Long boardId) {
        return groupsRepository.countByBoardId(boardId);
    }

    private boolean checkUserWishOnBoard(Long userId, Long boardId) {
        Optional<Wish> byUserIdAndBoardId = wishRepository.findByUserIdAndBoardId(userId, boardId);
        if (byUserIdAndBoardId.isPresent()) {
            return true;
        }
        return false;
    }

}

