package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.RoomPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class RoomView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public RoomView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 합격 여부 및 호실 확인 ===");
        System.out.println("1. 합격 여부 조회");
        System.out.println("2. 생활관 호실 조회");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            switch (sc.nextInt()) {
                case 1 -> checkPassStatus();
                case 2 -> checkRoomInfo();
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void checkPassStatus() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.CODE_ROOM_PASS_CHECK, studentId);
        receiveResponse();
    }

    private void checkRoomInfo() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.CODE_ROOM_INFO, studentId);
        receiveResponse();
    }

    private void sendRequest(byte code, String data) {
        try {
            RoomPacket packet = new RoomPacket(code, data);
            byte[] packetData = packet.getPacket();
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();
        } catch (Exception e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void receiveResponse() {
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