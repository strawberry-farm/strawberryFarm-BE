package com.strawberryfarm.fitingle.domain.adminarea.service;


import com.strawberryfarm.fitingle.domain.adminarea.dto.AdminAreaResponseDTO;
import com.strawberryfarm.fitingle.domain.adminarea.dto.RegionCodesResponseDTO;
import com.strawberryfarm.fitingle.domain.adminarea.dto.RegionCodesResponseDTO.RegionCode;
import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import com.strawberryfarm.fitingle.domain.adminarea.repository.AdminAreaRepository;

import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.*;
import java.util.stream.Collectors;
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

    public final String BASE_URL = "https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app/v1/regcodes?regcode_pattern=";


    // '시도' 이름을 간략화하기 위한 상수 매핑
    private static final Map<String, String> SIDO_NAME_MAP = Map.ofEntries(
            Map.entry("특별시", ""),
            Map.entry("광역시", ""),
            Map.entry("충청남도", "충남"),
            Map.entry("충청북도", "충북"),
            Map.entry("경상남도", "경남"),
            Map.entry("경상북도", "경북"),
            Map.entry("전라남도", "전남"),
            Map.entry("전라북도", "전북"),
            Map.entry("경기도", "경기"),
            Map.entry("제주특별자치도", "제주"),
            Map.entry("강원특별자치도", "강원")
    );

    ///-------------관리 지역 API 데이터 넣기 메소드-----------------
    /**
     * 행정구역 코드를 DB에 저장한다.
     * '시도'와 '구/군/동' 단위로 분리하여 저장하며, 세종특별자치시는 수동으로 추가한다.
     * 예시: 서울특별시 -> 서울 전체, 서울특별시 종로구 -> 종로구
     */

    @Transactional
    public void insertRegionCodes() {
        Map<String, AdminArea> areasToSaveMap = new HashMap<>();

        for (RegionCode sido : getSidoCodes()) {
            AdminArea sidoArea = buildSidoArea(sido);
            String sidoKey = sidoArea.getSidoCode();
            areasToSaveMap.put(sidoKey, sidoArea);


            for (RegionCode gunguDong : getGunguDongCodes(sido)) {
                AdminArea gunguArea = buildGunguArea(gunguDong, sido);
                String gunguKey = gunguArea.getSidoCode() + gunguArea.getGunguCode();
                areasToSaveMap.put(gunguKey, gunguArea);

            }
        }
        addSejongAreasToMap(areasToSaveMap);
        areaRepository.saveAll(new ArrayList<>(areasToSaveMap.values()));
    }

    //세종시 임의로 추가
    private void addSejongAreasToMap(Map<String, AdminArea> areasToSaveMap) {
        String sejongCode = "32";

        // "세종 전체" 항목 추가
        AdminArea sejongGeneralArea = AdminArea.builder()
                .sidoCode(sejongCode)
                .gunguCode(sejongCode + "000")
                .name("세종 전체")
                .build();
        areasToSaveMap.put(sejongCode + "000", sejongGeneralArea);

        // "세종특별자치시" 항목 추가
        AdminArea sejongCityArea = AdminArea.builder()
                .sidoCode(sejongCode)
                .gunguCode(sejongCode + "001") // 이 코드는 적절한 구/동 코드로 수정이 필요할 수 있다.
                .name("세종특별자치시")
                .build();
        areasToSaveMap.put(sejongCode + "001", sejongCityArea);
    }

    /**
     * '시도' 행정구역 객체를 구축한다.
     * 예시: 서울특별시 -> 서울 전체
     */
    private AdminArea buildSidoArea(RegionCode sido) {
        String sidoCode = extractSidoCode(sido);
        String sidoName = formatSidoName(sido.getName());
        return AdminArea.builder()
                .sidoCode(sidoCode)
                .gunguCode(sidoCode + "000")
                .name(sidoName)
                .build();
    }

    /**
     * '구/군/동' 행정구역 객체를 구축한다.
     * 예시: 서울특별시 종로구 -> 종로구
     */
    private AdminArea buildGunguArea(RegionCode gunguDong, RegionCode sido) {
        String gunguCode = extractGunguCode(gunguDong);
        String gunguName = formatGunguName(gunguDong.getName());
        return AdminArea.builder()
                .sidoCode(extractSidoCode(sido))
                .gunguCode(gunguCode)
                .name(gunguName)
                .build();
    }

    /**
     * '시도' 코드를 조회한다.
     * 예시: 서울특별시, 부산광역시, 대구광역시 등
     */
    private List<RegionCode> getSidoCodes() {
        return regionCodes("*00000000");
    }

    /**
     * 특정 '시도'에 해당하는 '구/군/동' 행정구역 코드들을 조회한다.
     * 예시: 서울특별시(11)에 해당하는 구/군/동 행정구역 코드 조회 -> 종로구(11010), 중구(11020), ... 등
     */
    private List<RegionCode> getGunguDongCodes(RegionCode sido) {
        List<RegionCode> originalCodes = regionCodes(extractSidoCode(sido) + "*00000");
        List<RegionCode> filteredCodes = new ArrayList<>();

        //시도 코드 제외
        for (RegionCode code : originalCodes) {
            if (!code.getCode().equals(sido.getCode())) {
                filteredCodes.add(code);
            }
        }
        return filteredCodes;
    }


    /**
     * '시도' 코드에서 '구/군/동' 코드를 추출한다.
     * 예시: code가 "1100000000"인 경우 -> "11" 반환
     */
    private String extractSidoCode(RegionCode code) {
        return code.getCode().substring(0, 2);
    }


    /**
     * 전체 행정구역 코드에서 '구/군/동' 코드를 추출한다.
     * 예시: code가 "1101056000"인 경우 -> "11010" 반환
     */
    private String extractGunguCode(RegionCode code) {
        return code.getCode().substring(0, 5);
    }

    /**
     * '구/군/동' 이름을 형식화한다. 이름이 두 단어 이상일 경우, 첫 단어를 제외한 나머지를 반환한다.
     * 예시: name이 "서울특별시 종로구"인 경우 -> "종로구" 반환
     */
    private String formatGunguName(String name) {
        String[] splitNames = name.split(" ");
        if (splitNames.length > 1) {
            return Arrays.stream(splitNames, 1, splitNames.length)
                    .collect(Collectors.joining(" "));
        }
        return name;
    }

    /**
     * '시도' 이름을 형식화한다. 이름을 간략화하고, "전체"를 붙여서 반환한다.
     * 예시: 서울특별시 -> 서울 전체
     * 예시: name이 "서울특별시"인 경우 -> "서울 전체" 반환
     */
    private String formatSidoName(String name) {
        for (Map.Entry<String, String> entry : SIDO_NAME_MAP.entrySet()) {
            if (name.contains(entry.getKey())) {
                String newName = (name.replace(entry.getKey(), entry.getValue()) + " 전체").trim();
                return newName;
            }
        }
        return name.trim();
    }


    /**
     * 주어진 패턴에 맞는 행정구역 코드들을 조회한다.
     * 예시: pattern이 "*00000000"인 경우 -> '시도' 행정구역 코드 리스트 반환
     */
    private List<RegionCode> regionCodes(String pattern) {
        ResponseEntity<RegionCodesResponseDTO> response = restTemplate.getForEntity(BASE_URL + pattern,
                RegionCodesResponseDTO.class);

        // API 호출에 문제가 없다면 응답 본문(body)의 데이터를 반환
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getRegcodes();
        }
        // API 호출에 문제가 있거나 응답 본문이 없는 경우 로그 출력, 빈 리스트를 반환 -> 예외 던지기 필요.
        log.error("행정코드 불러오기 에러. HTTP 상태 코드: {0000}", response.getStatusCode());
        return new ArrayList<>();
    }


    ///-------------저장된 관리 지역 데이터 가져오기 메소드-----------------
    /**
     * 모든 행정구역 정보를 가져와 '시도' 별로 그룹화하여 반환한다. 예: {31: [울산 전체, 중구, 남구, 동구, 북구, 울주군]}
     */
    public ResultDto<AdminAreaResponseDTO> getAllAdminAreas() {
        try {
            List<AdminArea> adminAreas = areaRepository.findAllByOrderBySidoCodeAscNameAsc();
            Map<String, List<AdminArea>> groupedBySido = groupBySido(adminAreas);

            List<AdminAreaResponseDTO.Sido> result = new ArrayList<>();
            for (Map.Entry<String, List<AdminArea>> entry : groupedBySido.entrySet()) {
                result.add(convertToAdminAreaDTO(entry));
            }

            AdminAreaResponseDTO response = AdminAreaResponseDTO.builder().sido(result).build();

            if (result.isEmpty()) {
                return ResultDto.<AdminAreaResponseDTO>builder()
                        .message("관리 지역 정보를 찾을 수 없습니다.")
                        .data(null)
                        .errorCode("2000")
                        .build();
            }

            return ResultDto.<AdminAreaResponseDTO>builder()
                    .message("관리 지역 정보를 성공적으로 가져왔습니다.")
                    .data(response)
                    .errorCode("1111")
                    .build();

        } catch(Exception e) {
            return ResultDto.<AdminAreaResponseDTO>builder()
                    .message("관리 지역 정보를 가져오는 중 오류가 발생하였습니다.")
                    .data(null)
                    .errorCode("2001")
                    .build();
        }
    }


    /**
     * 행정구역 리스트를 '시도' 코드별로 그룹화한다.
     * 예: [울산 중구, 울산 남구, 울산 동구, ...] -> {31: [중구, 남구, 동구, ...]}
     */
    private Map<String, List<AdminArea>> groupBySido(List<AdminArea> adminAreas) {
        Map<String, List<AdminArea>> map = new LinkedHashMap<>();
        for (AdminArea area : adminAreas) {
            String key = area.getSidoCode();
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(area);
        }
        return map;
    }

    /**
     * '시도'별로 그룹화된 행정구역 정보를 DTO로 변환한다.
     * 예: {31: [중구, 남구, 동구, ...]} -> AdminAreaResponseDTO(울산, [울산 전체, 중구, 남구, 동구, ...])
     * sigungus 항목이 1개만 있고, 전체 항목도 존재하면 전체 항목 제거 ->  세종의 경우 '세종 전체'는 삭제한다.
     */
    private AdminAreaResponseDTO.Sido convertToAdminAreaDTO(Map.Entry<String, List<AdminArea>> entry) {
        List<AdminArea> areas = entry.getValue();

        String sidoName = "";
        List<AdminAreaResponseDTO.Sigungu> sigungus = new ArrayList<>();
        AdminAreaResponseDTO.Sigungu entireArea = null;
        for (AdminArea area : areas) {
            if (area.getName().endsWith(" 전체")) {
                sidoName = area.getName().replace(" 전체", "");
                entireArea = new AdminAreaResponseDTO.Sigungu(sidoName + "전체", area.getGunguCode());
            } else {
                String sigunguName = area.getName();
                sigungus.add(new AdminAreaResponseDTO.Sigungu(sigunguName, area.getGunguCode()));
            }
        }
        // 만약 sigungus 항목이 1개만 있고, 전체 항목도 존재하면 전체 항목 제거
        if (sigungus.size() == 1 && entireArea != null) {
            entireArea = null;
        } else if (entireArea != null) {
            sigungus.add(0, entireArea);
        }
        return new AdminAreaResponseDTO.Sido(sidoName, sigungus);
    }


    @Transactional
    public void updateRegionCodes() {
        deleteAllRegionCodes();
        insertRegionCodes();
    }

    private void deleteAllRegionCodes() {
        areaRepository.deleteAll();
    }
}

