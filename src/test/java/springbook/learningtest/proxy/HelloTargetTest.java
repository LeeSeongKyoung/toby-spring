package springbook.learningtest.proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HelloTargetTest {

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
	}


}