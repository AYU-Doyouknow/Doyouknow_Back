package org.ayu.doyouknowback.domain.fcm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Column(name = "token")
    private String value;

    // 직접 생성 방지
    private Token(String value){
        validateToken(value);
        this.value = value;
    }

    // 정적 팩토리 메서드
    public static Token of(String value) {
        return new Token(value);
    }

    // 토큰 유효성 검증
    private void validateToken(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("토큰은 비어있을 수 없습니다.");
        }
        if (!isValidFormat(value)) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다: " + value);
        }
    }

    // 토큰 형식 검증
    private boolean isValidFormat(String value) {
        return value.startsWith("ExponentPushToken[") || value.startsWith("ExpoPushToken[");
    }

    // 토큰 유효성 확인 (런타임에서 확인용)
    public boolean isValid() {
        return value != null && !value.isEmpty() && isValidFormat(value);
    }

    // 토큰 매칭 확인
    public boolean matches(String targetToken) {
        return this.value.equals(targetToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token Token = (Token) o;
        return Objects.equals(value, Token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
