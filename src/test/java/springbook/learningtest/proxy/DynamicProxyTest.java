package springbook.learningtest.proxy;

import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import springbook.learningtest.proxy.Hello;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DynamicProxyTest {


	@Test
	public void simpleProxy(){
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				// 동적으로 생성되는 다이내믹 프록시 클래스 로딩에 사용할 클래스로더
				getClass().getClassLoader(),
				// 구현할 인터페이스
				new Class[] { Hello.class },
				// 부가기능과 위임 코드를 담은 InvocationHandler
				new UppercaseHandler(new HelloTarget()));
		Hello hello = (Hello) new HelloTarget();
		assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
		assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
		assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
	}

	@Test
	public void proxyFactoryBean(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());

		Hello proxiedHello = (Hello) pfBean.getObject();

		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
	}

	static class UppercaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}

	static interface Hello{
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}

	static class HelloTarget implements Hello{
		@Override
		public String sayHello(String name) {
			return "Hello " + name;
		}
		@Override
		public String sayHi(String name) {
			return "Hi " + name;
		}
		@Override
		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}

	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());

		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");

		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

		Hello proxiedHello = (Hello) pfBean.getObject();

		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");

	}

	@Test
	@DisplayName("포인트컷 테스트")
	public void classNamePointCutAdvisor(){
		// 포인트컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
			public ClassFilter getClassFilter(){ // 익명 내부 클래스 방식으로 클래스 정의
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
						// 클래스 이름이 HelloT로 시작하는 것만 선정
					}
				};
			}
		};
		// sayH로 시작하는 메소드 이름을 가진 메소드만 선정
		classMethodPointcut.setMappedName("sayH*");


		// 테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true);

		class HelloWorld extends HelloTarget{};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);

		class HelloToby extends HelloTarget{};
		checkAdviced(new HelloToby(), classMethodPointcut, true);
	}

	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();

		if (adviced) {
			// 메소드 선정 방식을 통해 어드바이스 적용
			assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
			assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
			assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
		}else {
			// 어드바이스 적용 대상 후보에서 아예 탈락
			assertThat(proxiedHello.sayHello("Toby")).isEqualTo("Hello Toby");
			assertThat(proxiedHello.sayHi("Toby")).isEqualTo("Hi Toby");
			assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
		}
	}

/*	@Test
	public void HelloUpperCase(){
		// 프록시를 통해 타깃 오브젝트에 접근하도록 구성
		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
	}*/


}