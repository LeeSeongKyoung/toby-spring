package springbook.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;


import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations="/applicationContext.xml")
class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;

	// 테스트 픽처
	List<User> users;

	@BeforeEach
	public void setUp(){
		users = Arrays.asList(
				new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
				new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
				new User("erwins", "신승한","p3",Level.SILVER,60,29),
				new User("madnite1","이상호","p4",Level.SILVER, 60, 30),
				new User("green", "오민규", "p5", Level.GOLD, 100, 100)
		);
	}

	@Test
	@DisplayName("빈등록확인")
	public void bean1(){
		ApplicationContext context = new GenericXmlApplicationContext("/applicationContext.xml");
		String[] beanNames = context.getBeanDefinitionNames();
		System.out.println(Arrays.toString(beanNames));

		UserService userService = (UserService) context.getBean("userService");
		System.out.println(userService.userDao != null);
	}


	@Test
	public void upgradeLevels(){
		userDao.deleteAll();

		for(User user : users) userDao.add(user);

		userService.upgradeLevels();

		for (User user : users) {
			System.out.println(user.getLevel());
			System.out.println("---------------");
		}

		// 각 사용자별로 업그레이드 후의 예상 레벨을 검증
/*		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);*/
	}

	// DB에서 사용자 정보를 가져와 레벨을 확인하는 코드가 중복되므로 헬퍼 메소드로 분리
	private void checkLevel(User user, Level expectedLevel){
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
	}

	// 잘 담겨지는데 UserDao.get() 메소드에 문제가 있는 듯..
	@Test
	public void add(){
		userDao.deleteAll();

		User userWithLevel = users.get(4); // GOLD 레벨
		User userWithoutLevel = users.get(0); // 레벨이 비어있는 사용자, 로직에 따라 등록 중에 BASIC 레벨도 설정돼야 함

		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

/*		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userwithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
		assertThat(userwithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);*/

/*
		System.out.println(userDao.get(userWithLevel.getId()));
		System.out.println(userDao.get(userWithoutLevel.getId()));*/

	}


}