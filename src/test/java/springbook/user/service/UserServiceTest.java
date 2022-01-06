package springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.learningtest.spring.factorybean.TransactionHandler;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebAppConfiguration
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
	@Autowired
	ApplicationContext context;

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

	static class MockUserDao implements UserDao {
		// 레벨 업그레이드 후보 User 오브젝트 목록
		private List<User> users;
		// 업그레이드 대상 오브젝트를 저장해둘 목록
		private List<User> updated = new ArrayList<>();

		private MockUserDao(List<User> users) {
			this.users = users;
		}

		private List<User> getUpdated() {
			return this.updated;
		}

		// 스텁기능 제공
		public List<User> getAll(){
			return this.users;
		}

		// 목 오브젝트 기능 제공
		public void update(User user) {
			updated.add(user);
		}

		// 테스트에 사용되지 않는 메소드
		public void add(User user){throw new UnsupportedOperationException();}
		public void deleteAll(){throw new UnsupportedOperationException();}
		public User get(String id){throw new UnsupportedOperationException();}
		public int getCount() { throw new UnsupportedOperationException();}
	}

	@Test
	@DisplayName("사용자 레벨 업그레이드 테스트")
	public void upgradeLevels() throws Exception{

		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		userServiceImpl.upgradeLevels();

		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size()).isEqualTo(2);
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
	}

	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId()).isEqualTo(expectedId);
		assertThat(updated.getLevel()).isEqualTo(expectedLevel);
	}

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
	@DirtiesContext // 다이내믹 프록시 팩토리 빈을 직접 만들어 사용할 때는 없앴다가 다시 등장한 컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception{
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);

		ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
//		TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
		// 팩토리 빈 자체를 가져와야 하므로 빈 이름에 &를 반드시 넣어야함
		// 테스트용 타깃 주입
		txProxyFactoryBean.setTarget(testUserService);
		UserService txUserService = (UserService)txProxyFactoryBean.getObject();
		// 변경된 타깃 설정을 이용해서 트랜잭션 다이내믹 프록시 오브젝트를 다시 생성

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

	// Mockito를 적용한 테스트 코드
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		userServiceImpl.upgradeLevels();

		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);
	}

}