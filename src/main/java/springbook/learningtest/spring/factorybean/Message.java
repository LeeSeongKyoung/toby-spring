package springbook.learningtest.spring.factorybean;

public class Message {
	String text;

	// 생성가자 private으로 선언되어있어서 생성자를 통해 오브젝트를 만들 수 없다
	private Message(String text) {
		this.text = text;
	}

	public String getText(){
		return text;
	}

	// 생성자 대신 사용할 수 있는 스태틱 팩토리 메소드를 제공
	public static Message newMessage(String text) {
		return new Message(text);
	}


}
