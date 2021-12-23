package springbook.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.rmi.UnexpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
	User user;

	@BeforeEach
	public void setUp(){
		user = new User();
	}

	@Test
	@DisplayName("유저 레벨 업그레이드 테스트")
	public void upgradeLevel(){
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.nextLevel() == null) {
				continue;
			}
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel()).isEqualTo(level.nextLevel());
		}
	}

	@Test
	@DisplayName("예외테스트-다음 레벨이 없는 레벨을 업그레이드 하는 경우")
	public void cannotUpgradeLevel(){
		Level[] levels = Level.values();

		Assertions.assertThrows(IllegalStateException.class, () -> {
			for (Level level : levels) {
				if(level.nextLevel() != null) continue;

				user.setLevel(level);
				user.upgradeLevel();
			}
		});
	}


}
