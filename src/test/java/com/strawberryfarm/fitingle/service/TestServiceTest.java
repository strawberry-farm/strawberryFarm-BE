package com.strawberryfarm.fitingle.service;

import static org.junit.jupiter.api.Assertions.*;

import com.strawberryfarm.fitingle.domain.Board;
import com.strawberryfarm.fitingle.domain.Days;
import com.strawberryfarm.fitingle.domain.PostStatus;
import com.strawberryfarm.fitingle.domain.Times;
import com.strawberryfarm.fitingle.domain.Users;
import com.strawberryfarm.fitingle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class TestServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService dummyDataService;

    @Test
    public void insertDummyDataTest() {
        dummyDataService.insertDummyUsers();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testUserAndBoardRelationship() {
        // Given
        Users user = Users.builder()
                .email("test@example.com2")
                .password("testPassword")
                .nickname("testNick")
                .interestArea("testInterestArea")
                .profileImageUrl("https://example.com/test.jpg")
                .loginToken("testToken")
                .pushToken("testPushToken")
                .aboutMe("About test user")
                .loginType("testType")
                .build();

        Board board = Board.builder()
                .user(user)
                .postStatus(PostStatus.Y)
                .titleContents("Test Content")
                .headCount(1L)
                .city("Test City")
                .district("Test District")
                .BCode("Test BCode")
                .location("Test Location")
                .latitude("Test Latitude")
                .longitude("Test Longitude")
                .question("Test Question")
                .days(Days.Y)
                .times(Times.Y)
                .views(1L)
                .build();
        user.addBoard(board);
        // When
        userRepository.save(user);
        // Then
        Users foundUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals(1, foundUser.getBoards().size());
        assertEquals(board.getTitleContents(), foundUser.getBoards().get(0).getTitleContents());
    }
}