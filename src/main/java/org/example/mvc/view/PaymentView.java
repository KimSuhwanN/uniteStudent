package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.PaymentPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class PaymentView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public PaymentView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
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
        sendRequest(Protocol.CODE_PAYMENT_AMOUNT, "");
        receiveResponse();
    }

    private void makePayment() {
        System.out.print("납부할 금액을 입력하세요: ");
        String amount = sc.next();
        sendRequest(Protocol.CODE_PAYMENT_PAY, amount);
        receiveResponse();
    }

    private void sendRequest(byte code, String data) {
        try {
            PaymentPacket packet = new PaymentPacket(code, data);
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