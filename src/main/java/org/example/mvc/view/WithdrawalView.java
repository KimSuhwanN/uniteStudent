package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 생활관 퇴사 신청을 처리하는 뷰 클래스.
 * BaseView를 상속받아 프로토콜 기반의 퇴사 신청 기능을 구현함.
 */

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

        String leaveDate = promptValidDate(); // 날짜 유효성 검증

        System.out.print("은행 이름을 입력하세요: ");
        String bankName = sc.next();

        System.out.print("계좌 번호를 입력하세요: ");
        String accountNumber = sc.next();

        // CSV 형식으로 데이터 구성 및 전송
        String requestData = String.join(",", studentId, leaveDate, bankName, accountNumber);
        sendRequest(Protocol.TYPE_WITHDRAWAL, Protocol.CODE_WITHDRAWAL_REQ, requestData);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }

    // 날짜 유효성 검증 메소드
    private String promptValidDate() {
        String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // YYYY-MM-DD 형식
        Pattern pattern = Pattern.compile(datePattern);
        String inputDate;

        while (true) {
            System.out.print("퇴사일을 입력하세요 (YYYY-MM-DD): ");
            inputDate = sc.next();

            if (pattern.matcher(inputDate).matches()) {
                return inputDate + "T00:00"; // 시간 정보 추가
            } else {
                System.out.println("유효하지 않은 날짜 형식입니다. 다시 입력해주세요 (예: 2025-01-01).");
            }
        }
    }
}