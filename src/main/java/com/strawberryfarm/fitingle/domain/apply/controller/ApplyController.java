package com.strawberryfarm.fitingle.domain.apply.controller;

import com.strawberryfarm.fitingle.domain.apply.dto.ApplyRequestDto;
import com.strawberryfarm.fitingle.domain.apply.service.ApplyService;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApplyController {
	private final ApplyService applyService;

	@PostMapping("/board/apply/{boardId}")
	public ResponseEntity<?> apply(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody ApplyRequestDto applyRequestDto,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.apply(applyRequestDto,boardId,userId));
	}

	@GetMapping("/user/groups/applyList/{boardId}")
	public ResponseEntity<?> getApplyList(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(applyService.getApplyList(boardId,userId));
	}

	@GetMapping("/user/groups/applyList/all")
	public ResponseEntity<?> getApplyList(
		@AuthenticationPrincipal UserDetails userDetails) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(applyService.getApplyList(userId));
	}

	@GetMapping("/user/groups/apply/{boardId}")
	public ResponseEntity<?> getMyApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(applyService.getMyApply(boardId, userId));
	}

	@GetMapping("/user/groups/apply/all")
	public ResponseEntity<?> getMyApply(@AuthenticationPrincipal UserDetails userDetails) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(applyService.getMyApply(userId));
	}

	@DeleteMapping("/user/groups/apply/{applyId}")
	public ResponseEntity<?> cancelApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long applyId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.cancelApply(applyId,userId));
	}

	@PatchMapping("/user/groups/apply/{applyId}/accept")
	public ResponseEntity<?> acceptApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long applyId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.acceptApply(applyId,userId));
	}

	@PatchMapping("/user/groups/apply/{applyId}/reject")
	public ResponseEntity<?> rejectApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long applyId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.rejectApply(applyId,userId));
	}

//	@GetMapping("/user/groups")
//	public ResponseEntity<?> mygroups(
//			@AuthenticationPrincipal UserDetails userDetails,@RequestParam GroupsStatus status) {
//		Long userId = Long.parseLong(userDetails.getUsername());
//		return ResponseEntity.ok(applyService.myBoardList(status,userId));
//	}

 }
