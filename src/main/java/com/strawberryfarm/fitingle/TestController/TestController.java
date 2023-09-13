package com.strawberryfarm.fitingle.TestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test",produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {
	@GetMapping("")
	public HttpStatus TestMethod(){
		System.out.println("연결성확인");
		return HttpStatus.OK;
	}
}
