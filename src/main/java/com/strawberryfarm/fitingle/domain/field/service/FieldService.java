package com.strawberryfarm.fitingle.domain.field.service;

import com.strawberryfarm.fitingle.annotation.Trace;
import com.strawberryfarm.fitingle.domain.field.dto.FieldsResponseDTO;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.utils.S3Manager;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;

    private final S3Manager s3Manager;

    private String mapToKoreanName(String fieldName) {
        // 나머지 스포츠와 그에 대응하는 한글 이름 추가 필요.
        Map<String, String> nameMapping = new HashMap<>();
        nameMapping.put("baseball", "야구");
        nameMapping.put("basketball", "농구");
        nameMapping.put("soccer", "축구");
        nameMapping.put("swimming", "수영");
        nameMapping.put("golf", "골프");
        nameMapping.put("running", "런닝");
        nameMapping.put("tennis", "테니스");
        return nameMapping.getOrDefault(fieldName, fieldName);
    }

    @Trace
    public void saveFieldsToDatabase() {
        List<S3Object> fieldObjects = s3Manager.listObjectsFromS3("fields/");
        for (S3Object fieldObject : fieldObjects) {
            String objectKey = fieldObject.key(); // 예: "fields/baseball-20231021.jpg"
            String fieldName = objectKey.replace("fields/", "").split("-")[0]; // "baseball"
            Field field = Field.builder()
                    .name(mapToKoreanName(fieldName))
                    .imageUrl(s3Manager.getFileUrl(objectKey)) // 실제 S3에서의 URL 구성
                    .build();
            fieldRepository.save(field);
        }
    }


    @Trace
    public ResultDto getAllFields() {
        List<Field> fields = fieldRepository.findAll();
        List<FieldsResponseDTO> fieldDtos = new ArrayList<>();
        for (Field field : fields) {
            FieldsResponseDTO dto = FieldsResponseDTO.builder()
                    .fieldId(field.getId())
                    .fieldName(field.getName())
                    .image(field.getImageUrl())
                    .build();
            fieldDtos.add(dto);
        }
        return ResultDto.builder()
                .message("분야 정보를 성공적으로 가져왔습니다.")
                .errorCode("1111")
                .data(fieldDtos)
                .build();
    }
}
