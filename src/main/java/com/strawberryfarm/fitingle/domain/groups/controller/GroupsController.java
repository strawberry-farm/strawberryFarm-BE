package com.strawberryfarm.fitingle.domain.groups.controller;

import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.service.GroupsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/user",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupsController {

	private final GroupsService groupsService;
	@RequestMapping("/group")
	private ResponseEntity<?> getMyGroups(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String status) {
		log.info("getMyGroups Controller Start");
		Long userId = Long.parseLong(userDetails.getUsername());

		return ResponseEntity.ok(groupsService.getMyGroups(userId,GroupsStatus.valueOf(status)));
	}
}
