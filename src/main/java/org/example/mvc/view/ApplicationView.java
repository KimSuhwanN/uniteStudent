package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.ApplicationPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ApplicationView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public ApplicationView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 입사 신청 ===");
        System.out.println("1. 신청서 제출");
        System.out.println("2. 신청 상태 조회");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            switch (sc.nextInt()) {
                case 1 -> submitApplication();
                case 2 -> checkApplicationStatus();
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void submitApplication() {
        System.out.println("신청서 정보를 입력하세요:");
        sc.nextLine();
        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("학번: ");
        String studentId = sc.nextLine();
        System.out.print("연락처: ");
        String contact = sc.nextLine();

        String applicationData = name + "," + studentId + "," + contact;
        sendRequest(Protocol.CODE_APPLICATION_SUBMIT, applicationData);
        receiveResponse();
    }

    private void checkApplicationStatus() {
        System.out.print("조회할 학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.CODE_APPLICATION_STATUS, studentId);
        receiveResponse();
    }

    private void sendRequest(byte code, String data) {
        try {
            ApplicationPacket packet = new ApplicationPacket(code, data);
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