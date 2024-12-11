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
}