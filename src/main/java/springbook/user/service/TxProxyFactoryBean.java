package springbook.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.learningtest.spring.factorybean.TransactionHandler;

import java.lang.management.PlatformLoggingMXBean;
import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {
	// TransactionHandler를 생성할 때 필요
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	// 다이내믹 프록시를 생성할 때 필요
	// UserService 외의 인터페이스를 가진 타깃에도 적용가능
	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	// FactoryBean 인터페이스 구현 메소드
	@Override
	public Object getObject() throws Exception { // DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시를 생성
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler);
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
		// 팩토리 빈이 생성하는 오브젝트의 타입은 DI 받은 인터페이스 타입에 따라 달라짐
		// 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용할 수 있다.
	}

	@Override
	public boolean isSingleton() {
		// 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 의미
		return false;
	}
}
