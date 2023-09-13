package com.strawberryfarm.fitingle.TestController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
	@GetMapping("/test")
	public String TestMethod(){
		return "testest";
	}
}
