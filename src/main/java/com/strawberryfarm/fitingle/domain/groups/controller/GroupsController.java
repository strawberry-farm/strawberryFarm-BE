package com.strawberryfarm.fitingle.domain.groups.controller;


import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.domain.groups.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GroupsController {
    private final GroupService groupService;
    @GetMapping("/user/groups")
    public ResponseEntity<?> getMyGroups(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String status) {
        Long userId = Long.parseLong(userDetails.getUsername());
        GroupsStatus groupStatus = GroupsStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(groupService.getMyGroups(userId, groupStatus));
    }
}
