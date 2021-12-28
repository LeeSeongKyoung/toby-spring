package springbook.learningtest.spring.factorybean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration // 설정파일 이름을 지정하지 않으면 클래스이름 + "-context.xml"이 디폴트로 사용
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void getMessageFromFactoryBean(){
		Object message = context.getBean("message");
		assertThat(message).isEqualTo(Message.class); // 타입 확인
		assertThat(((Message) message).getText()).isEqualTo("Factory Bean"); // 설정과 기능 확인
	}

}
