package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * View 계층의 공통 기능을 제공하는 추상 클래스.
 * 서버와의 데이터 송수신 기능을 캡슐화하여 코드 중복을 줄이고
 * 하위 클래스에서 간단히 쓸 수 있도록 설계됨.
 */

public abstract class BaseView {
    protected final DataInputStream in; // 서버로부터 데이터를 읽기 위한 입력 스트림
    protected final DataOutputStream out; // 서버에 데이터를 전송하기 위한 출력 스트림

    public BaseView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    // 요청 전송 메소드
    protected void sendRequest(byte type, byte code, String data) {
        try {
            // Protocol 객체 생성 및 요청 데이터 설정
            Protocol packet = new Protocol();
            packet.setType(type); // 요청 타입 설정
            packet.setCode(code); // 요청 코드 설정
            packet.setData(data); // 요청 데이터 설정

            // 패킷을 바이트 배열로 변환
            byte[] packetData = packet.getPacket();

            // 바이트 배열을 하나씩 전송
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush(); // 스트림 버퍼 비우기
        } catch (IOException e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
        }
    }

    // 데이터 처리 메소드
    protected void getResponse() {
        try {
            // 응답 패킷의 기본 필드 읽기
            byte type = in.readByte(); // 응답 타입
            byte code = in.readByte(); // 응답 코드
            short length = in.readShort(); // 데이터 길이

            // 디버그용 출력 (응답 필드 확인)
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            // 응답 데이터를 저장할 문자열 초기화
            String responseData = "";

            // 데이터 길이가 0보다 크면 데이터 읽기
            if (length > 0) {
                byte[] data = new byte[length]; // 데이터 저장용 바이트 배열
                in.readFully(data); // 데이터를 배열에 저장
                responseData = new String(data, StandardCharsets.UTF_8); // UTF-8로 변환
            }
            // 응답 데이터 출력
            System.out.println(responseData);

        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}