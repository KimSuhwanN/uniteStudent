package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

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
        sendRequest(Protocol.CODE_WITHDRAWAL_REQ, studentId);
        receiveResponse();
    }
}