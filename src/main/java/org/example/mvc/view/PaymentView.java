package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 생활관 비용 확인 및 납부와 관련된 작업을 처리하는 뷰 클래스.
 * BaseView를 상속받아 프로토콜 기반의 납부 및 조회 기능을 구현함.
 */

public class PaymentView extends BaseView {
    private final Scanner sc;

    public PaymentView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 생활관 비용 확인 및 납부 ===");
        System.out.println("1. 납부 금액 조회");
        System.out.println("2. 비용 납부");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            int choice = sc.nextInt();
            sc.nextLine(); // 버퍼 비우기
            switch (choice) {
                case 1 -> checkPaymentAmount(); // 1. 납부 금액 조회
                case 2 -> makePayment(); // 2. 비용 납부
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (InputMismatchException e) {
            // 정수가 아닌 값 입력 시 예외 처리
            System.out.println("숫자를 입력해주세요.");
            sc.nextLine();
        }
    }

    // 1. 납부 금액 조회
    private void checkPaymentAmount() {
        // 학번 입력 및 납부 금액 조회 요청
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_AMOUNT, studentId);
        try {
            // 서버 응답 처리
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            String responseData = "";
            // 납부 금액 데이터 읽기
            if (length > 0) {
                byte[] data = new byte[length];
                in.readFully(data);
                responseData = new String(data, StandardCharsets.UTF_8);
                System.out.println("납부 금액: " + responseData + "원");
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 2. 비용 납부
    private void makePayment() {
        // 학번 입력 및 납부 확인
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();
        System.out.print("생활관비를 납부하시겠습니까? (y/n): ");
        String input = sc.next().trim().toLowerCase();

        // 잘못된 입력 처리 후 재귀 호출
        if (!"y".equals(input) && !"n".equals(input)) {
            System.out.println("y 또는 n을 입력하세요.");
            makePayment();
            return;
        }

        // 납부 상태 데이터 구성
        String paid = "y".equals(input) ? "납부" : "미납";
        String paymentData = studentId + "," + paid;
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_PAY, paymentData);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }
}