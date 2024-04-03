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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

		// 1.게시물의 소유자가 신청을 시도하는 경우 에러 반환
		if (findBoard.getUser().getId().equals(userId)) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_APPLY_YOUR_BOARD.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_APPLY_YOUR_BOARD.getCode())
					.build();
		}
		// 2.이미 신청이 있는지 확인(거절은 다시 신청 가능)
		Optional<Apply> existingApplyOpt = applyRepository.findByUserIdAndBoardId(userId, boardId);
		if (existingApplyOpt.isPresent()) {
			Apply existingApply = existingApplyOpt.get();
			ApplyStatus status = existingApply.getStatus();

			// 상태가 'I' 또는 'Y'인 경우, 에러 처리
			if (status.equals(ApplyStatus.I) || status.equals(ApplyStatus.Y)) {
				return ResultDto.builder()
						.message(ErrorCode.ALREADY_APPLIED.getMessage())
						.data(null)
						.errorCode(ErrorCode.ALREADY_APPLIED.getCode())
						.build();
			}
			// 상태가 'C' 또는 'N'인 경우, 상태를 'I'로 변경
			else if (status.equals(ApplyStatus.C) || status.equals(ApplyStatus.N)) {
				//상태값 제거하지말고 남기고
				existingApply.modifyStatus(ApplyStatus.I);
				applyRepository.save(existingApply);
				return ApplyResponseDto.builder()
						.contents("신청 완료")
						.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
			}
		} else {

			if(findBoard.getHeadCount() <= groupsRepository.countByBoardId(findBoard.getId())){
				return ResultDto.builder()
						.message(ErrorCode.ALREADY_FULLED.getMessage())
						.data(null)
						.errorCode(ErrorCode.ALREADY_FULLED.getCode())
						.build();
			}

			// 신청이 존재하지 않는 경우, 새로운 신청 추가
			Apply newApply = Apply.builder()
					.contents(applyRequestDto.getContents())
					.status(ApplyStatus.I)
					.user(findUser)
					.board(findBoard)
					.build();

			applyRepository.save(newApply);

			return ApplyResponseDto.builder()
					.contents("신청 완료")
					.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
		}
		return ResultDto.builder()
				.message("처리되지 않은 상태입니다.")
				.data(null)
				.errorCode("UNHANDLED_STATE")
				.build();
	}



	public ResultDto<?> getApplyList(Long boardId,Long userId) {

		//1.유저확인
		boolean existUsers = usersRepository.existsById(userId);
		if (!existUsers) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_USERS.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
					.build();
		}
		List<ApplyDto> result = new ArrayList<>();

		//2.보드 id null -> 전체 조회
		if (boardId == null) {
			List<Board> ownedBoards = boardRepository.findByUserId(userId); // 사용자가 소유한 모든 게시판 조회
			for (Board board : ownedBoards) {
				List<Apply> applyList = applyRepository.findByBoardId(board.getId());
				applyListToApplyDtoList(board, applyList, result);
			}

		//3.보드 아이디가 있을때.
		}else{
			Optional<Board> findBoard = boardRepository.findByIdAndUserId(boardId,userId);;
			if (!findBoard.isPresent()) {
				return ResultDto.builder()
						.message(ErrorCode.NOT_OWNER_BOARDS.getMessage())
						.data(null)
						.errorCode(ErrorCode.NOT_OWNER_BOARDS.getCode())
						.build();
			}
			List<Apply> applyList = applyRepository.findByBoardId(boardId);
			applyListToApplyDtoList(findBoard.get(), applyList, result);
		}
		//4.값이 비어있을때.
		if(result.isEmpty()){
			return ResultDto.builder()
					.message(ErrorCode.SUCCESS.getMessage())
					.data(null)
					.errorCode(ErrorCode.SUCCESS.getCode())
					.build();
		}
		return ApplyListResponseDto.builder()
			.applyList(result)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	private static void applyListToApplyDtoList(Board board, List<Apply> applyList, List<ApplyDto> result) {

		for (Apply apply : applyList) {
			Users user = apply.getUser(); // 가정: Apply 객체가 User 객체에 접근할 수 있음
			result.add(ApplyDto.builder()
					.status(apply.getStatus())
					.profileUrl(user.getNickname())
					.nickName(user.getNickname())
					.aboutMe(user.getAboutMe())
					.applyId(apply.getId())
					.contents(apply.getContents())
					.question(board.getQuestion())
					.build());
		}
	}

	private ApplyDto convertToApplyDto(Apply apply) {
		// Apply 엔티티를 ApplyDto 객체로 변환하는 메서드
		return ApplyDto.builder()
				.status(apply.getStatus())
				.applyId(apply.getId())
				.contents(apply.getContents())
				.question(apply.getBoard().getQuestion())
				.build();
	}
	public ResultDto<?> getMyApply(Long boardId, Long userId) {
		// 1.사용자 존재 여부 확인
		if (!usersRepository.existsById(userId)) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_USERS.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
					.build();
		}

		List<ApplyDto> result = new ArrayList<>();

		// 2.boardId가 null인 경우: 사용자가 신청한 모든 게시물 조회
		if (boardId == null) {
			List<Apply> applyList = applyRepository.findByUserId(userId);
			for (Apply apply : applyList) {
				// 각 Apply에 대응하는 Board 정보를 함수에 전달
				applyListToApplyDtoList(apply.getBoard(), Collections.singletonList(apply), result);
			}
		} else {
			// 3.boardId가 제공된 경우
			Optional<Board> boardOptional = boardRepository.findById(boardId);
			if (!boardOptional.isPresent()) {
				return ResultDto.builder()
						.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
						.data(null)
						.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
						.build();
			}
			Board board = boardOptional.get();
			Optional<Apply> applyOptional = applyRepository.findByUserIdAndBoardId(userId, boardId);
			if (!applyOptional.isPresent()) {
				return ResultDto.builder()
						.message(ErrorCode.NOT_EXIST_APPLY.getMessage())
						.data(null)
						.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
						.build();
			}
			List<Apply> applyList = Collections.singletonList(applyOptional.get());
			applyListToApplyDtoList(board, applyList, result);
		}
		//4.결과 비어있는경우
		if (result.isEmpty()) {
			return ResultDto.builder()
					.message(ErrorCode.SUCCESS.getMessage())
					.data("null")
					.errorCode(ErrorCode.SUCCESS.getCode())
					.build();
		}
		return ApplyListResponseDto.builder()
				.applyList(result)
				.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}


	@Transactional
	public ResultDto<?> acceptApply(Long applyId, Long userId) {

		Optional<Apply> existApply = applyRepository.findById(applyId);

		if (!existApply.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_APPLY.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
				.build();
		}

		Apply apply = existApply.get();


		Optional<Users> existUsers = usersRepository.findUsersById(userId);
		if (!existUsers.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Board findBoard = existApply.get().getBoard();

		//반장인지 체크
		if(findBoard.getUser().getId() != userId){
			return  ResultDto.builder()
					.message(ErrorCode.NOT_OWNER_BOARDS.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_OWNER_BOARDS.getCode())
					.build();
		}

		//그룹인원을 찾아서 참여가 가능한지 조회
		if(findBoard.getHeadCount() <= groupsRepository.countByBoardId(findBoard.getId())){
			return ResultDto.builder()
					.message(ErrorCode.ALREADY_FULLED.getMessage())
					.data(null)
					.errorCode(ErrorCode.ALREADY_FULLED.getCode())
					.build();
		}

		//상태 Y로 변경
		apply.modifyStatus(ApplyStatus.Y);

		//모임에 상태값을 guest로 바꿈
		Optional<Groups> groupsOptional = groupsRepository.findByUserIdAndBoardId(apply.getBoard().getId(),userId);
		if (!groupsOptional.isPresent()) {
			return  ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_GROUP.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_GROUP.getCode())
					.build();
		}

		// 상태값을 GUEST로 변경
		groupsService.groupsCreate(existUsers.get(), findBoard, GroupsStatus.GUEST);


		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.Y)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	@Transactional
	public ResultDto<?> cancelApply(Long applyId, Long userId) {

		Optional<Apply> existApply = applyRepository.findById(applyId);

		if (!existApply.isPresent()) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_APPLY.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
					.build();
		}

		boolean existUsers = usersRepository.existsById(userId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		Apply apply = existApply.get();

		if (apply.getStatus() == ApplyStatus.Y) {
			return ResultDto.builder()
					.message(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getMessage())
					.data(null)
					.errorCode(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getCode())
					.build();
		}

		Long boardId = apply.getBoard().getId();
		Optional<Groups> groupOptional = groupsRepository.findByUserIdAndBoardId(userId, boardId);

		if (!groupOptional.isPresent()) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_GROUP.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_GROUP.getCode()) // 적절한 에러 코드 사용
					.build();
		}

		Groups group = groupOptional.get();

		if (group.getStatus() == GroupsStatus.GUEST) {
			return ResultDto.builder()
					.message(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getMessage())
					.data(null)
					.errorCode(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getCode())
					.build();
		}
		apply.modifyStatus(ApplyStatus.C);
		//applyRepository.delete(apply);

		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.C)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	@Transactional
	public ResultDto<?> rejectApply(Long applyId, Long userId) {
		boolean existUsers = usersRepository.existsById(userId);

		if (!existUsers) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_BOARDS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_BOARDS.getCode())
				.build();
		}

		Optional<Apply> existApply = applyRepository.findById(applyId);

		if (!existApply.isPresent()) {
			return ResultDto.builder()
					.message(ErrorCode.NOT_EXIST_APPLY.getMessage())
					.data(null)
					.errorCode(ErrorCode.NOT_EXIST_APPLY.getCode())
					.build();
		}
		Apply apply = existApply.get();

		if (apply.getStatus() == ApplyStatus.Y) {
			return ResultDto.builder()
					.message(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getMessage())
					.data(null)
					.errorCode(ErrorCode.CANNOT_CANCEL_APPROVED_APPLY.getCode())
					.build();
		}

		apply.modifyStatus(ApplyStatus.N);

		return ApplyChangeResponseDto.builder()
			.beforeStatus(ApplyStatus.I)
			.curStatus(ApplyStatus.N)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}
}
