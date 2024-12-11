package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
            sc.nextLine();
            switch (choice) {
                case 1 -> checkPaymentAmount();
                case 2 -> makePayment();
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("숫자를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void checkPaymentAmount() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_AMOUNT, studentId);
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
                System.out.println("납부 금액: " + responseData + "원");
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void makePayment() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();
        System.out.print("생활관비를 납부하시겠습니까? (y/n): ");
        String input = sc.next().trim().toLowerCase();

        if (!"y".equals(input) && !"n".equals(input)) {
            System.out.println("y 또는 n을 입력하세요.");
            makePayment();
            return;
        }

        String paid = "y".equals(input) ? "납부" : "미납";
        String paymentData = studentId + "," + paid;
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_PAY, paymentData);

        getResponse();
    }
}