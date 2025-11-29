package org.ayu.doyouknowback.domain.notice.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String msg) {
        super(msg);
        // 부모 클래스 RuntimeException 에 매개변수(msg)를 가지는 생성자 를 호출할 때 super(msg) 를 한다.
    }

}
