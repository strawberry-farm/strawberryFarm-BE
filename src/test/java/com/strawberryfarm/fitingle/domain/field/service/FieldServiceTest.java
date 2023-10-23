package com.strawberryfarm.fitingle.domain.field.service;

import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class FieldServiceTest {

    // AWS S3 관련 작업을 테스트하기 위해 S3Client를 Mock
    @Mock
    private S3Client s3Client;

    // 데이터베이스 관련 작업을 테스트하기 위해 FieldRepository를 Mock
    @Mock
    private FieldRepository fieldRepository;

    // Mock된 의존성들을 FieldService에 자동으로 주입
    @InjectMocks
    private FieldService fieldService;

    // 각 테스트 전에 Mock들을 초기화
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("S3에서 필드 데이터를 가져와 데이터베이스에 저장하는 기능 검증")
    void saveFieldsToDatabase() {
        // Given

        // AWS S3 응답을 시뮬레이션하기 위해 S3Object를 Mock
        S3Object object = mock(S3Object.class);
        when(object.key()).thenReturn("fields/baseball-20231021.jpg");

        // AWS S3에서 객체를 나열하는 응답을 시뮬레이션하기 위해 응답을 Mock
        ListObjectsV2Response response = mock(ListObjectsV2Response.class);
        when(response.contents()).thenReturn(Arrays.asList(object));

        // Mock된 S3 클라이언트의 동작을 정의
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        // When

        // 테스트할 메서드를 호출
        fieldService.saveFieldsToDatabase();

        // Then

        // 리포지토리의 save 메서드가 호출되었는지 확인
        verify(fieldRepository).save(any(Field.class));
    }

    @Test
    @DisplayName("모든 필드를 정상적으로 조회하는 기능 검증")
    void getAllFields() {
        // Given

        // 데이터베이스의 데이터를 시뮬레이션하기 위해 Field 엔터티를 Mock
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(1L);
        when(field.getName()).thenReturn("야구");
        when(field.getImageUrl()).thenReturn("some-url");

        // Mock된 리포지토리의 동작을 정의
        when(fieldRepository.findAll()).thenReturn(Collections.singletonList(field));

        // When
        // 테스트할 메서드를 호출
        fieldService.getAllFields();

        // Then

        // 리포지토리의 findAll 메서드가 호출되었는지 확인
        verify(fieldRepository).findAll();
    }
}