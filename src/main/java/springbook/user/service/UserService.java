package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	@Autowired
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
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
