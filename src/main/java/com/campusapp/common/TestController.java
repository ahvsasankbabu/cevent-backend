package com.campusapp.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {
	
	@GetMapping("/health")
	public ResponseEntity<ApiResponse<String>> health()
	{
		return ResponseEntity.ok(ApiResponse.success("Campus app is running", "ok"));
	}

}
