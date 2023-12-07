package com.strawberryfarm.fitingle.domain.field.service;

import com.strawberryfarm.fitingle.domain.field.dto.FieldsResponseDTO;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final S3Client s3Client;
    private final FieldRepository fieldRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String s3BaseUrl;

    private List<S3Object> getFieldObjectsFromS3() {
        //AWS SDK for Java V2에서 제공하는 클래스로, Amazon S3의 listObjectsV2
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix("fields/")
                .build();

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        List<S3Object> filteredList = new ArrayList<>();
        for (S3Object s3Object : listObjectsV2Response.contents()) {
            if (!s3Object.key().equals("fields/")) {
                filteredList.add(s3Object);
            }
        }
        return filteredList;
    }


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

    public void saveFieldsToDatabase() {
        List<S3Object> fieldObjects = getFieldObjectsFromS3();

        for (S3Object fieldObject : fieldObjects) {
            String objectKey = fieldObject.key(); // 예: "fields/baseball-20231021.jpg"
            String fieldName = objectKey.replace("fields/", "").split("-")[0]; // "baseball"

            Field field = Field.builder()
                    .name(mapToKoreanName(fieldName))
                    .imageUrl(constructImageUrl(objectKey)) // 실제 S3에서의 URL 구성
                    .build();

            fieldRepository.save(field);
        }
    }
    private String constructImageUrl(String objectKey) {
        return s3BaseUrl + objectKey;
    }

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
