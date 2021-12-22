package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;


public class UserDaoJdbc implements UserDao{
	// chap4
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;

	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};

	@Override
	public void add(final User user) throws DataAccessException{
		this.jdbcTemplate.update("insert into users(id, name, password, level , login, recommend) values(?,?,?,?,?,?)"
				, user.getId(), user.getName(), user.getPassword()
				, user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}

	@Override
	@Deprecated
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id}, this.userMapper);
	}

	@Override
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	@Override
	public int getCount(){
		return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
	}

	@Override
	public List<User> getAll(){
		return this.jdbcTemplate.query("select * from users order by id",
					this.userMapper);
	}

	// 사용자 정보 수정용
	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name =?, password = ?, level = ? , login = ? , " +
						"recommend = ? where id = ? ", user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId());

	}
}
