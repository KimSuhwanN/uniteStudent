package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.SchedulePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ScheduleView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public ScheduleView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 선발 일정 및 비용 확인 ===");
        System.out.println("1. 선발 일정 조회");
        System.out.println("2. 비용 조회");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            switch (sc.nextInt()) {
                case 1 -> viewSchedule();
                case 2 -> viewFee();
                default -> System.out.println("잘못된 입력입니다.");
            }
        } catch (InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void viewSchedule() {
        sendRequest(Protocol.CODE_SCHEDULE_VIEW, "");
        receiveResponse();
    }

    private void viewFee() {
        sendRequest(Protocol.CODE_SCHEDULE_FEE_VIEW, "");
        receiveResponse();
    }

    private void sendRequest(byte code, String data) {
        try {
            SchedulePacket packet = new SchedulePacket(code, data);
            byte[] packetData = packet.getPacket();
            // 패킷 전송
            for (byte packetDatum : packetData) out.writeByte(packetDatum);
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