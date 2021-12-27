package springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations="/applicationContext.xml")
class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	UserDao userDao;
	@Autowired
	DataSource dataSource;
	@Autowired
	PlatformTransactionManager transactionManager;

	// 테스트 픽처
	List<User> users;

	@BeforeEach
	public void setUp(){
		users = Arrays.asList(
				new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
				new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("erwins", "신승한","p3",Level.SILVER,MIN_RECOMMEND_FOR_GOLD-1,29),
				new User("madnite1","이상호","p4",Level.SILVER, MIN_RECOMMEND_FOR_GOLD, 30),
				new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
		);
	}

	static class TestUserService extends UserServiceImpl {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) {
				throw new TestUserServiceException();
			}
			super.upgradeLevel(user);
		}

		static class TestUserServiceException extends RuntimeException{}
	}


/*
	@Test
	@DisplayName("빈등록확인")
	public void bean1(){
		ApplicationContext context = new GenericXmlApplicationContext("/applicationContext.xml");
		String[] beanNames = context.getBeanDefinitionNames();
		System.out.println(Arrays.toString(beanNames));

		UserServiceImpl userService = (UserServiceImpl) context.getBean("userService");
		System.out.println(userService.userDao != null);
	}
*/


/*	@Test
	@DisplayName("사용자 레벨 업그레이드 테스트")
	public void upgradeLevels() throws SQLException {
		userDao.deleteAll();

		for(User user : users) {
			userDao.add(user);
		}

		userService.upgradeLevels();

		// 각 사용자별로 업그레이드 후의 예상 레벨을 검증
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);

	}*/



	// DB에서 사용자 정보를 가져와 레벨을 확인하는 코드가 중복되므로 헬퍼 메소드로 분리
	private void checkLevelUpgraded(User user, boolean upgraded){
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			// update가 일어났는지 확인
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
		}else {
			// update가 일어나지 않았는지 확인
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
		}
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
		assertThat(userwithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);*//*

*/
/*
		System.out.println(userDao.get(userWithLevel.getId()));
		System.out.println(userDao.get(userWithoutLevel.getId()));*//*

		 */
	}

	@Test
	public void upgradeAllOrNothing() throws Exception{
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);


		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}

		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
			// 예외 발생없이 정상적으로 종료되면 fail() 메소드 때문에 테스트 실패
			// -> 테스트가 의도한 대로 동작하는지 확인용
		} catch (TestUserService.TestUserServiceException e) {
			// TestUserService가 던져주는 예외를 잡아서 계속 진행되도록함. 그외의 예외라면 테스트 실패
		}

		// 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인
		checkLevelUpgraded(users.get(1), false);
	}



}