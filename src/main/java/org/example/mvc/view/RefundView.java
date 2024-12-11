package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RefundView extends BaseView {
    private final Scanner sc;

    public RefundView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 환불 확인 ===");
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_WITHDRAWAL, Protocol.CODE_WITHDRAWAL_STATUS, studentId);
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
                processRefundData(responseData);
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processRefundData(String responseData) {
        String[] parts = responseData.split(",");
        String lin = "---------------------------------------------------------------------------------";

        if (parts.length == 5) {
            String status = parts[0];
            String fee = parts[1];
            String terminationDate = parts[2];
            String bankName = parts[3];
            String accountNumber = parts[4];

            System.out.println(lin);
            System.out.printf("%-10s %-13s %-18s %-10s %-22s\n", "상태", "생활관비", "퇴사일", "은행명", "계좌번호");
            System.out.println(lin);
            System.out.printf("%-10s %-15s %-20s %-10s %-20s\n",
                    status, fee + "원", terminationDate, bankName, accountNumber);
            System.out.println(lin);
        }
    }
}