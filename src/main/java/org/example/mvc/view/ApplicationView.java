package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        System.out.print("지망: ");
        int dormitoryPreference = sc.nextInt();

        String applicationData = studentId + "," + dormName + "," + dormitoryPreference;
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_SUBMIT, applicationData);
        receiveResponse();
    }

    private void checkApplicationStatus() {
        System.out.print("조회할 학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_STATUS, studentId);
        receiveResponse();
    }
}