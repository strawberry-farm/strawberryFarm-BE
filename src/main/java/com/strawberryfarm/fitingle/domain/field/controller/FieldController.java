package com.strawberryfarm.fitingle.domain.field.controller;

import com.strawberryfarm.fitingle.domain.field.service.FieldService;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/field", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;

    @GetMapping
    public ResponseEntity<ResultDto> getAllFields() {
        return ResponseEntity.ok(fieldService.getAllFields());
    }
}
