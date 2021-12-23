package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {

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

	// 사용자 신규 등록 로직을 담은 add()메소드
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
			userDao.add(user);
		}
	}

	// 업그레이드 가능 확인 메소드
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= 50);
			case SILVER: return (user.getRecommend() >= 30);
			case GOLD: return false;
			default:throw new IllegalArgumentException("Unknown Level : " + currentLevel);
		}
	}

	// 레벨 업그레이드 작업 메소드
	private void upgradeLevel(User user) {
		if(user.getLevel() == Level.BASIC){
			user.setLevel(Level.SILVER);
		} else if (user.getLevel() == Level.SILVER) {
			user.setLevel(Level.GOLD);
			userDao.update(user);
		}
	}


}
