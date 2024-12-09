package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseView {
    protected final DataInputStream in;
    protected final DataOutputStream out;

    public BaseView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    // 요청 전송 메서드
    protected void sendRequest(byte type, byte code, String data) {
        try {
            Protocol packet = new Protocol();
            packet.setType(type);
            packet.setCode(code);
            packet.setData(data);
            byte[] packetData = packet.getPacket();

            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
        }
    }

    // 응답 처리 메서드
    protected void receiveResponse() {
        try {
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            String responseData = "";
            if (length > 0) {
                byte[] data = new byte[length];
                in.readFully(data);
                responseData = new String(data, StandardCharsets.UTF_8);
            }

            // 응답 코드에 따른 처리
            switch (code) {
                case Protocol.CODE_SUCCESS -> System.out.println("요청 성공: " + responseData);
                case Protocol.CODE_FAIL -> System.out.println("요청 실패: " + responseData);
                case Protocol.CODE_NO_AUTH -> System.out.println("권한 없음: " + responseData);
                case Protocol.CODE_INVALID_REQ -> System.out.println("잘못된 요청: " + responseData);
                default -> System.out.println("알 수 없는 응답 코드: " + code);
            }

            System.out.println("전체 응답 데이터: " + responseData);
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}