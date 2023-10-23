package com.strawberryfarm.fitingle.domain.field.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.strawberryfarm.fitingle.domain.field.dto.FieldsReponseDTO;
import com.strawberryfarm.fitingle.domain.field.service.FieldService;
import com.strawberryfarm.fitingle.dto.ResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class FieldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FieldService fieldService;

    @BeforeEach
    public void setup() {
        FieldsReponseDTO fieldsReponseDTO = FieldsReponseDTO.builder()
                .fieldId(1L)
                .fieldName("축구")
                .image("https://strawberry-bucket.s3.ap-northeast-2.amazonaws.com/fields/soccer-20231021.jpg")
                .build();

        ResultDto resultDto = fieldsReponseDTO.doResultDto("분야 정보를 성공적으로 가져왔습니다.", "1111");
        when(fieldService.getAllFields()).thenReturn(resultDto);
    }

    @Test
    @DisplayName("Fields를 정상적으로 조회하는지 검증")
    public void testGetAllFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/field")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("분야 정보를 성공적으로 가져왔습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fieldId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fieldName").value("축구"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                        .value("https://strawberry-bucket.s3.ap-northeast-2.amazonaws.com/fields/soccer-20231021.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("1111"));
    }
}