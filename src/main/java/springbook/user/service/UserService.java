package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	@Autowired
	DataSource dataSource;

	@Autowired
	UserDao userDao;


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

/*	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}*/

	public void upgradeLevels() throws SQLException {
		// 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화
		TransactionSynchronizationManager.initSynchronization();

		// DB 커넥션을 생성하고 트랜잭션을 시작한다.
		// 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행
		// DB 커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
		Connection c = DataSourceUtils.getConnection(dataSource);
		c.setAutoCommit(false);

		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			c.commit(); // 정상적으로 작업을 마치면 트랜잭션 커밋
		} catch (Exception e) {
			c.rollback();
			throw e; // 예외가 발생하면 롤백
		}finally {
			// 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫음
			DataSourceUtils.releaseConnection(c, dataSource);
			// 동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	// 업그레이드 가능 확인 메소드
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default:throw new IllegalArgumentException("Unknown Level : " + currentLevel);
		}
	}

	// 레벨 업그레이드 작업 메소드
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
	}

	// 사용자 신규 등록 로직을 담은 add()메소드
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
			userDao.add(user);
		}
	}

}
