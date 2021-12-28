package springbook.learningtest.spring.factorybean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations="/factoryBeanTest-context.xml")
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void getMessageFromFactoryBean(){
		Object message = context.getBean("message");
		assertThat(message).isInstanceOf(Message.class);// 타입 확인
		assertThat(((Message)message).getText()).isEqualTo("Factory Bean"); // 설정과 기능 확인
	}
	// FactoryBean 인터페이스를 구현한 클래스를 스프링 빈으로 만들어두면 getObject()라는 메소드가 생성해주는 오브젝트가
	// 실제 빈의 오브젝트로 대치된다는 사실을 알 수 있다

	@Test
	public void getFactoryBean() throws Exception {
		Object factory = context.getBean("&message");
		// &가 붙고 안붙고에 따라 getBean()메소드가 돌려주는 오브젝트가 달라짐
		assertThat(factory).isInstanceOf(MessageFactoryBean.class); // 타입 확인
	}
	// 팩토리 빈이 만들어주는 빈 오브젝트가 아니라 팩토리 빈 자체를 가져오고 싶을경우
	// &를 빈 이름앞에 붙여주면 팩토리 빈 자체를 돌려줌
}
