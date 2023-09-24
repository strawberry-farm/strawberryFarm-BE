package com.strawberryfarm.fitingle.TestController;

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
	public ResponseEntity<TestDto> TestMethod(){
		String[] testStringArray = new String[4];
		testStringArray[0] = "아이유가";
		testStringArray[1] = "부릅니다.";
		testStringArray[2] = "너랑";
		testStringArray[3] = "나";
		TestDto testDto = TestDto.builder()
			.data1("이것은")
			.data2("테스트용 response 입니다.")
			.data3(23123)
			.data4(testStringArray).
			build();

		return ResponseEntity.ok(testDto);
	}
}
