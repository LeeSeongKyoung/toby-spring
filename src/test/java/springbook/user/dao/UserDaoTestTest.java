package springbook.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// JUnit5
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTestTest {

	UserDao dao;

	// setUp() 메소드에서 만드는 오브젝트를 테스트 메소드에서 사용할 수 있도록 인스턴스 변수로 선언
	// 픽스처
	private User user1;
	private User user2;
	private User user3;

//	@Autowired
//	private ApplicationContext context;

	@BeforeEach
	public void setUp(){
//		this.dao = context.getBean("userDao", UserDao.class);
		dao = new UserDao();
		DataSource dataSource = new SingleConnectionDataSource(
				"jdbc:h2:tcp://localhost/~/test", "user", "password", true
		);
		dao.setDataSource(dataSource);
		this.user1 = new User("gyumee1", "박성철1", "springno1");
		this.user2 = new User("gyumee2", "박성철2", "springno2");
		this.user3 = new User("gyumee3", "박성철3", "springno3");
	}


	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException{
		User user1 = new User("hahaha1", "하하하1", "springno1");
		User user2 = new User("hahaha2" , "하하하2", "springno2");

		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);

		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);

		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName()).isEqualTo(user1.getName());
		assertThat(userget1.getPassword()).isEqualTo(user1.getPassword());

		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName()).isEqualTo(user2.getName());
		assertThat(userget2.getPassword()).isEqualTo(user2.getPassword());
	}


	@Test
	public void count() throws SQLException, ClassNotFoundException{

		User user1 = new User("hahaha1" , "하하하1", "springno1");
		User user2 = new User("hahaha2" , "하하하2", "springno2");
		User user3 = new User("hahaha3" , "하하하3", "springno3");

		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);

		dao.add(user1);
		assertThat(dao.getCount()).isEqualTo(1);

		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);

		dao.add(user3);
		assertThat(dao.getCount()).isEqualTo(3);
	}

	@Test
	public void getUserFailure() throws SQLException, ClassNotFoundException{

		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);

		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unkown_id"));
		// 이 메소드 실행 중에 예외가 발생해야 함. 예외가 발생하지 않으면 테스트 실패
		// 모든 User 데이터를 지우고 존재하지 않는 id로 get()메소드를 실행하는 test
	}

}