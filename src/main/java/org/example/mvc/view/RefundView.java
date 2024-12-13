package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 환불 상태 확인과 관련된 작업을 처리하는 뷰 클래스.
 * BaseView를 상속받아 환불 정보 조회 및 표시 기능을 구현함.
 */

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
        // 환불 상태 조회 요청
        sendRequest(Protocol.TYPE_WITHDRAWAL, Protocol.CODE_WITHDRAWAL_STATUS, studentId);
        try {
            // 서버 응답 처리
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            String responseData = "";
            if (length > 0) {
                // 응답 데이터 읽기
                byte[] data = new byte[length];
                in.readFully(data);
                responseData = new String(data, StandardCharsets.UTF_8);
                // 환불 데이터 처리
                processRefundData(responseData);
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 서버로부터 받은 환불 데이터를 포맷팅하여 출력하는 메소드
    private void processRefundData(String responseData) {
        // 응답 데이터를 구분자로 분리
        String[] parts = responseData.split(",");
        String lin = "---------------------------------------------------------------------------------";

        // 데이터 형식 검증 및 출력
        if (parts.length == 5) {
            String status = parts[0];
            String fee = parts[1];
            String terminationDate = parts[2];
            String bankName = parts[3];
            String accountNumber = parts[4];

            // 환불 정보 포맷팅 출력
            System.out.println(lin);
            System.out.printf("%-10s %-13s %-18s %-10s %-22s\n", "상태", "생활관비", "퇴사일", "은행명", "계좌번호");
            System.out.println(lin);
            System.out.printf("%-10s %-15s %-20s %-10s %-20s\n",
                    status, fee + "원", terminationDate, bankName, accountNumber);
            System.out.println(lin);
        }
    }
}