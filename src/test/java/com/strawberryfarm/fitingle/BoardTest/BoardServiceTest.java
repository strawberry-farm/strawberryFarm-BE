package com.strawberryfarm.fitingle.BoardTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.board.dto.BoardRegisterResponseDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import com.strawberryfarm.fitingle.domain.board.service.BoardService;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersAllUsersResponse;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import com.strawberryfarm.fitingle.dto.ResultDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Board Service Test")
public class BoardServiceTest {
	@Autowired
	private BoardService boardService;

	@Autowired
	private UsersService usersService;

	@BeforeAll
	public void SetUp() {
		UsersSignUpRequestDto testUsers1 = UsersSignUpRequestDto.builder()
			.email("testUsers1@test.com")
			.nickName("testUsers1")
			.password("123123")
			.build();


		usersService.signUp(testUsers1);
	}

	@Test
	@DisplayName("Board register service test")
	public void usersSignUpServiceTest() {
		//given
		UsersAllUsersResponse usersList = usersService.getUsersList();
		Long id = usersList.getUsers().get(0).getId();
		BoardRegisterRequestDTO boardRegisterRequestDTO = BoardRegisterRequestDTO
			.builder()
			.userId(id.intValue())
			.title("TITLE")
			.fieldId(1L)
			.headcount(10L)
			.location("LOCATION")
			.latitude("123123")
			.longitude("456456")
			.days(Days.ANYDAY.toString())
			.times(Times.AFTERNOON.toString())
			.details("DETAILS")
			.question("QUESTION")
			.city("CITY")
			.district("DISTRICT")
			.b_code("1111111")
			.build();

		//when
//		ResultDto<?> resultDto = usersService.signUp(usersSignUpRequestDto);
		ResultDto<BoardRegisterResponseDTO> resultDto = boardService.boardRegister(
			boardRegisterRequestDTO, null);

		//then
		assertThat(((BoardRegisterResponseDTO)resultDto.getData()).getTitle()).isEqualTo(boardRegisterRequestDTO.getTitle());
//		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getNickName()).isEqualTo(usersSignUpRequestDto.getNickName());
//		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getCreatedDate()).isNotNull();
//		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getUpdateDate()).isNotNull();
	}
}
