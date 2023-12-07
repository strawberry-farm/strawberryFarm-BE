package com.strawberryfarm.fitingle.domain.board.service;

import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import com.strawberryfarm.fitingle.domain.board.repository.BoardRepository;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.field.repository.FieldRepository;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.tag.entity.Tag;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class BoardService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String s3BaseUrl;

    private final S3Client s3Client;

    private final BoardRepository boardRepository;
    private final FieldRepository fieldRepository;


    @Transactional
    public ResultDto<BoardRegisterResponseDTO> boardRegister(BoardRegisterRequestDTO boardRegisterRequestDTO, List<MultipartFile> images) {
        try {
            Board board = Board.builder()
                    .titleContents(boardRegisterRequestDTO.getTitle())
                    .postStatus(PostStatus.Y)
                    .city(boardRegisterRequestDTO.getCity())
                    .district(boardRegisterRequestDTO.getDistrict())
                    .headCount(boardRegisterRequestDTO.getHeadcount())
                    .BCode(boardRegisterRequestDTO.getB_code())
                    .location(boardRegisterRequestDTO.getLocation())
                    .latitude(boardRegisterRequestDTO.getLatitude())
                    .longitude(boardRegisterRequestDTO.getLongitude())
                    .question(boardRegisterRequestDTO.getQuestion())
                    .days(Days.valueOf(boardRegisterRequestDTO.getDays()))
                    .times(Times.valueOf(boardRegisterRequestDTO.getTimes()))
                    .build();

            // 연관관계 세팅 (Tag)
            boardRegisterRequestDTO.getTags().forEach(tagName -> {
                Tag tag = Tag.builder()
                        .contents(tagName)
                        .build();
                board.addTag(tag);
            });

            // 이미지 처리
            List<String> imageUrls = images.stream()
                    .map(this::uploadImageToS3)
                    .collect(Collectors.toList());

            // 이미지 URL을 사용하여 Image 엔티티 생성 및 Board 엔티티에 추가
            imageUrls.forEach(url -> {
                Image image = Image.builder()
                        .imageUrl(url)
                        .build();
                board.addImage(image);
            });

            // 연관관계 세팅 (field)
            // 예) fieldId를 기반으로 Field 객체를 조회한 후, board에 설정
            Field field = fieldRepository.findById(boardRegisterRequestDTO.getFieldId()).orElse(null);
            board.addField(field);

            Board savedBoard = boardRepository.save(board);

            // BoardRegisterResponseDTO 객체 생성 및 필요한 정보 설정
            BoardRegisterResponseDTO responseDTO = BoardRegisterResponseDTO.builder()
                    .postsId(savedBoard.getId())
                    .title(savedBoard.getTitleContents())
                    .createdDate(savedBoard.getCreatedDate())
                    .updateDate(savedBoard.getUpdateDate())
                    .build();

            // ResultDto 객체 생성 및 반환
            return responseDTO.doResultDto("success", "1111");
        }catch (Exception e){
            return new BoardRegisterResponseDTO().doResultDto("fail", "3000");
        }
    }
    private String uploadImageToS3(MultipartFile file) {
        try {
            String directoryPath = "boards/"; // S3 내의 저장하고 싶은 디렉토리 경로
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String key = directoryPath + fileName; // S3에 저장될 전체 파일 경로와 이름

            // 파일의 MIME 타입을 결정
            String mimeType = file.getContentType();

            // S3에 업로드할 객체 생성 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read") // public-read 권한으로 설정
                    .contentType(mimeType) // 여기에 MIME 타입을 설정
                    .build();
            // S3에 파일 업로드 실행
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // 생성된 파일의 URL을 반환합니다.
            return getFileUrl(key);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 등록이 실패했습니다.", e);
        }
    }

    // S3 버킷에서 파일의 URL을 생성한다.
    private String getFileUrl(String fileName) {
        // S3 버킷에서 파일의 URL을 생성합니다.
        URL url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName).build());
        return url.toString();
    }

    // 파일 이름 충돌을 방지하기 위해 고유한 파일 이름을 생성하는 메소드
    private String generateUniqueFileName(String originalFilename) {
        // 여기에 고유한 파일 이름을 생성하는 로직을 구현합니다.
        // 예를 들어, UUID.randomUUID().toString()을 파일 이름 앞에 추가할 수 있다.
        return System.currentTimeMillis() + "_" + originalFilename;
    }
}
