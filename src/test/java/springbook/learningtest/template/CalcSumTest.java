package springbook.learningtest.template;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {

	@Test
	public void sumOfNumbers() throws IOException, NullPointerException {
		Calculator calculator = new Calculator();
		int sum = calculator.calcSum(getClass().getResource("numbers.txt").getPath());
		assertThat(sum).isEqualTo(10);
	}
}
