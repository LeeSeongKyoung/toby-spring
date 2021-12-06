package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {

	@Bean
	public UserDao userDao(){
		return new UserDao(connectionMaker());
	}

	@Bean // 오브젝트 생성을 담당하는 IoC 메소드라는 표시
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker();
	}
}
