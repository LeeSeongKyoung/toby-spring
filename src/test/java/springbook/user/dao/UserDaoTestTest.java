package springbook.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// JUnit5
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTestTest {
	@Autowired
	UserDaoJdbc dao;
	@Autowired
	DataSource dataSource;

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
		dao = new UserDaoJdbc();
		DataSource dataSource = new SingleConnectionDataSource(
				"jdbc:h2:tcp://localhost/~/test", "user", "password", true
		);
		dao.setDataSource(dataSource);
		this.user1 = new User("gyumee", "박성철1", "springno1");
		this.user2 = new User("leegw700", "박성철2", "springno2");
		this.user3 = new User("bumjin", "박성철3", "springno3");
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

	@Test
	public void getAll() throws SQLException,ClassNotFoundException{
		dao.deleteAll();

		// 예외
		List<User> users0 = dao.getAll();
		assertThat(users0.size()).isEqualTo(0);

		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size()).isEqualTo(1);
		checkSameUser(user1, users1.get(0));

		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size()).isEqualTo(2);
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));

		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size()).isEqualTo(3);
		// user3의 id 값이 알파벳순으로 가장 빠르므로 getAll()의 첫번째 엘리먼트여야함
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}

	// User 오브젝트의 내용을 비교하는 검증 코드. 테스트에서 반복적으로 사용되므로 분리해둠
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId()).isEqualTo(user2.getId());
		assertThat(user1.getName()).isEqualTo(user2.getName());
		assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
	}

	@Test
	public void duplicateKey(){
		dao.deleteAll();

		dao.add(user1);
		dao.add(user1);  // 강제로 같은 사용자 두번 등록, 여기서 예외발생해야함

//		assertThrows(DataAccessException.class, () ->{
//			dao.add(user1);
//		});
	}

	@Test
	public void sqlExceptionTranslate(){
		dao.deleteAll();

		try {
			dao.add(user1);
			dao.add(user1);
		} catch (DuplicateKeyException exception) {
			SQLException sqlEx = (SQLException) exception.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

			assertThat(set.translate(null, null, sqlEx)).isEqualTo(DuplicateKeyException.class);
		}
	}

}