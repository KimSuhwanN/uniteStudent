package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 입사 신청 프로세스를 관리하는 뷰 클래스.
 * BaseView를 상속받아 프로토콜 기반의 신청 및 조회 기능을 구현함.
 */

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
            int choice = sc.nextInt();
            sc.nextLine(); // 버퍼 비우기
            switch (choice) {
                case 1 -> submitApplication(); // 신청서 제출
                case 2 -> checkApplicationStatus(); // 신청 상태 조회
                default -> System.out.println("1 또는 2를 입력해주세요."); // 잘못된 입력 처리
            }
        } catch (InputMismatchException e) {
            // 정수가 아닌 값 입력 시 예외 처리
            System.out.println("정수를 입력해주세요.");
            sc.nextLine(); // 버퍼 비우기
        }
    }

    // 1. 신청서 작성
    private void submitApplication() {
        // 사용자로부터 신청서 정보를 입력받음
        System.out.print("학번: ");
        String studentId = sc.nextLine();
        System.out.print("생활관 선택(푸름관1동/푸름관2동/오름관1동/오름관2동): ");
        String dormName = sc.nextLine();
        System.out.print("2인실/4인실 선택(숫자만 입력): ");
        int roomType = sc.nextInt();
        System.out.print("5일식/7일식/0일식(식사X) 선택(숫자만 입력): ");
        int mealType = sc.nextInt();
        System.out.print("지망(1 또는 2): ");
        int dormitoryPreference = sc.nextInt();

        // 입력받은 데이터를 CSV 형태로 구성
        String applicationData;
        applicationData = studentId + "," + dormName + "," + roomType + "," + mealType + "," + dormitoryPreference;
        // 서버에 신청 데이터 전송
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_SUBMIT, applicationData);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }

    // 2. 신청 상태 조회
    private void checkApplicationStatus() {
        System.out.print("조회할 학번을 입력하세요: ");
        String studentId = sc.next();
        // 서버에 조회 요청 전송
        sendRequest(Protocol.TYPE_APPLICATION, Protocol.CODE_APPLICATION_STATUS, studentId);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }
}