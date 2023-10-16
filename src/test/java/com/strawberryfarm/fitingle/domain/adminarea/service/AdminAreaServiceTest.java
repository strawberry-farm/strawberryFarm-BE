package com.strawberryfarm.fitingle.domain.adminarea.service;

import com.strawberryfarm.fitingle.domain.adminarea.repository.AdminAreaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;


public class AdminAreaServiceTest {

    @InjectMocks
    private AdminAreaService adminAreaService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AdminAreaRepository adminAreaRepository;


    @Test
    public void testInsertRegionCodes() {

    }

    @Test
    public void getAllAdminAreas() {
    }

    @Test
    public void updateRegionCodes() {
    }
}