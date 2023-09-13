package com.strawberryfarm.fitingle.TestController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@GetMapping("/test")
	public ResponseEntity<String> TestMethod(){
		System.out.println("연결성확인");
		return ResponseEntity.ok("TEST API");
	}
}
