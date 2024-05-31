package com.strawberryfarm.fitingle.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@RequiredArgsConstructor
public class S3Manager {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String s3BaseUrl;

    private final S3Client s3Client;


    //S3에서 특정 디렉토리의 객체 목록을 리스트로 반환
    public List<S3Object> listObjectsFromS3(String directoryPath) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(directoryPath)
                .build();
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        List<S3Object> filteredList = new ArrayList<>();
        for (S3Object s3Object : listObjectsV2Response.contents()) {
            if (!s3Object.key().equals(directoryPath)) {
                filteredList.add(s3Object);
            }
        }
        return filteredList;
    }

    //S3에서 특정 디렉토리에 파일을 등록하고 해당 객체 URL 반환
    public String uploadFileToS3(MultipartFile file, String directoryPath) {
        try {
            // 고유한 파일 이름 생성
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String key = directoryPath + fileName; // S3에 저장될 전체 파일 경로와 이름
            // 파일의 MIME 타입 결정
            String mimeType = file.getContentType();
            // S3에 업로드할 객체 생성 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read") // public-read 권한으로 설정
                    .contentType(mimeType) // MIME 타입 설정
                    .build();
            // S3에 파일 업로드 실행
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // 생성된 파일의 URL 반환
            return getFileUrl(key);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    //S3에서 해당 URL 맞는 객체 삭제
    public void deleteFileFromS3(String fileUrl) {
        //System.out.println("Attempting to delete file from S3: " + fileUrl);
        try {
            String fileName = fileUrl.substring(s3BaseUrl.length());
           // System.out.println("Resolved file name: " + fileName);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            //System.out.println("File deleted successfully");
        } catch (Exception e) {
            //System.err.println("Error deleting file from S3: " + e.getMessage());
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }


    //URL을 생성 메소드
    public String getFileUrl(String fileName) {
        return s3BaseUrl + fileName;
    }

    //파일 이름 충돌을 방지하기 위해 고유한 파일 이름을 생성하는 메소드
    private String generateUniqueFileName(String originalFilename) {
        return System.currentTimeMillis() + "_" + originalFilename;
    }
}
