package com.strawberryfarm.fitingle.domain.groups.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.board.dto.BoardDetailResponseDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.groups.dto.GroupsGetMyGroupsResponseDto;
import com.strawberryfarm.fitingle.domain.groups.dto.PostDetailDto;
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupsService {

	private final GroupsRepository groupsRepository;
	private final UsersRepository usersRepository;
	public ResultDto<?> getMyGroups(Long userId,GroupsStatus status) {
		log.info("getMyGroups Service Start");

		Optional<Users> findUsers = usersRepository.findById(userId);

		if (!findUsers.isPresent()) {
			return ResultDto.builder()
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.data(null)
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.build();
		}

		Users findUser = findUsers.get();

		List<Groups> groups;

		if (status == GroupsStatus.ALL) {
			groups = findUser.getGroups();
		} else {
			groups = findUser.getGroups().stream().filter(gorup -> gorup.getStatus() == status).collect(
				Collectors.toList());
		}

		List<PostDetailDto> posts = new ArrayList<>();

		for (Groups g : groups) {
			Board board = g.getBoard();
			int participantCount = groupsRepository.getParticipantCount(board.getId());

			List<String> imageUrls = board.getImages().stream().map(Image::getImageUrl)
					.collect(Collectors.toList());
			posts.add(PostDetailDto.builder()
					.postId(board.getId())
					.title(board.getTitle())
					.location(board.getLocation())
					.times(board.getTimes())
					.days(board.getDays())
					.headCount(board.getHeadCount().intValue())
					.participantCount(participantCount)
					.status(g.getStatus())
					.imagesUrl(imageUrls)
				.build());
		}

		log.info("getMyGroups Service End");
		return GroupsGetMyGroupsResponseDto.builder()
			.posts(posts)
			.build()
			.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}
}
