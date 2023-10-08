package com.strawberryfarm.fitingle.domain.adminarea.service;


import com.strawberryfarm.fitingle.domain.adminarea.dto.RegionCodesResponseDTO;
import com.strawberryfarm.fitingle.domain.adminarea.dto.RegionCodesResponseDTO.RegionCode;
import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import com.strawberryfarm.fitingle.domain.adminarea.repository.AdminAreaRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
@Log4j2
@RequiredArgsConstructor
public class AdminAreaService {
    private final RestTemplate restTemplate;
    private final AdminAreaRepository areaRepository;

    public String BASE_URL = "https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app/v1/regcodes?regcode_pattern=";

    @Transactional
    public void updateRegionCodes() {
        deleteAllRegionCodes();
        insertRegionCodes();
    }

    private void deleteAllRegionCodes() {
        areaRepository.deleteAll();
    }

    public void insertRegionCodes() {
        Map<String, AdminArea> areasToSaveMap = new HashMap<>();
        List<RegionCode> sidoCodes = regionCodes("*00000000");

        for (RegionCode sido : sidoCodes) {
            String sidoCode = sido.getCode().substring(0, 2);
            String sidoName = formatSidoName(sido.getName());
            AdminArea sidoArea = AdminArea.builder()
                    .sidoCode(sidoCode)
                    .gunguCode(sidoCode + "000")
                    .name(sidoName + " 전체")
                    .build();

            areasToSaveMap.put(sidoName + " 전체", sidoArea);
            List<RegionCode> gunguDongCodes = regionCodes(sidoCode + "*");
            for (RegionCode gunguDong : gunguDongCodes) {
                if (gunguDong.getCode().endsWith("00000000") || !gunguDong.getCode().endsWith("0000")) {
                    continue;
                }
                String gunguDongCode = gunguDong.getCode().substring(0, 5);
                String gunguDongName = formatGunguName(gunguDong.getName(), sidoName);
                AdminArea area = AdminArea.builder()
                        .sidoCode(sidoCode)
                        .gunguCode(gunguDongCode)
                        .name(gunguDongName)
                        .build();
                areasToSaveMap.put(sidoCode + "-" + gunguDongName, area);
            }
        }
        List<AdminArea> areasToSaveList = new ArrayList<>(areasToSaveMap.values());
        areaRepository.saveAll(areasToSaveList);
    }

    private String formatGunguName(String name, String sidoName) {
        String[] splitNames = name.split(" ");
        if (splitNames.length > 1) {
            return sidoName + " " + splitNames[1];
        }
        return name;
    }

    private String formatSidoName(String name) {
        if (name.endsWith("00000000")) {
            return name.substring(0, name.length() - 8) + " 전체";
        }
        name = name.replace("특별시", "시");
        name = name.replace("광역시", "시");
        name = name.replace("충청남도", "충남");
        name = name.replace("충청북도", "충북");
        name = name.replace("경상남도", "경남");
        name = name.replace("경상북도", "경북");
        name = name.replace("전라남도", "전남");
        name = name.replace("전라북도", "전북");
        name = name.replace("경기도", "경기");
        name = name.replace("제주특별자치도", "제주");
        name = name.replace("강원특별자치도", "강원");
        return name.trim();
    }

    private List<RegionCode> regionCodes(String pattern) {
        ResponseEntity<RegionCodesResponseDTO> response = restTemplate.getForEntity(BASE_URL + pattern, RegionCodesResponseDTO.class);

        // API 호출에 문제가 없다면 응답 본문(body)의 데이터를 반환
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getRegcodes();
        }
        // API 호출에 문제가 있거나 응답 본문이 없는 경우 로그 출력, 빈 리스트를 반환
        log.error("행정코드 불러오기 에러. HTTP 상태 코드: {0000}", response.getStatusCode());
        return new ArrayList<>();
    }
}

