package com.strawberryfarm.fitingle.domain.wish.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.wish.dto.WishDeleteResponseDTO;
import com.strawberryfarm.fitingle.domain.wish.dto.WishRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.wish.dto.WishRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.wish.service.WishService;
import com.strawberryfarm.fitingle.dto.ResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    WishService wishService;

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Wish 등록 컨트롤러")
    void wishRegister() throws Exception {
        // Given
        WishRegisterRequestDTO wishRegisterRequestDTO = new WishRegisterRequestDTO(1L);
        WishRegisterResponseDTO responseDTO = new WishRegisterResponseDTO(1L, true);

        given(wishService.wishRegister(any(WishRegisterRequestDTO.class), eq(1L)))
                .willReturn(new ResultDto<>(ErrorCode.SUCCESS.getMessage(), responseDTO, ErrorCode.SUCCESS.getCode()));

        // When & Then
        mockMvc.perform(post("/boards/wish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(wishRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.boardsId").exists())
                .andExpect(jsonPath("$.data.wishState").exists())
                .andDo(print());

        //호출 확인
        verify(wishService).wishRegister(any(WishRegisterRequestDTO.class), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Wish 삭제 컨트롤러")
    void wishDelete() throws Exception {
        //Given
        WishDeleteResponseDTO responseDTO = new WishDeleteResponseDTO(1L, 1L, false);
        Long wishId = 1L;

        given(wishService.wishDelete(eq(1L), eq(1L)))
                .willReturn(new ResultDto<>(ErrorCode.SUCCESS.getMessage(), responseDTO, ErrorCode.SUCCESS.getCode()));

        //When & Then
        mockMvc.perform(delete("/boards/wish/" + wishId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.boardsId").value(1L))
                .andExpect(jsonPath("$.data.wishId").value(1L))
                .andExpect(jsonPath("$.data.wishState").value("false"))
                .andDo(print());
    }
}