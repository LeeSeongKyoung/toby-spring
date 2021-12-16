package springbook.learningtest.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.*;

// chap3
// JUnit5
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class JUnitTest {

	@Autowired
	ApplicationContext context;

	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	static ApplicationContext contextObject = null;

	@Test
	public void test1(){
		assertThat(testObjects).isNotSameAs(hasItem(this));
		testObjects.add(this);
		assertThat(contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}

	@Test
	public void test2(){
		assertThat(testObjects).isNotSameAs(hasItem(this));
		testObjects.add(this);
		assertThat(contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}

	@Test
	public void test3(){
		assertThat(testObjects).isNotSameAs(hasItem(this));
		testObjects.add(this);
		assertThat(contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}
}
