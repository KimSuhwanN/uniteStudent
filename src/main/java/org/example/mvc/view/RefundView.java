package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.RefundPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class RefundView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public RefundView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 환불 확인 ===");
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.CODE_WITHDRAWAL_STATUS, studentId);
        receiveResponse();
    }

    private void sendRequest(byte code, String data) {
        try {
            RefundPacket packet = new RefundPacket(code, data);
            out.write(packet.getPacket());
            out.flush();
        } catch (Exception e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
        }
    }

    private void receiveResponse() {
        try {
            Protocol response = readProtocolResponse();
            if (response.getCode() == Protocol.CODE_SUCCESS) {
                System.out.println("환불 상태: " + response.getDataAsString());
            } else {
                System.out.println("환불 확인 실패: " + response.getDataAsString());
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
        }
    }

    private Protocol readProtocolResponse() throws Exception {
        byte type = in.readByte();
        byte code = in.readByte();
        short length = in.readShort();
        byte[] data = new byte[length];
        in.readFully(data);
        Protocol response = new Protocol(type, code);
        response.setData(data);
        return response;
    }
}