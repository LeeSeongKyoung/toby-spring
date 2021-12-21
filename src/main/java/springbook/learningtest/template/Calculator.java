package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Calculator {

	// 덧셈
	public Integer calcSum(String filepath) throws IOException {
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value + Integer.valueOf(line);
			}};
		return lineReadTemplate(filepath, sumCallback, 0);
	}

	// 곱셈
	public Integer calcMultiply(String filePath) throws IOException {
		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line);
			}
		};
		return lineReadTemplate(filePath, multiplyCallback, 1);
	}

	// 문자열 연결 기능 콜백을 이용해 만든 concatenate() 메소드
	public String concatenate(String filepath) throws IOException{
		LineCallback<String> concatenateCallback = new LineCallback<String>() {
			@Override
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}};
		return lineReadTemplate(filepath, concatenateCallback, "");
	}

	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
		BufferedReader br =null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			int ret = callback.doSomethingWithReader(br);
			return ret;
		}catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if (br != null) {
				try {
					br.close();
				}catch (IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
	}

	public <T>  T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		}catch (IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if (br != null) {
				try {
					br.close();
				}catch (IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
	}


}
