package com.strawberryfarm.fitingle.domain.apply.controller;

import com.strawberryfarm.fitingle.domain.apply.dto.ApplyRequestDto;
import com.strawberryfarm.fitingle.domain.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApplyController {
	private final ApplyService applyService;

	@PostMapping("/board/apply/{boardId}")
	public ResponseEntity<?> apply(
		@AuthenticationPrincipal UserDetails userDetails,
		ApplyRequestDto applyRequestDto,
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

	@GetMapping("/user/groups/apply/{boardId}")
	public ResponseEntity<?> getMyApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.getMyApply(boardId, userId));
	}

	@DeleteMapping("/user/groups/apply/{boardId}")
	public ResponseEntity<?> cancelApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.cancelApply(boardId,userId));
	}

	@PatchMapping("/user/groups/apply/{boardId}/accept")
	public ResponseEntity<?> acceptApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.acceptApply(boardId,userId));
	}

	@PatchMapping("/user/groups/apply/{boardId}/reject")
	public ResponseEntity<?> rejectApply(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long boardId) {
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(applyService.rejectApply(boardId,userId));
	}
 }
