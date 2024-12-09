package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ApplicationView extends BaseView {
    private final Scanner sc;

    public ApplicationView(DataInputStream in, DataOutputStream out) {
        super(in, out);
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
        System.out.print("학번: ");
        String studentId = sc.nextLine();
        System.out.print("생활관: ");
        String dormName = sc.nextLine();
        System.out.print("2인실/4인실 선택(숫자만 입력): ");
        int roomType = sc.nextInt();
        System.out.print("5일식/7일식/식사 안 함 선택(숫자만 입력): ");
        int mealType = sc.nextInt();
        System.out.print("지망: ");
        int dormitoryPreference = sc.nextInt();

        String applicationData;
        applicationData = studentId + "," + dormName + "," + roomType + "," + mealType + "," + dormitoryPreference;
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_SUBMIT, applicationData);
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

    private void checkApplicationStatus() {
        System.out.print("조회할 학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_STATUS, studentId);
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