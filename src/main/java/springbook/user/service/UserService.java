package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
	@Autowired
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

/*	@Bean
	public UserService userService(){
		return new UserService(userDao());
	}*/

	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			Boolean changed = null; // 레벨의 변화가 있는지를 확인하는 플래그
			if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
				user.setLevel(Level.SILVER); // BASIC 레벨 없그레이드 작업
				changed = true;
			} else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
				user.setLevel(Level.GOLD); // SILVER 레벨 업그레이드 작업
				changed = true;
			} else if (user.getLevel() == Level.GOLD) {
				changed = false; // GOLD 레벨은 변경 없음
			} else {
				changed = false; // 일치하는 조건 없으면 변경 없음
			}

			if (changed) {
				userDao.update(user); // 레벨의 변경만 있는 경우에만 update() 호출
			}
		}
	}
}
