package com.strawberryfarm.fitingle.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.exception.CustomException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandleFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request,response);
		} catch (CustomException e) {
			setErrorResponse(response,e.getErrorCode());
		}
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
		throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(
			ResultDto.builder()
				.message(errorCode.getMessage())
				.data(null)
				.errorCode(errorCode.getCode())
				.build()
		));
	}
}
