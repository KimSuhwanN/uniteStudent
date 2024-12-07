package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BaseView {
    protected final DataInputStream in;
    protected final DataOutputStream out;

    public BaseView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    // 요청 전송 메서드
    protected void sendRequest(byte code, String data) {
        try {
            Protocol packet = new Protocol();
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

            byte[] data = null;
            if (length > 0) {
                data = new byte[length];
                in.readFully(data);
            }

            Protocol response = new Protocol(type, code);
            if (data != null) response.setData(data);
            response.getDataAsString();
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
        }
    }
}