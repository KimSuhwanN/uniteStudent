package org.example.mvc.packet;

import org.example.global.Protocol;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPacket extends Protocol {
    public LoginPacket(String data, String code) {
        // 타입은 항상 인증과 관련된 TYPE_AUTH로 설정
        this.type = Protocol.TYPE_AUTH;

        // code 값에 따라 알맞은 요청 코드를 설정
        this.code = Objects.equals(code, "id")
                ? Protocol.CODE_AUTH_ID_REQ // "id"라면 ID 요청 코드 설정
                : Protocol.CODE_AUTH_PW_REQ; // 그 외의 경우 PW 요청 코드 설정

        // 데이터 길이를 계산하여 length 필드에 저장
        this.length = (short) data.length();

        // 문자열 데이터를 바이트 배열로 변환하여 Protocol의 데이터 필드에 저장
        setData(data.getBytes());
    }
}