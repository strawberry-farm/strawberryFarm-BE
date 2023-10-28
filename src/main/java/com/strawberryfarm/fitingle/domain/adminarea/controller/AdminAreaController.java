package com.strawberryfarm.fitingle.domain.adminarea.controller;


import com.strawberryfarm.fitingle.domain.adminarea.service.AdminAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/contents", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminAreaController {

    private final AdminAreaService adminAreaService;

    @GetMapping("/adminArea")
    public ResponseEntity<?> getContentsList() {
        return ResponseEntity.ok(adminAreaService.getAllAdminAreas());
    }
}