package com.strawberryfarm.fitingle.domain.groups.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GroupServiceTest {
    @Autowired
    private GroupService groupService;

    @Test
    public void contextLoads() {
        assertNotNull(groupService);
        System.out.println(groupService);
    }
}
