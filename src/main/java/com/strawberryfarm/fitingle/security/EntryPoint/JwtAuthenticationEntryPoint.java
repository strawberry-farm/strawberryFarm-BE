package com.strawberryfarm.fitingle.security.EntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(
			ResultDto.builder()
				.message(ErrorCode.AUTHENTICATION_DENIED.getMessage())
				.data(null)
				.errorCode(ErrorCode.AUTHENTICATION_DENIED.getCode())
				.build()
		));
	}
}
