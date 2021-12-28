package springbook.learningtest.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UppercaseHandler implements InvocationHandler {
	// 어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 Object 타입으로 수정
	Object target;
	private UppercaseHandler(Object target) {
		this.target = target;
	}

	// 호출한 메소드의 리턴 타입이 String인 경우만 대문자 변경 기능을 적용하도록 수정
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args);
		if (ret instanceof String && method.getName().startsWith("say")) {
			return ((String)ret).toUpperCase();
		}else {
			return ret;
		}
	}
}
