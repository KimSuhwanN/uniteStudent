package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
            switch (sc.nextInt()) {
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
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_AMOUNT, "");
        receiveResponse();
    }

    private void makePayment() {
        System.out.print("납부할 금액을 입력하세요: ");
        String amount = sc.next();
        sendRequest(Protocol.TYPE_PAYMENT, Protocol.CODE_PAYMENT_PAY, amount);
        receiveResponse();
    }
}