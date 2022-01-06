package springbook.learningtest.proxy;

import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DynamicProxyTest {

/*

	@Test
	public void simpleProxy(){
		// 타깃은 인터페이스를 통해 접근하는 습관을 들이자
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
		assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
		assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
	}

	@Test
	public void HelloUpperCase(){
		// 프록시를 통해 타깃 오브젝트에 접근하도록 구성
		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
*/


	@Test
	public void simpleProxy(){

		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] {Hello.class},
				new UppercaseHandler(new HelloTarget()));

		Hello hello = (Hello) new HelloTarget();
//		Hello hello = (Hello) new HelloTarget();
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

		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("Hello Toby");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("Hi Toby");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
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

	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());

		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");

		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

		Hello proxiedHello = (Hello) pfBean.getObject();

		assertThat(proxiedHello.sayHello("Toby")).isEqualTo("Hello Toby");
		assertThat(proxiedHello.sayHi("Toby")).isEqualTo("Hi Toby");
		assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");

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