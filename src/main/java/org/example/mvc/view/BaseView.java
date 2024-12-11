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

    // 요청 전송 메소드
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

    // 데이터 처리 메소드
    protected void getResponse() {
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
            System.out.println(responseData);

        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}