package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WithdrawalView extends BaseView {
    private final Scanner sc;

    public WithdrawalView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 퇴사 신청 ===");
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();

        String leaveDate = promptValidDate(); // 유효한 날짜 입력 받기

        System.out.print("은행 이름을 입력하세요: ");
        String bankName = sc.next();

        System.out.print("계좌 번호를 입력하세요: ");
        String accountNumber = sc.next();

        String requestData = String.join(",", studentId, leaveDate, bankName, accountNumber);
        sendRequest(Protocol.TYPE_WITHDRAWAL, Protocol.CODE_WITHDRAWAL_REQ, requestData);

        getResponse();
    }

    private String promptValidDate() {
        String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // YYYY-MM-DD 형식
        Pattern pattern = Pattern.compile(datePattern);
        String inputDate;

        while (true) {
            System.out.print("퇴사일을 입력하세요 (YYYY-MM-DD): ");
            inputDate = sc.next();

            if (pattern.matcher(inputDate).matches()) {
                return inputDate + "T00:00";
            } else {
                System.out.println("유효하지 않은 날짜 형식입니다. 다시 입력해주세요 (예: 2025-01-01).");
            }
        }
    }

    private void processResponse() {
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