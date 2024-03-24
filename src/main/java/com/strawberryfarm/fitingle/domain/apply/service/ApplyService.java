package com.strawberryfarm.fitingle.domain.apply.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.apply.dto.ApplyChangeResponseDto;
import com.strawberryfarm.fitingle.domain.apply.dto.ApplyDto;
import com.strawberryfarm.fitingle.domain.apply.dto.ApplyListResponseDto;
import com.strawberryfarm.fitingle.domain.apply.dto.ApplyRequestDto;
import com.strawberryfarm.fitingle.domain.apply.dto.ApplyResponseDto;
import com.strawberryfarm.fitingle.domain.apply.entity.Apply;
import com.strawberryfarm.fitingle.domain.apply.entity.ApplyStatus;
import com.strawberryfarm.fitingle.domain.apply.repository.ApplyRepository;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.repository.GroupsRepository;
import com.strawberryfarm.fitingle.domain.groups.service.GroupsService;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyService {
	private final ApplyRepository applyRepository;
	private final BoardRepository boardRepository;
	private final UsersRepository usersRepository;

	private final GroupsService groupsService;

	private final GroupsRepository groupsRepository;


	@Transactional
	public ResultDto<?> apply(ApplyRequestDto applyRequestDto, Long boardId, Long userId) {
		Optional<Users> findUsers = usersRepository.findUsersById(userId);
		Optional<Board> findBoards = boardRepository.findById(boardId);

		if (!findUsers.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!findBoards.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Users findUser = findUsers.get();
		Board findBoard = findBoards.get();

		Apply newApply = Apply.builder()
			.contents(applyRequestDto.getContents())
			.status(ApplyStatus.I)
			.build();

		newApply.setUser(findUser);
		newApply.setBoard(findBoard);

		//todo 1.모임 테이블에 추가(상태값을 wait으로)
		//유저랑, 보드, 상태값을 저장한다.
		//기존에 없는지 확인을해야하지 않을까?
		groupsService.groupsCreate(findUsers.get(), findBoards.get(), GroupsStatus.WAIT);

		//todo 2. apply 추가
		applyRepository.save(newApply);

		return ApplyResponseDto.builder()
			.contents("신청 완료")
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> getApplyList(Long boardId,Long userId) {
		boolean existUsers = usersRepository.existsById(userId);
		Optional<Board> findBoards = boardRepository.findById(boardId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!findBoards.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Board findBoard = findBoards.get();

		List<Apply> applyList = applyRepository.getAppliesByBoardId(boardId);
		List<ApplyDto> result = new ArrayList<>();

		applyListToApplyDtoList(findBoard, applyList, result);

		return ApplyListResponseDto.builder()
			.applyList(result)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	private static void applyListToApplyDtoList(Board findBoard, List<Apply> applyList, List<ApplyDto> result) {
		for (Apply apply : applyList) {
			result.add(ApplyDto.builder().status(apply.getStatus())
				.contents(apply.getContents())
				.question(findBoard.getQuestion())
				.build());
		}
	}

	public ResultDto<?> getMyApply(Long boardId,Long userId) {
		Optional<Users> findUsers = usersRepository.findUsersById(userId);
		Optional<Board> findBoards = boardRepository.findById(boardId);

		if (!findUsers.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!findBoards.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Board board = findBoards.get();
		Apply apply = applyRepository.getApplyByBoardIdAndUserId(boardId,userId);

		return ApplyDto.builder()
			.question(board.getQuestion())
			.contents(apply.getContents())
			.status(apply.getStatus()).build()
			.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> acceptApply(Long boardId, Long userId) {
		boolean existUsers = usersRepository.existsById(userId);
		boolean existBoard = boardRepository.existsById(boardId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!existBoard) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Apply apply = applyRepository.getApplyByBoardIdAndUserId(boardId,userId);

		if (apply == null) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
				.build();
		}

		apply.modifyStatus(ApplyStatus.Y);

		//todo 여기에 모임에 상태값을 guest로 바꿈
		Optional<Groups> groupsOptional = groupsRepository.findByUserIdAndBoardId(boardId,userId);
		if (!groupsOptional.isPresent()) {
			//여기에 조치 필요
		}
		Groups group = groupsOptional.get();
		group.changeStatusToGuest(); // 상태값을 GUEST로 변경
		groupsRepository.save(group); // 변경된 상태를 저장

		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.Y)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> cancelApply(Long boardId, Long userId) {
		boolean existUsers = usersRepository.existsById(userId);
		boolean existBoard = boardRepository.existsById(boardId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!existBoard) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		//todo 여기에 모임의 wait를 삭제하고 apply 거절로 바꾸기 해야함.

		Apply apply = applyRepository.getApplyByBoardIdAndUserId(boardId,userId);

		if (apply == null) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
				.build();
		}

		applyRepository.delete(apply);

		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.C)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> rejectApply(Long boardId, Long userId) {
		boolean existUsers = usersRepository.existsById(userId);
		boolean existBoard = boardRepository.existsById(boardId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		if (!existBoard) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Apply apply = applyRepository.getApplyByBoardIdAndUserId(boardId,userId);

		if (apply == null) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
				.build();
		}

		apply.modifyStatus(ApplyStatus.N);

		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.N)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}
}
