package springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

	@Autowired
	UserService userService;

	@Test
	public void bean(){
		assertThat(this.userService).isNotNull();
	}

/*

	// 테스트 픽스처
	List<User> users;

	@BeforeEach
	public void setUp(){
		users = Arrays.asList( // 배열을 리스트로 만들어주는 메소드
				new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
				new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
				new User("erwins", "신승한","p3",Level.SILVER,60,29),
				new User("madnite1","이상호","p4",Level.SILVER, 60, 30),
				new User("green", "오민규", "p5", Level.GOLD, 100, 100)
		);
	}

	@Test
	public void upgradeLevels(){
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}

		userService.upgradeLevels();

		// 각 사용자별로 업그레이드 후의 예상 레벨을 검증
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}

	// DB에서 사용자 정보를 가져와 레벨을 확인하는 코드가 중복되므로 헬퍼 메소드로 분리
	private void checkLevel(User user, Level expectedLevel){
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
	}

	@Test
	public void add(){
		userDao.deleteAll();

		User userWithLevel = users.get(4); // GOLD 레벨
		User userWithoutLevel = users.get(0); // 레벨이 비어있는 사용자, 로직에 따라 등록 중에 BASIC 레벨도 설정돼야 함
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userwithoutLevelRead = userDao.get(userWithoutLevel.getId());
		// DB에 저장된 결과를 가져와 확인

		assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
		assertThat(userwithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
	}
*/

}